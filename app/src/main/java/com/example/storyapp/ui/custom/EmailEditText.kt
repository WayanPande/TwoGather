package com.example.storyapp.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyapp.R

class EmailEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    private fun init() {

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches() && s.toString()
                        .isNotEmpty()
                ) {
                    showErrorIcon()
                    setBackgroundResource(R.drawable.error_edt)
                } else {
                    hideErrorIcon()
                    setBackgroundResource(R.drawable.custom_input)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    private fun showErrorIcon() {
        error = "Not a valid email!"
    }

    private fun hideErrorIcon() {
        error = null
    }

}