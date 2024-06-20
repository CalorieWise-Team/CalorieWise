import os
import numpy as np
import tensorflow as tf
from flask import Flask, request, jsonify
from tensorflow.keras.preprocessing.image import img_to_array
from PIL import Image
import io
import requests

# Flask API
app = Flask(__name__)

# Load your trained model
model_path = 'models.h5'
try:
    assert os.path.exists(model_path), f"Model file not found at {model_path}"
    model = tf.keras.models.load_model(model_path)
    model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])
    print("Model successfully loaded.")
except Exception as e:
    print(f"Failed to load model: {str(e)}")
    model = None

# Define class labels with food_id
class_labels = {
    0: {'label': 'Bakso', 'food_id': 64},
    1: {'label': 'Burger', 'food_id': 91},
    2: {'label': 'Caesar Salad', 'food_id': 1349},
    3: {'label': 'Cumi Goreng Tepung', 'food_id': 222},
    4: {'label': 'Kerang Tiram', 'food_id': 718},
    5: {'label': 'Nasi', 'food_id': 921},
    6: {'label': 'Nasi Goreng', 'food_id': 924},
    7: {'label': 'Omelette', 'food_id': 1219},
    8: {'label': 'Sate Ayam', 'food_id': 1347},
    9: {'label': 'Sayap Ayam Goreng', 'food_id': 31},
    10: {'label': 'Siomay', 'food_id': 1141},
    11: {'label': 'Spaghetti', 'food_id': 1165},
    12: {'label': 'Steak', 'food_id': 232},
    14: {'label': 'Yoghurt', 'food_id': 1346}
}

def preprocess_image(img):
    img = img.resize((224, 224))  # Resize image to target size
    img = img.convert('RGB')  # Convert to RGB mode (eliminates alpha channel)
    img_array = img_to_array(img)
    img_array = tf.keras.applications.mobilenet_v2.preprocess_input(img_array)
    img_array = np.expand_dims(img_array, axis=0)
    return img_array

def predict_image(img):
    img_array = preprocess_image(img)
    prediction = model.predict(img_array)
    predicted_label_index = np.argmax(prediction)
    accuracy = np.max(prediction)
    
    # Check if the predicted label index exists in class_labels
    if predicted_label_index in class_labels:
        predicted_class = class_labels[predicted_label_index]['label']
        food_id = class_labels[predicted_label_index]['food_id']
        return int(predicted_label_index), predicted_class, float(accuracy), food_id
    else:
        return None, None, None, None

@app.route("/")
def index():
    return jsonify({
        "status": {
            "code": 200,
            "message": "Success fetching the API",
        },
        "data": None
    }), 200

@app.route('/predict', methods=['POST'])
def predict():
    try:
        if 'photo' not in request.files:
            return jsonify({"error": "No file part in the request"}), 400

        file = request.files['photo']

        if file.filename == '':
            return jsonify({"error": "No selected file"}), 400
        
        # Load image from file in memory
        img = Image.open(io.BytesIO(file.read()))
        
        # Preprocess and predict image
        predicted_label_index, predicted_class, accuracy, food_id = predict_image(img)
        
        # Print image dimensions
        img_width, img_height = img.size
        print(f"Uploaded image dimensions: {img_width}x{img_height}")
        
        # Check prediction accuracy
        if predicted_label_index is not None:
            if 0.39 <= accuracy <= 1:
                # Make request to the next endpoint
                response = requests.get(f"https://api-backend-dot-caloriewise-425017.et.r.appspot.com/views/food/{food_id}")
                data = response.json()
                if response.status_code == 200:
                    return jsonify({
                        'status' : 200,
                        'error': False,
                        'message': "Model is predicted successfully.",
                        'confidenceScore': accuracy*100,
                        'isAboveThreshold': True,
                        'data': data['data']
                    }), 200
                else:
                    return jsonify({
                        'error': True,
                        'message': 'Failed to get food information',
                        'food_info': None
                    }), 500
            else:
                return jsonify({
                    'error': True,
                    'message': 'Prediction accuracy is too low, Cannot Detect the Image',
                    'accuracy': accuracy
                }), 400
        else:
            return jsonify({
                'error': True,
                'message': 'Prediction failed. Class label not found.',
                'accuracy': 0
            }), 404

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
