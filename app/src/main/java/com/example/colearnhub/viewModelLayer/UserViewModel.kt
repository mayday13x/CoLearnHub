package com.example.colearnhub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colearnhub.repositoryLayer.User
import com.example.colearnhub.repositoryLayer.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun loadUser(userId: String) {
        viewModelScope.launch {
            val userData = repository.getUserById(userId)
            _user.value = userData
        }
    }
}
