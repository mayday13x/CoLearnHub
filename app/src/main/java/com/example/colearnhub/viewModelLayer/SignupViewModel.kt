package com.example.colearnhub.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.colearnhub.R
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

@RequiresApi(Build.VERSION_CODES.O)
class SignupViewModel(
    application: Application,
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : AndroidViewModel(application) {

    private val context = application.applicationContext
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
            name = name,
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
    private fun validateStep1(): Boolean {
        val currentState = _uiState.value
        var hasError = false
        var nameError: String? = null
        var emailError: String? = null
        var dateError: String? = null

        // Validate name
        when {
            currentState.name.isBlank() -> {
                nameError = context.getString(R.string.name_required)
                hasError = true
            }
            currentState.name.length < 2 -> {
                nameError = context.getString(R.string.name_min_length)
                hasError = true
            }
        }

        // Validate email
        when {
            currentState.email.isBlank() -> {
                emailError = context.getString(R.string.email_required)
                hasError = true
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches() -> {
                emailError = context.getString(R.string.email_invalid)
                hasError = true
            }
        }

        // Validate birth date
        when {
            currentState.day == 0 || currentState.month == 0 || currentState.year == 0 -> {
                dateError = context.getString(R.string.birthdate_required)
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
                            dateError = context.getString(R.string.age_too_young)
                            hasError = true
                        }
                        age > 100 -> {
                            dateError = context.getString(R.string.age_too_old)
                            hasError = true
                        }
                    }
                } catch (e: Exception) {
                    dateError = context.getString(R.string.invalid_date)
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
                usernameError = context.getString(R.string.username_required)
                hasError = true
            }
            currentState.username.length < 3 -> {
                usernameError = context.getString(R.string.username_min_length)
                hasError = true
            }
            currentState.username.length > 20 -> {
                usernameError = context.getString(R.string.username_max_length)
                hasError = true
            }
            !currentState.username.matches(Regex("^[a-zA-Z0-9_]+$")) -> {
                usernameError = context.getString(R.string.username_invalid_chars)
                hasError = true
            }
        }

        // Validate password
        when {
            currentState.password.isBlank() -> {
                passwordError = context.getString(R.string.password_required)
                hasError = true
            }
            currentState.password.length < 8 -> {
                passwordError = context.getString(R.string.password_min_length_8)
                hasError = true
            }
            !currentState.password.any { it.isUpperCase() } -> {
                passwordError = context.getString(R.string.password_uppercase_required)
                hasError = true
            }
            !currentState.password.any { it.isLowerCase() } -> {
                passwordError = context.getString(R.string.password_lowercase_required)
                hasError = true
            }
            !currentState.password.any { it.isDigit() } -> {
                passwordError = context.getString(R.string.password_digit_required)
                hasError = true
            }
        }

        // Validate confirm password
        when {
            currentState.confirmPassword.isBlank() -> {
                confirmPasswordError = context.getString(R.string.confirm_password_required)
                hasError = true
            }
            currentState.confirmPassword != currentState.password -> {
                confirmPasswordError = context.getString(R.string.confirm_password_mismatch)
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
    @SuppressLint("StringFormatInvalid")
    fun proceedToStep2() {
        if (!validateStep1()) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val emailExists = authRepository.checkEmailExists(_uiState.value.email)
                if (emailExists) {
                    _uiState.value = _uiState.value.copy(
                        emailError = context.getString(R.string.email_already_registered),
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        snackbarMessage = "step1_success" // Signal to navigate
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    snackbarMessage = context.getString(R.string.error_checking_email, e.message)
                )
            }
        }
    }

    // Step 2 - Complete signup
    fun completeSignup() {
        if (!validateStep2()) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                // Check if username exists
                if (userRepository.checkUsernameExists(currentState.username)) {
                    _uiState.value = _uiState.value.copy(
                        usernameError = context.getString(R.string.username_already_taken),
                        isLoading = false
                    )
                    return@launch
                }

                // Check if email exists (double check)
                if (authRepository.checkEmailExists(currentState.email)) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        snackbarMessage = context.getString(R.string.email_already_registered_server)
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
                    snackbarMessage = context.getString(R.string.account_created_success)
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    snackbarMessage = context.getString(R.string.error_creating_account, e.message)
                )
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

    // Get current user data as User object
 /*   fun getCurrentUserData(): UserData {
        val currentState = _uiState.value
        val birthDate = String.format("%04d-%02d-%02d", currentState.year, currentState.month, currentState.day)

        return UserData(
            name = currentState.name,
            email = currentState.email,
            username = currentState.username,
            password = currentState.password,
            country = currentState.country,
            birth_date = birthDate
        )
    }*/
}