package com.capstone.caloriewisebeta.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.result.Result
import com.capstone.caloriewisebeta.databinding.ActivityLoginBinding
import com.capstone.caloriewisebeta.view.ViewModelFactory
import com.capstone.caloriewisebeta.view.main.MainActivity
import com.capstone.caloriewisebeta.view.main.SearchFragment
import com.capstone.caloriewisebeta.view.signup.SignupActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var userPreference: UserPreference

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(SESSION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(dataStore)
        lifecycleScope.launch {
            try {
                userPreference.isLoggedIn().collect { isLoggedIn ->
                    if (isLoggedIn == true) {
                        moveToMain()
                    } else {
                        showLoading(false)
                        setupAnimation()
                        setupTitle()
                        setupButton()
                        setupAction()
                        setupAccessibility()
                    }
                }
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error in collecting user login status", e)
            }
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener { setupLogin() }
    }

    private fun setupAccessibility() {
        binding.apply {
            caloriewiseImage.contentDescription = getString(R.string.caloriewise_s_logo)
            loginTitle.contentDescription = getString(R.string.title_of_login)
            loginDescription.contentDescription = getString(R.string.description_of_login)
            emailEditTextLayout.contentDescription = getString(R.string.email_input_field)
            passwordEditTextLayout.contentDescription = getString(R.string.password_input_field)
            loginButton.contentDescription = getString(R.string.login_button)
            registerButton.contentDescription = getString(R.string.register_button)
        }
    }

    private fun setupAnimation() {
        ObjectAnimator.ofFloat(binding.caloriewiseImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val loginTitle = ObjectAnimator.ofFloat(binding.loginTitle, View.ALPHA, 1f).setDuration(100)
        val loginDescription = ObjectAnimator.ofFloat(binding.loginDescription, View.ALPHA, 1f).setDuration(100)
        val edEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val edPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val loginButton = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val registerButton = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(loginTitle, loginDescription, edEmail, edPassword, loginButton, registerButton)
            start()
        }
    }

    private fun setupTitle() {
        val linoleumBlue = ContextCompat.getColor(this, R.color.green_main)

        val loginTitle = getString(R.string.login_title)
        val dicodingStory = getString(R.string.caloriewise)

        val spannable = SpannableString("$loginTitle $dicodingStory")
        spannable.setSpan(
            ForegroundColorSpan(linoleumBlue),
            loginTitle.length + 1,
            loginTitle.length + 1 + dicodingStory.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.loginTitle.text = spannable
    }

    private fun setupButton() {
        val linoleumBlue = ContextCompat.getColor(this, R.color.green_main)

        val spannable = SpannableString(getString(R.string.login_to_signup, getString(R.string.signup_now)))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            }
        }

        val boldSpan = StyleSpan(Typeface.BOLD)
        spannable.setSpan(
            boldSpan,
            spannable.indexOf(getString(R.string.signup_now)),
            spannable.indexOf(getString(R.string.signup_now)) + getString(R.string.signup_now).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            clickableSpan,
            spannable.indexOf(getString(R.string.signup_now)),
            spannable.indexOf(getString(R.string.signup_now)) + getString(R.string.signup_now).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(linoleumBlue),
            spannable.indexOf(getString(R.string.signup_now)),
            spannable.indexOf(getString(R.string.signup_now)) + getString(R.string.signup_now).length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.registerButton.text = spannable
        binding.registerButton.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupLogin() {
        val loginViewModel = obtainViewModel(this@LoginActivity)

        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        when {
            email.isEmpty() -> {
                binding.emailEditText.error = getString(R.string.empty_email)
            }
            password.isEmpty() -> {
                binding.passwordEditText.error = getString(R.string.empty_password)
            }
            else -> {
                loginViewModel.login(email, password).observe(this@LoginActivity) {
                    if (it != null) {
                        when (it) {
                            is com.capstone.caloriewisebeta.data.result.Result.Loading -> {
                                showLoading(true)
                            }
                            is com.capstone.caloriewisebeta.data.result.Result.Success -> {
                                showLoading(false)

                                val response = it.data

                                loginViewModel.saveLoginState(response.token.toString())
                                showToast(getString(R.string.successfully_logged_in))

                                val intent = Intent(this@LoginActivity, SearchFragment::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
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
    }

    private fun moveToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): LoginViewModel {
        val factory = ViewModelFactory.getInstance(
            activity.application,
            UserPreference.getInstance(dataStore)
        )
        return ViewModelProvider(activity, factory)[LoginViewModel::class.java]
    }

    companion object {
        const val SESSION = "session"
    }
}