package com.example.colearnhub.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colearnhub.repositoryLayer.AuthRepository
import com.example.colearnhub.repositoryLayer.CountryRepository
import com.example.colearnhub.repositoryLayer.User
import com.example.colearnhub.repositoryLayer.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val authRepository = AuthRepository()
    private val countryRepository = CountryRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _countryName = MutableStateFlow<String?>(null)
    val countryName: StateFlow<String?> = _countryName

    private val _formattedCreatedAt = MutableStateFlow<String>("Not defined")
    val formattedCreatedAt: StateFlow<String> = _formattedCreatedAt

    // Crud do utilizador
    fun getUserById(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            _user.value = user
            // Get country name when user is loaded
            user?.country?.let { countryId ->
                getCountryName(countryId)
            }
            // Format created_at date
            user?.created_at?.let { dateStr ->
                formatCreatedAtDate(dateStr)
            }
        }
    }

    fun getUserByUsername(username: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByUsername(username)
            _user.value = user
            // Get country name when user is loaded
            user?.country?.let { countryId ->
                getCountryName(countryId)
            }
            // Format created_at date
            user?.created_at?.let { dateStr ->
                formatCreatedAtDate(dateStr)
            }
        }
    }

    fun getCurrentUser() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            getUserById(currentUser.id)
        }
    }

    private fun getCountryName(countryId: Int) {
        viewModelScope.launch {
            val country = countryRepository.getCountryById(countryId)
            _countryName.value = country?.country ?: "Not defined"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatCreatedAtDate(dateStr: String) {
        try {
            val dateTime = LocalDateTime.parse(dateStr)
            _formattedCreatedAt.value = dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        } catch (e: Exception) {
            _formattedCreatedAt.value = "Not defined"
        }
    }

    //fun getUserCountry

   //TODO:
 /*   fun updateUser(user: User) {
        viewModelScope.launch {
            UserRepository.updateUser(user)
            _user.value = user
        }
    } */
}
