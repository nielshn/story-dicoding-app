package com.dicoding.storydicodingapp.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.storydicodingapp.R
import com.dicoding.storydicodingapp.data.api.response.StoryResponse
import com.dicoding.storydicodingapp.databinding.ActivityMapsBinding
import com.dicoding.storydicodingapp.di.Injection
import com.dicoding.storydicodingapp.result.Result
import com.dicoding.storydicodingapp.ui.main.ViewModelFactory
import com.dicoding.storydicodingapp.utils.MapHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory(Injection.provideRepository(applicationContext))
    }

//    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMapFragment()

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        MapHelper.setupMap(mMap, this)
        getMyLocation()

        viewModel.getStoryLocation().observe(this) { result ->
            handleStoryLocationsResult(result)
        }
    }

    private fun handleStoryLocationsResult(result: Result<StoryResponse>) {
        when (result) {
            is Result.Loading -> showToast(getString(R.string.loading))
            is Result.Success -> MapHelper.addMarkers(mMap, result.data.listStory)
            is Result.Error -> Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}