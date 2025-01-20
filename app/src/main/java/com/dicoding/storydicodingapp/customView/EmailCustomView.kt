package com.dicoding.storydicodingapp.customView

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.storydicodingapp.R

class EmailCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs)
{
    init {
        setupValidation()
    }

    private fun setupValidation() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before text change
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!Patterns.EMAIL_ADDRESS.matcher(s!!).matches()) {
                    error = context.getString(R.string.email_invalid)
                } else {
                    error = null
                    setTextColor(Color.BLACK)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // After text change
                if (s.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    setTextColor(Color.RED)
                } else {
                    setTextColor(Color.BLACK)
                }
            }
        })
    }
}