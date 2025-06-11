package com.example.colearnhub.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colearnhub.repositoryLayer.AuthRepository
import com.example.colearnhub.repositoryLayer.CountryRepository
import com.example.colearnhub.repositoryLayer.RatingRepository
import com.example.colearnhub.modelLayer.User
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
    private val ratingRepository = RatingRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _countryName = MutableStateFlow<String?>(null)
    val countryName: StateFlow<String?> = _countryName

    private val _formattedCreatedAt = MutableStateFlow<String>("Not defined")
    val formattedCreatedAt: StateFlow<String> = _formattedCreatedAt

    private val _userContributions = MutableStateFlow(0)
    val userContributions: StateFlow<Int> = _userContributions

    private val _averageRating = MutableStateFlow(0.0)
    val averageRating: StateFlow<Double> = _averageRating

    // Crud do utilizador
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadCurrentUser() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            loadUserById(currentUser.id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadCurrentUserEditProfile() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            loadUserByIdEditProfile(currentUser.id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUserById(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            _user.value = user
            // Get country name when user is loaded
            user?.country?.let { countryId ->
                loadCountryName(countryId)
            }
            // Format created_at date
            user?.created_at?.let { dateStr ->
                formatCreatedAtDate(dateStr)
            }
            // Get user contributions and average rating
            user?.id?.let { id ->
                loadUserContributions(id)
                loadAverageRatingForUserMaterials(id)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUserByIdEditProfile(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            _user.value = user
            // Get country name when user is loaded
            user?.country?.let { countryId ->
                loadCountryName(countryId)
            }
            // Format created_at date
            user?.created_at?.let { dateStr ->
                formatCreatedAtDate(dateStr)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUserByUsername(username: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByUsername(username)
            _user.value = user
            // Get country name when user is loaded
            user?.country?.let { countryId ->
                loadCountryName(countryId)
            }
            // Format created_at date
            user?.created_at?.let { dateStr ->
                formatCreatedAtDate(dateStr)
            }
            // Get user contributions and average rating
            user?.id?.let { id ->
                loadUserContributions(id)
                loadAverageRatingForUserMaterials(id)
            }
        }
    }

    private fun loadCountryName(countryId: Int) {
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

    private fun loadUserContributions(userId: String) {
        viewModelScope.launch {
            val contributions = ratingRepository.getUserContributions(userId)
            _userContributions.value = contributions
        }
    }

    private fun loadAverageRatingForUserMaterials(userId: String) {
        viewModelScope.launch {
            val average = ratingRepository.getAverageRatingForUserMaterials(userId)
            _averageRating.value = average
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(user)
                _user.value = user
                // Reload country name
                loadCountryName(user.country)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating user: ${e.message}")
            }
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
