package com.dicoding.storydicodingapp.data.local.pref

data class AuthToken (
    var token: String,
    var userId: String,
    var name: String,
    var isLoggedIn: Boolean = false
)
