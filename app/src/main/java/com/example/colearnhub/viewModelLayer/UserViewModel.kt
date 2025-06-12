package com.example.colearnhub.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
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

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application.applicationContext)
    private val authRepository = AuthRepository()
    private val countryRepository = CountryRepository()
    private val ratingRepository = RatingRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _countryName = MutableStateFlow<String?>(null)
    val countryName: StateFlow<String?> = _countryName

    private val _formattedCreatedAt = MutableStateFlow<String>("Not defined")
    val formattedCreatedAt: StateFlow<String> = _formattedCreatedAt

    // Inicializar com valores do SharedPreferences
    private val _userContributions = MutableStateFlow(userRepository.getUserAdditionalDataFromPrefs().second)
    val userContributions: StateFlow<Int> = _userContributions

    private val _averageRating = MutableStateFlow(userRepository.getUserAdditionalDataFromPrefs().third)
    val averageRating: StateFlow<Double> = _averageRating

    // Crud do utilizador
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadCurrentUser() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            loadUserById(currentUser.id)
        } else {
            loadUserFromPrefs()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadCurrentUserEditProfile() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            loadUserByIdEditProfile(currentUser.id)
        } else {
            loadUserFromPrefs()
        }
    }

    private fun loadUserFromPrefs() {
        val user = userRepository.getUserFromPrefs()
        if (user != null) {
            _user.value = user
            val (countryName, contributions, avgRating) = userRepository.getUserAdditionalDataFromPrefs()
            _countryName.value = countryName
            _userContributions.value = contributions
            _averageRating.value = avgRating

            Log.d("UserViewModel", "Dados carregados do SharedPreferences:")
            Log.d("UserViewModel", "User: ${user.toString()}")
            Log.d("UserViewModel", "Country Name: $countryName")
            Log.d("UserViewModel", "Contributions: $contributions")
            Log.d("UserViewModel", "Average Rating: $avgRating")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUserById(userId: String) {
        viewModelScope.launch {
            // Primeiro, carregar dados do SharedPreferences
            loadUserFromPrefs()
            
            try {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    _user.value = user
                    
                    // Get country name when user is loaded
                    user.country.let { countryId ->
                        try {
                            val country = countryRepository.getCountryById(countryId)
                            _countryName.value = country?.country
                        } catch (e: Exception) {
                            Log.e("UserViewModel", "Error loading country: ${e.message}")
                        }
                    }
                    
                    // Format created_at date
                    user.created_at?.let { dateStr ->
                        formatCreatedAtDate(dateStr)
                    }
                    
                    // Get user contributions and average rating
                    try {
                        val contributions = ratingRepository.getUserContributions(userId)
                        val average = ratingRepository.getAverageRatingForUserMaterials(userId)
                        
                        // Só atualiza se os valores forem diferentes de 0
                        if (contributions > 0) {
                            _userContributions.value = contributions
                        }
                        if (average > 0) {
                            _averageRating.value = average
                        }
                        
                        // Salvar todos os dados no SharedPreferences
                        userRepository.updateUser(
                            user = user,
                            countryName = _countryName.value,
                            contributions = _userContributions.value,
                            averageRating = _averageRating.value
                        )
                    } catch (e: Exception) {
                        Log.e("UserViewModel", "Error loading contributions/rating: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error loading from server: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUserByIdEditProfile(userId: String) {
        viewModelScope.launch {
            // Primeiro, carregar dados do SharedPreferences
            loadUserFromPrefs()
            
            try {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    _user.value = user
                    
                    // Get country name when user is loaded
                    user.country.let { countryId ->
                        try {
                            val country = countryRepository.getCountryById(countryId)
                            _countryName.value = country?.country
                        } catch (e: Exception) {
                            Log.e("UserViewModel", "Error loading country: ${e.message}")
                        }
                    }
                    
                    // Format created_at date
                    user.created_at?.let { dateStr ->
                        formatCreatedAtDate(dateStr)
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error loading from server: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUserByUsername(username: String) {
        viewModelScope.launch {
            // Primeiro, carregar dados do SharedPreferences
            loadUserFromPrefs()
            
            try {
                val user = userRepository.getUserByUsername(username)
                if (user != null) {
                    _user.value = user
                    
                    // Get country name when user is loaded
                    user.country.let { countryId ->
                        try {
                            val country = countryRepository.getCountryById(countryId)
                            _countryName.value = country?.country
                        } catch (e: Exception) {
                            Log.e("UserViewModel", "Error loading country: ${e.message}")
                        }
                    }
                    
                    // Format created_at date
                    user.created_at?.let { dateStr ->
                        formatCreatedAtDate(dateStr)
                    }
                    
                    // Get user contributions and average rating
                    try {
                        val contributions = ratingRepository.getUserContributions(user.id)
                        val average = ratingRepository.getAverageRatingForUserMaterials(user.id)
                        
                        // Só atualiza se os valores forem diferentes de 0
                        if (contributions > 0) {
                            _userContributions.value = contributions
                        }
                        if (average > 0) {
                            _averageRating.value = average
                        }
                        
                        // Salvar todos os dados no SharedPreferences
                        userRepository.updateUser(
                            user = user,
                            countryName = _countryName.value,
                            contributions = _userContributions.value,
                            averageRating = _averageRating.value
                        )
                    } catch (e: Exception) {
                        Log.e("UserViewModel", "Error loading contributions/rating: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error loading from server: ${e.message}")
            }
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

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(
                    user = user,
                    countryName = _countryName.value,
                    contributions = _userContributions.value,
                    averageRating = _averageRating.value
                )
                _user.value = user
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating user: ${e.message}")
                _user.value = user
            }
        }
    }
}
