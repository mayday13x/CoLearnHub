package com.example.colearnhub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.colearnhub.modelLayer.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseTestAuth : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var buttonSignUp: Button
    private lateinit var textViewLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_supabase_test_auth)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        textViewLogin = findViewById(R.id.textViewLogin)
    }

    private fun setupClickListeners() {
        buttonSignUp.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            if (validateInput(name, email, password, confirmPassword)) {
                signUpUser(name, email, password)
            }
        }

        textViewLogin.setOnClickListener {
            // Navegar de volta para tela de login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): Boolean {
        // Validar nome
        if (name.isEmpty()) {
            editTextName.error = "Nome é obrigatório"
            editTextName.requestFocus()
            return false
        }

        if (name.length < 2) {
            editTextName.error = "Nome deve ter pelo menos 2 caracteres"
            editTextName.requestFocus()
            return false
        }

        // Validar email
        if (email.isEmpty()) {
            editTextEmail.error = "Email é obrigatório"
            editTextEmail.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Email inválido"
            editTextEmail.requestFocus()
            return false
        }

        // Validar senha
        if (password.isEmpty()) {
            editTextPassword.error = "Palavra-passe é obrigatória"
            editTextPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            editTextPassword.error = "Palavra-passe deve ter pelo menos 6 caracteres"
            editTextPassword.requestFocus()
            return false
        }

        // Validar confirmação de senha
        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.error = "Confirmação de palavra-passe é obrigatória"
            editTextConfirmPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            editTextConfirmPassword.error = "Palavras-passe não coincidem"
            editTextConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    private fun signUpUser(name: String, email: String, password: String) {
        buttonSignUp.isEnabled = false
        buttonSignUp.text = "Criando conta..."

        lifecycleScope.launch {
            try {
                // Criar usuário no Supabase Auth
                val result = SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    this.data = buildJsonObject {
                        put("full_name", name)
                        put("display_name", name)
                    }
                }

                runOnUiThread {
                    Toast.makeText(
                        this@SupabaseTestAuth,
                        "Conta criada com sucesso! Verifique seu email para confirmar.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Navegar para próxima tela ou login
                    val intent = Intent(this@SupabaseTestAuth, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                print("Resultado $result")

            } catch (e: Exception) {
                runOnUiThread {
                    val errorMessage = when {
                        e.message?.contains("already registered") == true ->
                            "Este email já está registado"
                        e.message?.contains("invalid email") == true ->
                            "Email inválido"
                        e.message?.contains("weak password") == true ->
                            "Palavra-passe muito fraca"
                        else -> "Erro ao criar conta: ${e.message}"
                    }

                    Toast.makeText(this@SupabaseTestAuth, errorMessage, Toast.LENGTH_LONG).show()
                    resetButton()
                }
            }
        }
    }

    private fun resetButton() {
        buttonSignUp.isEnabled = true
        buttonSignUp.text = "Criar Conta"
    }
}