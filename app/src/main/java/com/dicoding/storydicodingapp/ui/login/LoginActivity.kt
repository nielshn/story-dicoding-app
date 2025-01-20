package com.dicoding.storydicodingapp.ui.login

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storydicodingapp.R
import com.dicoding.storydicodingapp.data.api.response.LoginResponse
import com.dicoding.storydicodingapp.data.api.response.toAuthToken
import com.dicoding.storydicodingapp.databinding.ActivityLoginBinding
import com.dicoding.storydicodingapp.result.Result
import com.dicoding.storydicodingapp.ui.main.MainActivity
import com.dicoding.storydicodingapp.ui.main.ViewModelFactory
import com.dicoding.storydicodingapp.ui.signup.SignupActivity
import com.dicoding.storydicodingapp.utils.applyWindowInsets
import com.dicoding.storydicodingapp.utils.fadeInAnimation
import com.dicoding.storydicodingapp.utils.startBounceAnimation

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(application)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeActions()
    }

    private fun setupUI() {
        binding.root.applyWindowInsets()
        setupAnimations()
        supportActionBar?.hide()
    }

    private fun observeActions() {
        binding.btnSignIn.setOnClickListener { handleLogin() }
        binding.textSignUp.setOnClickListener { navigateToSignUp() }
    }

    private fun handleLogin() {
        val email = binding.emailEditTextLayout.editText?.text.toString().trim()
        val password = binding.passEditTextLayout.editText?.text.toString().trim()

        if (email.isBlank() || password.isBlank()) {
            showToast(getString(R.string.empty_fields_error))
            return
        }

        login(email, password)
    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password).observe(this) { result ->
            when (result) {
                is Result.Loading -> setLoadingState(true)
                is Result.Success -> {
                    setLoadingState(false)
                    handleSuccess(result.data)
                }

                is Result.Error -> {
                    setLoadingState(false)
                    handleError(result.error)
                }
            }
        }
    }

    private fun handleSuccess(response: LoginResponse) {
        val userToken = response.toAuthToken()
        viewModel.saveUserSession(userToken)
        navigateToMain()
    }

    private fun handleError(errorMessage: String) {
        Log.e("LoginActivity", "Login error: $errorMessage")
        showToast(errorMessage)
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigateToSignUp() {
        startActivity(Intent(this, SignupActivity::class.java))
    }

    private fun navigateToMain() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.success))
            .setMessage(getString(R.string.login_success_message))
            .setPositiveButton(getString(R.string.okay)) { _, _ ->
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupAnimations() {
        val imageBounce = binding.imageView.startBounceAnimation()

        val animations = listOf(
            binding.titleTextView.fadeInAnimation(),
            binding.emailEditTextLayout.fadeInAnimation(),
            binding.passEditTextLayout.fadeInAnimation(),
            binding.btnSignIn.fadeInAnimation(),
            binding.textSignUp.fadeInAnimation(),
            binding.tvSignUp.fadeInAnimation(),
            imageBounce
        )

        AnimatorSet().apply {
            playSequentially(animations)
            startDelay = 100
        }.start()
    }

}
