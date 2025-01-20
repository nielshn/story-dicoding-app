package com.dicoding.storydicodingapp.ui.signup

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storydicodingapp.R
import com.dicoding.storydicodingapp.data.api.response.RegisterResponse
import com.dicoding.storydicodingapp.databinding.ActivitySignUpBinding
import com.dicoding.storydicodingapp.di.Injection
import com.dicoding.storydicodingapp.result.Result
import com.dicoding.storydicodingapp.ui.login.LoginActivity
import com.dicoding.storydicodingapp.ui.main.ViewModelFactory
import com.dicoding.storydicodingapp.utils.applyWindowInsets
import com.dicoding.storydicodingapp.utils.fadeInAnimation
import com.dicoding.storydicodingapp.utils.startBounceAnimation

class SignupActivity : AppCompatActivity() {

    // ViewModel injected with repository
    private val signupViewModel by viewModels<SignupViewModel> {
        ViewModelFactory(Injection.provideRepository(this))
    }

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        binding.root.applyWindowInsets()
        setupAction()
        setupAnimations()
        binding.textSignIn.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    // Setup UI binding
    private fun setupUI() {
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }

    // Handle actions like button clicks
    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val username = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passEditText.text.toString()

            if (areFieldsValid(username, email, password)) {
                registerUser(username, email, password)
            } else {
                showToast(getString(R.string.empty_fields_error))
            }
        }
    }

    // Check if input fields are valid
    private fun areFieldsValid(username: String, email: String, password: String): Boolean {
        return username.isNotBlank() && email.isNotBlank() && password.isNotBlank()
    }

    // Perform user registration
    private fun registerUser(username: String, email: String, password: String) {
        signupViewModel.register(username, email, password).observe(this) { result ->
            handleResult(result)
        }
    }

    //    // Handle the result of the registration process
//    private fun handleResult(result: Result<RegisterResponse>) {
//        when (result) {
//            is Result.Loading -> toggleLoading(true)
//            is Result.Success -> {
//                toggleLoading(false)
//                showSuccessDialog()
//            }
//
//            is Result.Error -> {
//                toggleLoading(false)
//                showToast(getString(R.string.signup_failed))
//            }
//        }
//    }
    private fun handleResult(result: Result<RegisterResponse>) {
        when (result) {
            is Result.Loading -> toggleLoading(true)
            is Result.Success -> {
                toggleLoading(false)
                showSuccessDialog()
            }

            is Result.Error -> {
                toggleLoading(false)
                val errorMessage = result.error
                showToast(errorMessage)
            }
        }
    }


    // Show success dialog and navigate to login
    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success_dialog_title))
            setMessage(getString(R.string.successfuly_signup_message))
            setPositiveButton(getString(R.string.next)) { _, _ -> navigateToLogin() }
            create()
            show()
        }
    }

    // Navigate to Login screen
    private fun navigateToLogin() {
        Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
        finish()
    }

    // Toggle loading indicator visibility
    private fun toggleLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    // Show toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Setup animations for UI components
    private fun setupAnimations() {
        val imageView = binding.imageView.startBounceAnimation()
        AnimatorSet().apply {
            playSequentially(
                binding.titlePageSignup.fadeInAnimation(),
                binding.nameEditTextLayout.fadeInAnimation(),
                binding.emailEditTextLayout.fadeInAnimation(),
                binding.passwordEditTextLayout.fadeInAnimation(),
                binding.signupButton.fadeInAnimation(),
                binding.tvLoginAccount.fadeInAnimation(),
                binding.textSignIn.fadeInAnimation(),
                imageView
            )
            startDelay = 100
        }.start()
    }
}
