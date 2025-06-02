package com.example.colearnhub.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.colearnhub.R
import com.example.colearnhub.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val authState by authViewModel.authState.collectAsState()

    // Observar mudanças no estado de autenticação
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated && authState.successMessage != null) {
            //rota temporária enquanto não temos a home
            navController.navigate("signup") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(48.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.colearnhubwt),
            contentDescription = stringResource(R.string.colearnhub_logo),
            modifier = Modifier
                .width(225.dp)
                .height(134.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar mensagem de erro se houver
        if (authState.errorMessage != null) {
            Text(
                text = authState.errorMessage!!,
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Email Input
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                    authViewModel.clearMessages()
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

        // Password Input
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                    authViewModel.clearMessages()
                },
                label = { Text("Password") },
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
                            contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
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

        Spacer(modifier = Modifier.height(24.dp))

        // Forgot Password
        Text(
            text = stringResource(R.string.forgot_password),
            color = Color(0xFF526C84),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable {
                    // Handle forgot password click
                    // navController.navigate("forgot_password")
                },
            textAlign = TextAlign.Start,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Login Button
        Button(
            onClick = {
                var hasError = false

                // Validação de email
                if (email.isBlank()) {
                    emailError = "stringResource(R.string.email_required)"
                    hasError = true
                } else if (!authViewModel.isValidEmail(email)) {
                    emailError = "stringResource(R.string.email_invalid)"
                    hasError = true
                }

                // Validação de password
                if (password.isBlank()) {
                    passwordError = "stringResource(R.string.password_required)"
                    hasError = true
                } else if (!authViewModel.isValidPassword(password)) {
                    passwordError = "stringResource(R.string.password_min_length)"
                    hasError = true
                }

                if (!hasError) {
                    authViewModel.signIn(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            enabled = !authState.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF395174),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(4.dp)
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Login",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sign Up Text
        val fullText = stringResource(R.string.signup_prompt)
        val signUpText = stringResource(R.string.signup_highlight)
        val annotatedString = buildAnnotatedString {
            val startIndex = fullText.indexOf(signUpText)
            val endIndex = startIndex + signUpText.length

            append(fullText)
            addStyle(
                style = SpanStyle(color = Color.Black),
                start = 0,
                end = startIndex
            )
            addStyle(
                style = SpanStyle(
                    color = Color(0xFF526C84),
                    fontWeight = FontWeight.Medium
                ),
                start = startIndex,
                end = endIndex
            )

            addStringAnnotation(
                tag = "SIGN_UP",
                annotation = "sign_up",
                start = startIndex,
                end = endIndex
            )
        }

        ClickableText(
            text = annotatedString,
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = { offset ->
                annotatedString.getStringAnnotations(
                    tag = "SIGN_UP",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    navController.navigate("signup_step1")
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}