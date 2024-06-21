package com.capstone.caloriewisebeta.view.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.data.response.NotesItem
import com.capstone.caloriewisebeta.data.response.SearchItem
import com.capstone.caloriewisebeta.data.result.Result
import com.capstone.caloriewisebeta.databinding.ActivityDetailNoteBinding
import com.capstone.caloriewisebeta.databinding.ActivityDetailSearchBinding
import com.capstone.caloriewisebeta.helper.uriToFile
import com.capstone.caloriewisebeta.helper.withDateFormat
import com.capstone.caloriewisebeta.view.note.AddNoteViewModel

class DetailSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSearchBinding
    private var originalItem: SearchItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val result = intent.getParcelableExtra<SearchItem>(EXTRA_RESULT)
        if (result != null) {
            originalItem = result
            setupToolbar(result)
            setupDetailStory(result)
            setupAccessibility()
            setupMultiplierWatcher()
        } else {
            showToast(getString(R.string.failed_to_load_data))
        }
    }

    private fun setupToolbar(item: SearchItem) {
        with(binding) {
            topAppBar.title = getString(R.string.m_story, item.name)

            topAppBar.setNavigationIcon(R.drawable.ic_arrow_back)
            topAppBar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun setupAccessibility() {
        binding.apply {
            topAppBar.contentDescription = getString(R.string.navigation_and_actions)
            imageViewSearch.contentDescription = getString(R.string.search_image)
            storyTitlesSearch.contentDescription = getString(R.string.makanan)
            calorieLabelSearch.contentDescription = getString(R.string.calorie)
            calorieValueSearch.contentDescription = getString(R.string.hasil)
            fatLabelSearch.contentDescription = getString(R.string.fat)
            fatValueSearch.contentDescription = getString(R.string.value_fat)
            carbsLabelSearch.contentDescription = getString(R.string.carbs_label)
            carbsValueSearch.contentDescription = getString(R.string.hasil)
            protLabelSearch.contentDescription = getString(R.string.protein)
            protValueSearch.contentDescription = getString(R.string.value_prot)
            plusEditTextLayout.contentDescription = getString(R.string.plus_edit)
            btnPlusSearch.contentDescription = getString(R.string.save)
        }
    }

    private fun setupMultiplierWatcher() {
        binding.plusEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val multiplier = s?.toString()?.toIntOrNull() ?: 1
                updateNutritionalValues(multiplier)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateNutritionalValues(multiplier: Int) {
        originalItem?.let { items ->
            binding.apply {
                calorieValueSearch.text = (items.calories?.times(multiplier))?.toString() ?: getString(R.string.not_available)
                fatValueSearch.text = (items.fat?.times(multiplier))?.toString() ?: getString(R.string.not_available)
                carbsValueSearch.text = (items.carbohydrate?.times(multiplier))?.toString() ?: getString(R.string.not_available)
                protValueSearch.text = (items.proteins?.times(multiplier))?.toString() ?: getString(R.string.not_available)
            }
        }
    }

    private fun setupDetailStory(items: SearchItem) {
        binding.apply {
            storyTitlesSearch.text = items.name ?: getString(R.string.not_available)
            calorieValueSearch.text = items.calories?.toString() ?: getString(R.string.not_available)
            fatValueSearch.text = items.fat?.toString() ?: getString(R.string.not_available)
            carbsValueSearch.text = items.carbohydrate?.toString() ?: getString(R.string.not_available)
            protValueSearch.text = items.proteins?.toString() ?: getString(R.string.not_available)
            Glide.with(this@DetailSearchActivity)
                .load(items.imageUrl)
                .into(imageViewSearch)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
    }
}