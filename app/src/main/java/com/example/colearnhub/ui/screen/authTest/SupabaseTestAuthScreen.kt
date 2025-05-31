package com.example.colearnhub.ui.screen.authTest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.colearnhub.modelLayer.SupabaseClient
import com.example.colearnhub.viewmodel.AuthViewModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Estados de erro
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Título
            Text(
                text = "Criar Conta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo Nome
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Nome completo") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(4.dp),
                    singleLine = true
                )

                if (nameError != null) {
                    Text(
                        text = nameError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Email
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(4.dp),
                    singleLine = true
                )

                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Senha
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    label = { Text("Palavra-passe") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(4.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    singleLine = true
                )

                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Confirmar Senha
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = null
                    },
                    label = { Text("Confirmar palavra-passe") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPasswordError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(4.dp),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (confirmPasswordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    singleLine = true
                )

                if (confirmPasswordError != null) {
                    Text(
                        text = confirmPasswordError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Criar Conta
            Button(
                onClick = {
                    // Validação
                    var hasError = false

                    if (name.isEmpty()) {
                        nameError = "Nome é obrigatório"
                        hasError = true
                    } else if (name.length < 2) {
                        nameError = "Nome deve ter pelo menos 2 caracteres"
                        hasError = true
                    }

                    if (email.isEmpty()) {
                        emailError = "Email é obrigatório"
                        hasError = true
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Email inválido"
                        hasError = true
                    }

                    if (password.isEmpty()) {
                        passwordError = "Palavra-passe é obrigatória"
                        hasError = true
                    } else if (password.length < 6) {
                        passwordError = "Palavra-passe deve ter pelo menos 6 caracteres"
                        hasError = true
                    }

                    if (confirmPassword.isEmpty()) {
                        confirmPasswordError = "Confirmação de palavra-passe é obrigatória"
                        hasError = true
                    } else if (password != confirmPassword) {
                        confirmPasswordError = "Palavras-passe não coincidem"
                        hasError = true
                    }

                    if (!hasError) {
                        isLoading = true
                        scope.launch {
                            try {
                                // Criar usuário no Supabase
                                SupabaseClient.client.auth.signUpWith(Email) {
                                    this.email = email
                                    this.password = password
                                    this.data = buildJsonObject {
                                        put("full_name", name)
                                        put("display_name", name)
                                    }
                                }

                                // Sucesso
                                snackbarHostState.showSnackbar(
                                    "Conta criada com sucesso! Verifique seu email.",
                                    duration = SnackbarDuration.Long
                                )

                                // Navegar de volta para login após sucesso
                                navController.popBackStack()

                            } catch (e: Exception) {
                                val errorMessage = when {
                                    e.message?.contains("already registered", ignoreCase = true) == true ->
                                        "Este email já está registado"
                                    e.message?.contains("invalid email", ignoreCase = true) == true ->
                                        "Email inválido"
                                    e.message?.contains("weak password", ignoreCase = true) == true ->
                                        "Palavra-passe muito fraca"
                                    else -> "Erro ao criar conta: ${e.message}"
                                }
                                snackbarHostState.showSnackbar(
                                    errorMessage,
                                    duration = SnackbarDuration.Long
                                )
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF395174),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Criar Conta", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link para Login
            Text(
                text = "Já tem conta? Fazer login",
                color = Color(0xFF526C84),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable {
                        navController.popBackStack()
                    }
                    .padding(8.dp)
            )
        }
    }
}