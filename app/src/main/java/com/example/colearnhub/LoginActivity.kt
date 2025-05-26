package com.example.colearnhub

import android.content.Intent
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

        val fullText = getString(R.string.signup_prompt)
        val highlightText = getString(R.string.signup_highlight)

        val startIndex = fullText.indexOf(highlightText)
        val endIndex = startIndex + highlightText.length

        val spannable = SpannableString(fullText)
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#526C84")),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        signUpText.text = spannable

        signUpText.text = spannable
        signUpText.setOnClickListener {
            val intent = Intent(this, SupabaseTestAuth::class.java)
            startActivity(intent)
        }

        val passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)


    }
}
