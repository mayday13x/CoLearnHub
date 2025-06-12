package com.example.colearnhub.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.colearnhub.modelLayer.UserData
import com.example.colearnhub.repositoryLayer.AuthRepository
import com.example.colearnhub.repositoryLayer.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class SignupUiState(
    // Step 1 fields
    val name: String = "",
    val email: String = "",
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    val country: Int = 1,

    // Step 2 fields
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Validation errors
    val nameError: String? = null,
    val emailError: String? = null,
    val dateError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,

    // UI states
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val isSignupSuccess: Boolean = false,

)

class SignupViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    // Step 1 Actions
    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name.trim(),
            nameError = null
        )
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email.trim().lowercase(),
            emailError = null
        )
    }

    fun updateDay(day: Int) {
        _uiState.value = _uiState.value.copy(
            day = day,
            dateError = null
        )
    }

    fun updateMonth(month: Int) {
        _uiState.value = _uiState.value.copy(
            month = month,
            dateError = null
        )
    }

    fun updateYear(year: Int) {
        _uiState.value = _uiState.value.copy(
            year = year,
            dateError = null
        )
    }

    fun updateCountry(country: Int) {
        _uiState.value = _uiState.value.copy(country = country)
    }

    // Step 2 Actions
    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(
            username = username.lowercase().replace(" ", "_"),
            usernameError = null
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null
        )
    }

    // Validation methods
    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateStep1(): Boolean {
        val currentState = _uiState.value
        var hasError = false
        var nameError: String? = null
        var emailError: String? = null
        var dateError: String? = null

        // Validate name
        when {
            currentState.name.isBlank() -> {
                nameError = "Nome é obrigatório"
                hasError = true
            }
            currentState.name.length < 2 -> {
                nameError = "Nome deve ter pelo menos 2 caracteres"
                hasError = true
            }
        }

        // Validate email
        when {
            currentState.email.isBlank() -> {
                emailError = "Email é obrigatório"
                hasError = true
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches() -> {
                emailError = "Email inválido"
                hasError = true
            }
        }

        // Validate birth date
        when {
            currentState.day == 0 || currentState.month == 0 || currentState.year == 0 -> {
                dateError = "Data de nascimento é obrigatória"
                hasError = true
            }
            else -> {
                try {
                    val birthDate = LocalDate.of(currentState.year, currentState.month, currentState.day)
                    val now = LocalDate.now()
                    val age = now.year - birthDate.year -
                            if (now.dayOfYear < birthDate.dayOfYear) 1 else 0

                    when {
                        age < 13 -> {
                            dateError = "Deve ter pelo menos 13 anos"
                            hasError = true
                        }
                        age > 100 -> {
                            dateError = "Data inválida"
                            hasError = true
                        }
                    }
                } catch (e: Exception) {
                    dateError = "Data inválida"
                    hasError = true
                }
            }
        }

        _uiState.value = _uiState.value.copy(
            nameError = nameError,
            emailError = emailError,
            dateError = dateError
        )

        return !hasError
    }

    private fun validateStep2(): Boolean {
        val currentState = _uiState.value
        var hasError = false
        var usernameError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null

        // Validate username
        when {
            currentState.username.isBlank() -> {
                usernameError = "Username é obrigatório"
                hasError = true
            }
            currentState.username.length < 3 -> {
                usernameError = "Username deve ter pelo menos 3 caracteres"
                hasError = true
            }
            currentState.username.length > 20 -> {
                usernameError = "Username deve ter no máximo 20 caracteres"
                hasError = true
            }
            !currentState.username.matches(Regex("^[a-zA-Z0-9_]+$")) -> {
                usernameError = "Username pode conter apenas letras, números e _"
                hasError = true
            }
        }

        // Validate password
        when {
            currentState.password.isBlank() -> {
                passwordError = "Password é obrigatória"
                hasError = true
            }
            currentState.password.length < 8 -> {
                passwordError = "Password deve ter pelo menos 8 caracteres"
                hasError = true
            }
            !currentState.password.any { it.isUpperCase() } -> {
                passwordError = "Password deve conter pelo menos uma letra maiúscula"
                hasError = true
            }
            !currentState.password.any { it.isLowerCase() } -> {
                passwordError = "Password deve conter pelo menos uma letra minúscula"
                hasError = true
            }
            !currentState.password.any { it.isDigit() } -> {
                passwordError = "Password deve conter pelo menos um número"
                hasError = true
            }
        }

        // Validate confirm password
        when {
            currentState.confirmPassword.isBlank() -> {
                confirmPasswordError = "Confirmação de password é obrigatória"
                hasError = true
            }
            currentState.confirmPassword != currentState.password -> {
                confirmPasswordError = "Passwords não coincidem"
                hasError = true
            }
        }

        _uiState.value = _uiState.value.copy(
            usernameError = usernameError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )

        return !hasError
    }

    // Step 1 - Validate and proceed
    fun proceedToStep2() {
        if (!validateStep1()) return

        _isLoading.value = true
        _error.value = null
        _isSuccess.value = false

        viewModelScope.launch {
            try {
                val emailExists = authRepository.checkEmailExists(_uiState.value.email)
                if (emailExists) {
                    _uiState.value = _uiState.value.copy(
                        emailError = "Este email já está registado",
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        snackbarMessage = "step1_success" // Signal to navigate
                    )
                }
            } catch (e: Exception) {
                Log.e("SignupViewModel", "Error during signup: ${e.message}")
                _error.value = e.message ?: "An error occurred during signup"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Step 2 - Complete signup
    @RequiresApi(Build.VERSION_CODES.O)
    fun completeSignup() {
        if (!validateStep2()) return

        _isLoading.value = true
        _error.value = null
        _isSuccess.value = false

        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                // Check if username exists
                if (userRepository.checkUsernameExists(currentState.username)) {
                    _uiState.value = _uiState.value.copy(
                        usernameError = "Username já está em uso",
                        isLoading = false
                    )
                    return@launch
                }

                // Check if email exists (double check)
                if (authRepository.checkEmailExists(currentState.email)) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        snackbarMessage = "Email já está registado"
                    )
                    return@launch
                }

                // Create auth account
                val userData = mapOf(
                    "name" to currentState.name,
                    "username" to currentState.username
                )

                val userId = authRepository.signUp(
                    email = currentState.email,
                    password = currentState.password,
                    userData = userData
                )

                // Create user in database
                val birthDate = String.format("%04d-%02d-%02d", currentState.year, currentState.month, currentState.day)

                userRepository.createUser(
                    userId = userId,
                    name = currentState.name,
                    username = currentState.username,
                    country = currentState.country,
                    birthDate = birthDate,
                    email = currentState.email
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSignupSuccess = true,
                    snackbarMessage = "Conta criada com sucesso! Verifique o seu email."
                )

            } catch (e: Exception) {
                Log.e("SignupViewModel", "Error during signup: ${e.message}")
                _error.value = e.message ?: "An error occurred during signup"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Clear snackbar message
    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    // Initialize with existing user data (if coming from previous step)
    fun initializeWithUser(userData: UserData) {
        val birthParts = userData.birthDate.split("-")
        _uiState.value = _uiState.value.copy(
            name = userData.name,
            email = userData.email,
            username = userData.username,
            password = userData.password,
            country = userData.country,
            year = birthParts.getOrNull(0)?.toIntOrNull() ?: 0,
            month = birthParts.getOrNull(1)?.toIntOrNull() ?: 0,
            day = birthParts.getOrNull(2)?.toIntOrNull() ?: 0
        )
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _isSuccess.value = false
    }
}