package com.example.colearnhub

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signUpText = findViewById<TextView>(R.id.SignUp)
        val fullText = "New to CoLearnHub? Sign Up"
        val spannable = SpannableString(fullText)

        val startIndex = fullText.indexOf("Sign Up")
        val endIndex = startIndex + "Sign Up".length

        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#526C84")),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        signUpText.text = spannable


        val passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)


    }
}
