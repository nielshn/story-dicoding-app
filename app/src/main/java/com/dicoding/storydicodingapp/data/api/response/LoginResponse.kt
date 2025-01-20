package com.dicoding.storydicodingapp.data.api.response

import com.dicoding.storydicodingapp.data.local.pref.AuthToken
import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("loginResult")
	val loginResult: LoginResult,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class LoginResult(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("token")
	val token: String
)
fun LoginResponse.toAuthToken(): AuthToken {
	return AuthToken(
		token = this.loginResult.token,
		userId = this.loginResult.userId,
		name = this.loginResult.name,
		isLoggedIn = true
	)
}
