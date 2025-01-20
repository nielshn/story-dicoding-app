package com.dicoding.storydicodingapp.utils

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.dicoding.storydicodingapp.R
import com.dicoding.storydicodingapp.data.api.response.ListStoryItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

object MapHelper {
    fun setupMap(map: GoogleMap, context: Context) {
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
        setMapStyle(map, context)
    }

    private fun setMapStyle(map: GoogleMap, context: Context) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: $e")
        }
    }

    fun addMarkers(map: GoogleMap, stories: List<ListStoryItem>) {
        stories.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            val marker = MarkerOptions()
                .position(latLng)
                .title(story.name)
                .snippet(story.description)
            map.addMarker(marker)
        }
    }

    private const val TAG = "MapHelper"
}
