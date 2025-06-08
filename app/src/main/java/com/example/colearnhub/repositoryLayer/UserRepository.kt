package com.example.colearnhub.repositoryLayer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.colearnhub.modelLayer.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter

@Serializable
data class User(
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
    val curricularYear: Int? = null,
    val created_at: String? = null
)


@Serializable
data class UserUsername(val username: String)

class UserRepository {

    /**
     * Verifica se um username já existe na base de dados
     * @param username Username a verificar
     * @return true se existe, false caso contrário
     */
    suspend fun checkUsernameExists(username: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = SupabaseClient.client
                .from("Users")
                .select {
                    filter {
                        eq("username", username)
                    }
                }
                .decodeSingle<UserUsername>()

            result != null
        } catch (e: Exception) {
            if (e.message?.contains("List is empty") == true) {
                false
            } else {
                Log.e("UserRepository", "stringResource(R.string.error_checking_username): ${e.message}")
                false
            }
        }
    }


    /**
     * Cria um novo utilizador na base de dados
     * @param userId ID do utilizador
     * @param name Nome completo
     * @param username Nome de utilizador
     * @param country País (1=Portugal, 2=EUA)
     * @param birthDate Data de nascimento
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createUser(
        userId: String,
        name: String,
        username: String,
        country: Int,
        birthDate: String,
        email : String,
    ) {
        val user = User(
            id = userId,
            name = name,
            username = username,
            country = country,
            birth_date = birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            profile_picture = null,
            role = null,
            email = email
        )

        SupabaseClient.client
            .from("Users")
            .insert(user)
    }

    /**
     * Obtém um utilizador pelo ID
     * @param userId ID do utilizador
     * @return Utilizador ou null se não encontrado
     */
    suspend fun getUserById(userId: String): User? {
        return try {
            SupabaseClient.client
                .from("Users")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<User>()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtém um utilizador pelo username
     * @param username Username do utilizador
     * @return Utilizador ou null se não encontrado
     */
    suspend fun getUserByUsername(username: String): User? {
        return try {
            SupabaseClient.client
                .from("Users")
                .select {
                    filter {
                        eq("username", username)
                    }
                }
                .decodeSingleOrNull<User>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUsersByIds(userIds: List<String>): List<User> = withContext(Dispatchers.IO) {
        try {
            if (userIds.isEmpty()) return@withContext emptyList()

            SupabaseClient.client
                .from("Users")
                .select {
                    filter {
                        User::id isIn userIds
                    }
                }
                .decodeList<User>()
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao buscar utilizadores: ${e.message}", e)
            emptyList()
        }
    }


}
