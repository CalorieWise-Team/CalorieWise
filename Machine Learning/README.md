## CalorieWise ML Documentation
We developed a highly efficient food image classification model using custom convolutional neural networks (CNN) combined with transfer learning using VGG16. Utilizing the VGG16 architecture, a well-known and highly effective deep learning model, we significantly enhanced our model's performance. We trained this model with thousands of food images across 14 class labels, achieving an accuracy rate of over 80%. This ensures our application can reliably provide users with precise nutritional information based on their uploaded food images.

## Datasets
This is our [Dataset](https://drive.google.com/drive/folders/1gVJCdNCKionhBdR5HBitFLLD7O0DSGHj?usp=sharing)
For the datasets, we search from various sources on Kaggle and merge them together. So that 14 labels are collected, which are:
1. Bakso
2. Burger
3. Caesar Salad
4. Cumi Goreng Tepung
5. Kerang Tiram
6. Nasi
7. Nasi goreng
8. Omelette
9. Sate Ayam
10. Sayap Ayam Goreng
11. Siomay
12. Spaghetti
13. Steak
14. Yoghurt

## Model
This is our [Model](https://drive.google.com/drive/folders/1gVJCdNCKionhBdR5HBitFLLD7O0DSGHj?usp=sharing)

'''json
model = models.Sequential([
    base_model,
    layers.Flatten(),
    layers.Dense(512, activation='relu', kernel_regularizer=regularizers.l2(0.001)),
    layers.Dropout(0.6),
    layers.Dense(train_dataset.num_classes, activation='softmax')
])

The model architecture using transfer learning with a pre-trained VGG16 model as a feature extractor, combined with custom layers for classification. Below is a brief explanation for our Image Classification Model Architecture:
