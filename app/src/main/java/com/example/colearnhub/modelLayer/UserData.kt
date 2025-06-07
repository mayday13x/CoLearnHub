package com.example.colearnhub.modelLayer

/**
 * Classe para armazenar os dados do signup entre os diferentes steps
 */
data class UserData(
    var name: String = "",
    var email: String = "",
    var birthDate: String = "",
    var country: Int = 1, // 1 = Portugal, 2 = Estados Unidos
    var username: String = "",
    var password: String = "",
    val role: Long? = null,
) {
    fun isStep1Valid(): Boolean {
        return name.isNotBlank() &&
                email.isNotBlank()
    }

    fun isStep2Valid(): Boolean {
        return username.isNotBlank() &&
                password.isNotBlank()
    }

    fun isComplete(): Boolean {
        return isStep1Valid() && isStep2Valid()
    }
}