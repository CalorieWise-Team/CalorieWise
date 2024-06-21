package com.capstone.caloriewisebeta.view.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.capstone.caloriewisebeta.R
import com.google.android.material.textfield.TextInputEditText

class EmailCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    private val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                error = if (email.isNotEmpty() && !email.matches(emailRegex)) {
                    context.getString(R.string.invalid_email_format)
                } else {
                    null
                }
            }
        })
    }
}