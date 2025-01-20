package com.dicoding.storydicodingapp.ui.uploadStory

import android.Manifest
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storydicodingapp.R
import com.dicoding.storydicodingapp.databinding.ActivityUploadStoryBinding
import com.dicoding.storydicodingapp.di.Injection
import com.dicoding.storydicodingapp.result.Result
import com.dicoding.storydicodingapp.ui.main.MainActivity
import com.dicoding.storydicodingapp.ui.main.ViewModelFactory
import com.dicoding.storydicodingapp.utils.applyWindowInsets
import com.dicoding.storydicodingapp.utils.getImageUri
import com.dicoding.storydicodingapp.utils.isPermissionGranted
import com.dicoding.storydicodingapp.utils.reduceFileImage
import com.dicoding.storydicodingapp.utils.runEntranceAnimations
import com.dicoding.storydicodingapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class UploadStoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<UploadStoryViewModel> {
        ViewModelFactory(Injection.provideRepository(application))
    }

    private lateinit var binding: ActivityUploadStoryBinding
    private lateinit var locationClient: FusedLocationProviderClient

    private var currentImageUri: Uri? = null
    private var isLocationEnabled: Boolean = false
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupActions()
        runEntranceAnimation()
    }

    private fun setupUI() {
        enableEdgeToEdge()
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applyWindowInsets()
        supportActionBar?.apply {
            title = getString(R.string.upload_story)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        locationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupActions() {
        binding.apply {
            cameraButton.setOnClickListener { openCamera() }
            galleryButton.setOnClickListener { openGallery() }
            uploadButton.setOnClickListener { validateAndUploadStory() }
            switchLocation.setOnCheckedChangeListener { _, isChecked ->
                toggleLocation(isChecked)
            }
        }
        requestPermissions()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finishAfterTransition()
            true
        } else super.onOptionsItemSelected(item)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            showToast(getString(R.string.permission_request_denied))
        }
    }

    private fun requestPermissions() {
        val permissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        if (!permissions.all { isPermissionGranted(it) }) {
            permissionLauncher.launch(permissions)
        }
    }

    private fun toggleLocation(isChecked: Boolean) {
        isLocationEnabled = isChecked
        if (isChecked) updateLocation() else resetLocation()
    }

    private fun updateLocation() {
        if ((isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION))) {
            try {
                locationClient.lastLocation.addOnSuccessListener { location ->
                    currentLocation = location
                    Log.d(TAG, "Location: ${location?.latitude}, ${location?.longitude}")
                }.addOnFailureListener {
                    Log.e(TAG, it.message ?: getString(R.string.location_error))
                }
            } catch (e: SecurityException) {
                Log.e(TAG, e.message.toString())
            }
        } else {
            requestPermissions()
        }
    }

    private fun resetLocation() {
        currentLocation = null
    }

    private fun openCamera() {
        currentImageUri = getImageUri(this)
        currentImageUri?.let { cameraLauncher.launch(it) }
    }


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                showImage()
            }else {
                showToast(getString(R.string.message_no_image_captured))
                currentImageUri = null
            }
        }

    private fun openGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        currentImageUri = uri
        showImage()
    }

    private fun runEntranceAnimation() {
        binding.runEntranceAnimations()
    }

    private fun validateAndUploadStory() {
        val description = binding.edtDesc.text.toString()
        if (currentImageUri != null && description.isNotEmpty()) {
            uploadStory(description)
        } else {
            showToast(getString(R.string.message_insert_image))
        }
    }

    private fun uploadStory(description: String) {
        val imageFile = currentImageUri?.let { uriToFile(it, this).reduceFileImage() }
        val (latitude, longitude) = getLocationCoordinates()

        imageFile?.let { file ->
            viewModel.uploadStory(description, file, latitude, longitude).observe(this) { result ->
                handleUploadResult(result)
            }
        }
    }

    private fun handleUploadResult(result: Result<*>) {
        when (result) {
            is Result.Loading -> toggleLoading(true)
            is Result.Success -> {
                showToast(getString(R.string.upload_story))
                navigateToMain()
            }

            is Result.Error -> {
                showToast(result.error)
                toggleLoading(false)
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }

    private fun showImage() {
        binding.ImageView.setImageURI(currentImageUri)
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun getLocationCoordinates(): Pair<Double?, Double?> {
        return currentLocation?.let { it.latitude to it.longitude } ?: (null to null)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "LocationError"
    }
}