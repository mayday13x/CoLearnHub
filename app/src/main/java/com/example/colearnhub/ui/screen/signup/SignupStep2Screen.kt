package com.example.colearnhub.ui.screen.signup

import BackButton
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.colearnhub.R
import com.example.colearnhub.data.SignupData
import com.example.colearnhub.repository.AuthRepository
import com.example.colearnhub.repository.UserRepository
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignupStep2Screen(
    navController: NavController,
    signupData: SignupData
) {
    // Estados dos campos
    var username by remember { mutableStateOf(signupData.username) }
    var password by remember { mutableStateOf(signupData.password) }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Estados de validação e loading
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun validateInputs(): Boolean {
        var hasError = false

        // Validar username
        when {
            username.isBlank() -> {
                usernameError = "Username é obrigatório"
                hasError = true
            }
            username.length < 3 -> {
                usernameError = "Username deve ter pelo menos 3 caracteres"
                hasError = true
            }
            username.length > 20 -> {
                usernameError = "Username deve ter no máximo 20 caracteres"
                hasError = true
            }
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> {
                usernameError = "Username pode conter apenas letras, números e _"
                hasError = true
            }
            else -> usernameError = null
        }

        // Validar password
        when {
            password.isBlank() -> {
                passwordError = "Password é obrigatória"
                hasError = true
            }
            password.length < 8 -> {
                passwordError = "Password deve ter pelo menos 8 caracteres"
                hasError = true
            }
            !password.any { it.isUpperCase() } -> {
                passwordError = "Password deve conter pelo menos uma letra maiúscula"
                hasError = true
            }
            !password.any { it.isLowerCase() } -> {
                passwordError = "Password deve conter pelo menos uma letra minúscula"
                hasError = true
            }
            !password.any { it.isDigit() } -> {
                passwordError = "Password deve conter pelo menos um número"
                hasError = true
            }
            else -> passwordError = null
        }

        // Validar confirmação de password
        when {
            confirmPassword.isBlank() -> {
                confirmPasswordError = "Confirmação de password é obrigatória"
                hasError = true
            }
            confirmPassword != password -> {
                confirmPasswordError = "Passwords não coincidem"
                hasError = true
            }
            else -> confirmPasswordError = null
        }

        return !hasError
    }

    suspend fun handleSignup() {
        if (!validateInputs()) return

        isLoading = true
        try {
            // Verificar se o username já existe
            if (userRepository.checkUsernameExists(username)) {
                usernameError = "Username já está em uso"
                isLoading = false
                return
            }

            // Verificar se o email já existe
            if (authRepository.checkEmailExists(signupData.email)) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Email já está registado",
                        duration = SnackbarDuration.Short
                    )
                }
                isLoading = false
                return
            }

            // Atualizar signupData
            signupData.username = username
            signupData.password = password

            // Criar conta no auth
            val userData = mapOf(
                "name" to signupData.name,
                "username" to username
            )

            val userId = authRepository.signUp(
                email = signupData.email,
                password = password,
                userData = userData
            )

            // Criar utilizador na base de dados
            userRepository.createUser(
                userId = userId,
                name = signupData.name,
                username = username,
                country = signupData.country,
                birthDate = signupData.birthDate!!
            )

            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Conta criada com sucesso! Verifique o seu email.",
                    duration = SnackbarDuration.Long
                )
            }

            // Navegar para login
            navController.navigate("login") {
                popUpTo("signup_step1") { inclusive = true }
            }

        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Erro ao criar conta: ${e.message}",
                    duration = SnackbarDuration.Long
                )
            }
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header com botão voltar e título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(
                    onClick = { navController.popBackStack() }
                )

                Text(
                    text = stringResource(R.string.sign_up),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 40.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Informações do Step 1 (só para mostrar)
            Text(
                text = "Dados inseridos:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF395174),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Nome: ${signupData.name}\nEmail: ${signupData.email}",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Username
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it.lowercase().replace(" ", "_")
                        usernameError = null
                    },
                    label = { Text("Username", fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    isError = usernameError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                usernameError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Password
            Column {
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    label = { Text("Password", fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    isError = passwordError != null,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible) "Ocultar password" else "Mostrar password",
                                tint = Color.Gray
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )

                passwordError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Confirmar Password
            Column {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = null
                    },
                    label = { Text("Confirmar Password", fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    isError = confirmPasswordError != null,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Ocultar password" else "Mostrar password",
                                tint = Color.Gray
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                confirmPasswordError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Criar Conta
            Button(
                onClick = {
                    scope.launch {
                        handleSignup()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF395174)
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Criar Conta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}