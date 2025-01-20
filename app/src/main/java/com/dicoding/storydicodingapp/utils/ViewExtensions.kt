package com.dicoding.storydicodingapp.utils

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.animation.Animator
import android.animation.AnimatorSet
import com.dicoding.storydicodingapp.databinding.ActivityUploadStoryBinding

fun View.fadeInAnimation(duration: Long = 1000): Animator {
    return ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f).apply {
        this.duration = duration
        interpolator = AccelerateDecelerateInterpolator()
    }
}

fun View.startBounceAnimation(duration: Long = 6000): Animator {
    return ObjectAnimator.ofFloat(this, View.TRANSLATION_X, -30f, 30f).apply {
        this.duration = duration
        repeatCount = ObjectAnimator.INFINITE
        repeatMode = ObjectAnimator.REVERSE
        interpolator = AccelerateDecelerateInterpolator()
    }
}
fun ActivityUploadStoryBinding.runEntranceAnimations() {
    val bounce = ImageView.startBounceAnimation()
    val fadeCamera = cameraButton.fadeInAnimation()
    val fadeGallery = galleryButton.fadeInAnimation()
    val fadeDesc = tfDesc.fadeInAnimation()
    val fadeUpload = uploadButton.fadeInAnimation()

    AnimatorSet().apply {
        playSequentially(
            AnimatorSet().apply { playTogether(fadeCamera, fadeGallery) },
            fadeDesc,
            fadeUpload,
            bounce
        )
        start()
    }
}
