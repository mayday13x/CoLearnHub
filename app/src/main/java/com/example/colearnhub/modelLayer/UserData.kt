package com.example.colearnhub.modelLayer

import kotlinx.serialization.Serializable


/**
 * Classe para armazenar os dados do signup entre os diferentes steps
 */
data class UserData(    // user from auth user of supabase
    var name: String = "",
    var email: String = "",
    var birthDate: String = "",
    var country: Int = 1, // 1 = Portugal, 2 = Estados Unidos
    var username: String = "",
    var password: String = "",
    val role: Long? = null,
)

@Serializable
data class User(    // user from database table
    val id: String,
    val name: String,
    val username: String,
    val country: Int, // 1 = Portugal, 2 = Estados Unidos
    val profile_picture: String? = null,
    val birth_date: String,
    val role: Long? = null,
    val email: String? = null,
    val school: String? = null,
    val course: String? = null,
    //val curricularYear: Int? = null,
    val created_at: String? = null
)