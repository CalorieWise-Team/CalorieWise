package com.capstone.caloriewisebeta.view.note

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.result.Result
import com.capstone.caloriewisebeta.databinding.ActivityAddNoteBinding
import com.capstone.caloriewisebeta.helper.uriToFile
import com.capstone.caloriewisebeta.view.ViewModelFactory
import com.capstone.caloriewisebeta.view.camera.CameraActivity
import com.capstone.caloriewisebeta.view.camera.CameraActivity.Companion.CAMERAX_RESULT

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding

    private lateinit var storyViewModel: AddNoteViewModel

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(SESSION)

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast(getString(R.string.permission_request_granted))
            } else {
                showToast(getString(R.string.permission_request_denied))
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        storyViewModel = obtainViewModel(this@AddNoteActivity)

        showLoading(false)
        setupToolbar()
        setupAction()
        setupAccessibility()
    }

    private fun setupToolbar() {
        with(binding) {
            topAppBar.title = getString(R.string.add_new_story)

            topAppBar.setNavigationIcon(R.drawable.ic_arrow_back)
            topAppBar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCameraX() }
        binding.uploadButton.setOnClickListener { uploadStory() }
    }

    private fun setupAccessibility() {
        binding.apply {
            topAppBar.contentDescription = getString(R.string.navigation_and_actions)
            previewImageView.contentDescription = getString(R.string.preview_of_selected_image)
            galleryButton.contentDescription = getString(R.string.open_gallery_to_select_image)
            cameraButton.contentDescription = getString(R.string.take_photo_with_camera)
            descriptionEditText.contentDescription = getString(R.string.enter_description_for_the_image)
            uploadButton.contentDescription = getString(R.string.upload_the_selected_image)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", getString(R.string.no_media_selected))
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun uploadStory() {
        if (currentImageUri == null) {
            showToast(getString(R.string.please_select_an_image_first))
        }

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this)
            val contentBody = binding.descriptionEditText.text.toString()
            val titleBody = binding.titleEditText.text.toString()

            storyViewModel.uploadNewNote(imageFile, contentBody, titleBody).observe(this) {
                if (it != null) {
                    when (it) {
                        is com.capstone.caloriewisebeta.data.result.Result.Loading -> {
                            showLoading(true)
                        }
                        is com.capstone.caloriewisebeta.data.result.Result.Success -> {
                            showLoading(false)

                            val response = it.data
                            showToast(response.message.toString())
                            finish()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showToast(it.error)
                        }
                    }
                }
            }
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): AddNoteViewModel {
        val factory = ViewModelFactory.getInstance(
            activity.application,
            UserPreference.getInstance(dataStore)
        )
        return ViewModelProvider(activity, factory)[AddNoteViewModel::class.java]
    }

    companion object {
        const val SESSION = "session"
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}