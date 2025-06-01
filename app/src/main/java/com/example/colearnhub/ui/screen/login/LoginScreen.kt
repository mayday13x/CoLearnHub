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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import androidx.navigation.NavController
import com.example.colearnhub.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

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
            contentDescription = "CoLearnHub Logo",
            modifier = Modifier
                .width(225.dp)
                .height(134.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email Input
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

        // Password Input
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
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

        Spacer(modifier = Modifier.height(24.dp))

        // Forgot Password
        Text(
            text = "Forgot password?",
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
                // Validação simples
                var hasError = false

                if (email.isBlank()) {
                    emailError = "Email is required"
                    hasError = true
                }

                if (password.isBlank()) {
                    passwordError = "Password is required"
                    hasError = true
                }

                if (!hasError) {
                    isLoading = true
                    // Simular login - aqui você faria a chamada para o Supabase
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000) // Simular delay de rede
                        isLoading = false
                        // Após implementar autenticação real:
                        // navController.navigate("home") {
                        //     popUpTo("login") { inclusive = true }
                        // }
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
                Text(
                    text = "Login",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sign Up Text
        val fullText = "New to CoLearnHub? Sign Up"
        val signUpText = "Sign Up"
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
                    navController.navigate("signup_step1") // Adicione esta linha
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}