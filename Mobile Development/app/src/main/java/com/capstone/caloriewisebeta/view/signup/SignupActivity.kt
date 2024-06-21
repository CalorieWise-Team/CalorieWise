package com.capstone.caloriewisebeta.view.signup

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstone.caloriewisebeta.R
import com.capstone.caloriewisebeta.data.pref.UserPreference
import com.capstone.caloriewisebeta.data.result.Result
import com.capstone.caloriewisebeta.databinding.ActivitySignupBinding
import com.capstone.caloriewisebeta.view.ViewModelFactory
import com.capstone.caloriewisebeta.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(SESSION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)
        setupAnimation()
        setupTitle()
        setupButton()
        setupAction()
        setupAccessibility()

        // Setup gender dropdown
        val items = listOf("Male", "Female")
        val autoComplete: AutoCompleteTextView = findViewById(R.id.auto_complate)
        val adapter = ArrayAdapter(this, R.layout.list_item_gender, items)
        autoComplete.setAdapter(adapter)
        autoComplete.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val itemSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(this, "Item: $itemSelected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener { setupRegister() }
    }

    private fun setupAccessibility() {
        binding.apply {
            caloriewiseImage.contentDescription = getString(R.string.caloriewise_s_logo)
            signupTitle.contentDescription = getString(R.string.title_of_signup)
            signupDescription.contentDescription = getString(R.string.description_of_signup)
            emailEditTextLayout.contentDescription = getString(R.string.email_input_field)
            passwordEditTextLayout.contentDescription = getString(R.string.password_input_field)
            GenderEditTextLayout.contentDescription = getString(R.string.gender_input_field)
            signupButton.contentDescription = getString(R.string.sign_up_button)
            loginButton.contentDescription = getString(R.string.login_button)
        }
    }

    private fun setupAnimation() {
        ObjectAnimator.ofFloat(binding.caloriewiseImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val signupTitle = ObjectAnimator.ofFloat(binding.signupTitle, View.ALPHA, 1f).setDuration(100)
        val signupDescription = ObjectAnimator.ofFloat(binding.signupDescription, View.ALPHA, 1f).setDuration(100)
        val edEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val edPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val edName = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val edGender = ObjectAnimator.ofFloat(binding.GenderEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signupButton = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val loginButton = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(signupTitle, signupDescription, edEmail, edPassword, edName, edGender, signupButton, loginButton)
            start()
        }
    }

    private fun setupTitle() {
        val linoleumBlue = ContextCompat.getColor(this, R.color.green_main)

        val spannable = SpannableString(getString(R.string.signup_title, getString(R.string.caloriewise)))
        spannable.setSpan(
            ForegroundColorSpan(linoleumBlue),
            spannable.indexOf(getString(R.string.caloriewise)),
            spannable.indexOf(getString(R.string.caloriewise)) + getString(R.string.caloriewise).length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.signupTitle.text = spannable
    }

    private fun setupButton() {
        val linoleumBlue = ContextCompat.getColor(this, R.color.green_main)

        val spannable = SpannableString(getString(R.string.signup_to_login, getString(R.string.login_now)))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            }
        }

        val boldSpan = StyleSpan(Typeface.BOLD)
        spannable.setSpan(
            boldSpan,
            spannable.indexOf(getString(R.string.login_now)),
            spannable.indexOf(getString(R.string.login_now)) + getString(R.string.login_now).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            clickableSpan,
            spannable.indexOf(getString(R.string.login_now)),
            spannable.indexOf(getString(R.string.login_now)) + getString(R.string.login_now).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(linoleumBlue),
            spannable.indexOf(getString(R.string.login_now)),
            spannable.indexOf(getString(R.string.login_now)) + getString(R.string.login_now).length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.loginButton.text = spannable
        binding.loginButton.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupRegister() {
        val registerViewModel = obtainViewModel(this@SignupActivity)

        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val name = binding.nameEditText.text.toString().trim()
        val gender = binding.autoComplate.text.toString().trim() // Perubahan di sini

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || gender.isEmpty()) {
            showToast("All fields are required")
            return
        }

        // Tambahkan baris log di sini
        Log.d("SignupActivity", "Email: $email, Password: $password, Name: $name, Gender: $gender")

        // Pastikan urutan parameter sesuai dengan yang diharapkan oleh API
        registerViewModel.register(email, password, name, gender).observe(this@SignupActivity) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val response = result.data
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.register_success))
                        val registerMessage = getString(R.string.register_message)
                        val fullMessage = "${response.message} $registerMessage"
                        setMessage(fullMessage)
                        setPositiveButton(getString(R.string.continue_login)) { _, _ ->
                            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    showToast(result.error)
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): SignupViewModel {
        val factory = ViewModelFactory.getInstance(
            activity.application,
            UserPreference.getInstance(dataStore)
        )
        return ViewModelProvider(activity, factory)[SignupViewModel::class.java]
    }

    companion object {
        const val SESSION = "session"
    }
}
