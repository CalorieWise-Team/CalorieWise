package com.capstone.caloriewisebeta.view.note

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.data.response.NotesItem
import com.capstone.caloriewisebeta.databinding.ActivityDetailNoteBinding
import com.capstone.caloriewisebeta.helper.withDateFormat

class DetailNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val result = intent.getParcelableExtra<NotesItem>(EXTRA_RESULT)
        if (result != null) {
            setupToolbar(result)
            setupDetailStory(result)
            setupAccessibility()
        } else {
            showToast(getString(R.string.failed_to_load_data))
        }
    }

    private fun setupToolbar(item: NotesItem) {
        with(binding) {
            topAppBar.title = getString(R.string.s_story, item.title)

            topAppBar.setNavigationIcon(R.drawable.ic_arrow_back)
            topAppBar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun setupAccessibility() {
        binding.apply {
            topAppBar.contentDescription = getString(R.string.navigation_and_actions)
            storyImage.contentDescription = getString(R.string.story_image)
            storyTitle.contentDescription = getString(R.string.story_name)
            storyDate.contentDescription = getString(R.string.story_date)
            storyDescription.contentDescription = getString(R.string.story_description)
        }
    }

    private fun setupDetailStory(items: NotesItem) {
        binding.apply {
            storyTitle.text = items.title ?: getString(R.string.not_available)
            storyDate.text = items.createdAt?.withDateFormat() ?: getString(R.string.not_available)
            storyDescription.text = items.content ?: getString(R.string.not_available)
            Glide.with(this@DetailNoteActivity)
                .load(items.imageUrl)
                .into(storyImage)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
    }
}