package com.example.colearnhub.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colearnhub.repositoryLayer.AuthRepository
import com.example.colearnhub.ui.utils.SharedPreferenceHelper
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(private val context: Context) : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val authRepository = AuthRepository()
    private val sharedPreferenceHelper = SharedPreferenceHelper(context)

    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser.asStateFlow()

    init {
        getCurrentUser()
        checkSavedAuthState()
    }

    private fun checkSavedAuthState() {
        val isLoggedIn = sharedPreferenceHelper.getBooleanData(SharedPreferenceHelper.IS_USER_LOGGED_IN)
        _authState.value = _authState.value.copy(isAuthenticated = isLoggedIn)
    }

    fun getCurrentUser() {
        _currentUser.value = authRepository.getCurrentUser()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferenceHelper.getBooleanData(SharedPreferenceHelper.IS_USER_LOGGED_IN)
    }

/*    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)

                val userData = mapOf(
                    "email" to email
                )
                
                authRepository.signUp(email, password, userData)
                getCurrentUser()

                // Guardar estado de autenticação
                sharedPreferenceHelper.saveBooleanData(SharedPreferenceHelper.IS_USER_LOGGED_IN, true)
                sharedPreferenceHelper.saveStringData(SharedPreferenceHelper.USER_EMAIL, email)

                _authState.value = _authState.value.copy(
                    isLoading = false,
                    successMessage = "Account created successfully! Please check your email for verification.",
                    isAuthenticated = true
                )

            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Sign up failed"
                )
            }
        }
    }*/

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)

                authRepository.signIn(email, password)
                getCurrentUser()

                // Guardar estado de autenticação
                sharedPreferenceHelper.saveBooleanData(SharedPreferenceHelper.IS_USER_LOGGED_IN, true)
                sharedPreferenceHelper.saveStringData(SharedPreferenceHelper.USER_EMAIL, email)

                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    successMessage = "Login successful!"
                )

            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Login failed"
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                _currentUser.value = null

                // Limpar dados guardados
                sharedPreferenceHelper.clearPreferences()

                _authState.value = _authState.value.copy(
                    isAuthenticated = false,
                    successMessage = "Logged out successfully"
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    errorMessage = e.message ?: "Logout failed"
                )
            }
        }
    }

    fun clearMessages() {
        _authState.value = _authState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}