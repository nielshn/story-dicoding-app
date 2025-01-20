package com.dicoding.storydicodingapp.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.storydicodingapp.R

class PasswordCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs)
{
    init {
        setupPasswordValidation()
    }

    private fun setupPasswordValidation() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No-op
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s?.length ?: 0) < 8) {
                    setError(context.getString(R.string.pass_invalid), null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                error = if (s.isNullOrEmpty() || s.length < 8) {
                    context.getString(R.string.pass_invalid)
                } else {
                    null
                }
            }
        })
    }
}