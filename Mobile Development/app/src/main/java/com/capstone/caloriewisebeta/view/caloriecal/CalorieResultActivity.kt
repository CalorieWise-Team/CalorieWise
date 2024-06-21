package com.capstone.caloriewisebeta.view.caloriecal

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.data.response.PredictResponse
import com.capstone.caloriewisebeta.databinding.ActivityCalorieResultBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CalorieResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalorieResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalorieResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan JSON string dari Intent
        val jsonString = intent.getStringExtra("RESULT_DATA_JSON")

        // Mengonversi JSON string menjadi objek PredictResponse
        val gson = Gson()
        val type = object : TypeToken<PredictResponse>() {}.type
        val resultData: PredictResponse = gson.fromJson(jsonString, type)

        // Menampilkan hasil klasifikasi di TextView
        val resultText = generateResultText(resultData)
        binding.resultText.text = resultText

        // Menampilkan gambar di ImageView jika URI tidak null
        val imageUriString = intent.getStringExtra("IMAGE_URI")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            binding.resultImage.setImageURI(imageUri)
        } else {
            // Handle jika URI null, misalnya dengan menampilkan placeholder atau pesan error
            binding.resultImage.setImageResource(R.drawable.ic_place_holder)
        }
    }

    private fun generateResultText(resultData: PredictResponse): String {
        // Ubah objek resultData menjadi teks sesuai kebutuhan aplikasi Anda
        val dataItem = resultData.data?.firstOrNull()
        return if (dataItem != null) {
            "Nama Makanan: ${dataItem.name}\n" +
                    "Kalori: ${dataItem.calories}\n" +
                    "Protein: ${dataItem.proteins}\n" +
                    "Lemak: ${dataItem.fat}\n" +
                    "Karbohidrat: ${dataItem.carbohydrate}\n" +
                    "Confidence Score: ${resultData.confidenceScore}"
        } else {
            "Data tidak tersedia"
        }
    }
}
