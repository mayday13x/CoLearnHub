package com.example.colearnhub.repository

import android.util.Log
import com.example.colearnhub.modelLayer.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {

    /**
     * Verifica se um email já existe no sistema de autenticação
     * @param email Email a verificar
     * @return true se o email existe, false caso contrário
     */


    suspend fun checkEmailExists(email: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = SupabaseClient.client
                .from("Users")
                .select {
                    filter {
                        eq("email", email)
                    }
                }
                .decodeSingle<UserEmail>()

            result != null
        } catch (e: Exception) {
            if (e.message?.contains("List is empty") == true) {
                false
            } else {
                Log.e("UserRepository", "Erro ao verificar email: ${e.message}")
                false
            }
        }
    }
    @Serializable
    data class UserEmail(val email: String)



    /**
     * Cria uma nova conta no sistema de autenticação
     * @param email Email do utilizador
     * @param password Password do utilizador
     * @param userData Dados adicionais do utilizador
     * @return ID do utilizador criado
     */
    suspend fun signUp(
        email: String,
        password: String,
        userData: Map<String, String>
    ): String {
        val response = SupabaseClient.client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            this.data = buildJsonObject {
                userData.forEach { (key, value) ->
                    put(key, value)
                }
            }
        }

        println("ID: ${response?.id}")

        val userId = response?.id
        if (userId == null) {
            throw Exception("Erro ao obter ID do utilizador")
        }

        return userId
    }


    /**
     * Faz login no sistema
     * @param email Email do utilizador
     * @param password Password do utilizador
     */
    suspend fun signIn(email: String, password: String) {
        SupabaseClient.client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    /**
     * Faz logout do sistema
     */
    suspend fun signOut() {
        SupabaseClient.client.auth.signOut()
    }

    /**
     * Obtém o utilizador atual
     */
    fun getCurrentUser() = SupabaseClient.client.auth.currentUserOrNull()
}