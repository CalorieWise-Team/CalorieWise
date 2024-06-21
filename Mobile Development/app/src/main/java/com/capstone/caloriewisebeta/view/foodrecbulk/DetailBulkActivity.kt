package com.capstone.caloriewisebeta.view.foodrecbulk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.data.response.BulkItem
import com.capstone.caloriewisebeta.databinding.ActivityDetailBulkBinding

class DetailBulkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBulkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBulkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val result = intent.getParcelableExtra<BulkItem>(EXTRA_RESULT)
        if (result != null) {
            setupToolbar(result)
            setupDetailFood(result)
            setupAccessibility()
        } else {
            showToast(getString(R.string.failed_to_load_data))
        }
    }

    private fun setupToolbar(item: BulkItem?) {
        with(binding) {
            if (item != null) {
                topAppBar.title = getString(R.string.m_story, item.foodName)
            }

            topAppBar.setNavigationIcon(R.drawable.ic_arrow_back)
            topAppBar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun setupAccessibility() {
        binding.apply {
            topAppBar.contentDescription = getString(R.string.navigation_and_actions)
            foodImage.contentDescription = getString(R.string.story_image)
            foodTitle.contentDescription = getString(R.string.story_name)
            foodDescription.contentDescription = getString(R.string.story_description)
        }
    }

    private fun setupDetailFood(items: BulkItem?) {
        binding.apply {
            if (items != null) {
                foodTitle.text = items.foodName ?: getString(R.string.not_available)
            }
            if (items != null) {
                foodDescription.text = items.description ?: getString(R.string.not_available)
            }
            if (items != null) {
                Glide.with(this@DetailBulkActivity)
                    .load(items.imageUrl)
                    .into(foodImage)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
    }
}