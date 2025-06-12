package com.example.colearnhub.repositoryLayer

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.colearnhub.modelLayer.SupabaseClient
import com.example.colearnhub.modelLayer.User
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.format.DateTimeFormatter

@Serializable
data class UserUsername(val username: String)

class UserRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Salva os dados do usuário no SharedPreferences
     */
    private fun saveUserToPrefs(user: User, countryName: String? = null, contributions: Int? = null, averageRating: Double? = null) {
        val userJson = json.encodeToString(User.serializer(), user)

        // Preserva valores existentes se os novos não forem fornecidos
        val currentCountryName = countryName ?: sharedPreferences.getString("country_name", null)
        val currentContributions = contributions ?: sharedPreferences.getInt("contributions", 0)
        val currentAverageRating = averageRating ?: sharedPreferences.getFloat("average_rating", 0f).toDouble()

        sharedPreferences.edit().apply {
            putString("user_data", userJson)
            putBoolean("has_offline_changes", true)
            currentCountryName?.let { putString("country_name", it) }
            putInt("contributions", currentContributions)
            putFloat("average_rating", currentAverageRating.toFloat())
            apply()
        }

        Log.d("UserRepository", "Data saved to SharedPreferences:")
        Log.d("UserRepository", "User: $userJson")
        Log.d("UserRepository", "Country Name: $currentCountryName")
        Log.d("UserRepository", "Contributions: $currentContributions")
        Log.d("UserRepository", "Average Rating: $currentAverageRating")
    }

    /**
     * Atualiza apenas os dados do usuário (preservando dados adicionais)
     */
    private fun saveUserOnlyToPrefs(user: User) {
        val userJson = json.encodeToString(User.serializer(), user)
        sharedPreferences.edit().apply {
            putString("user_data", userJson)
            putBoolean("has_offline_changes", true)
            apply()
        }

        Log.d("UserRepository", "User data updated in SharedPreferences: $userJson")
    }

    /**
     * Recupera os dados do usuário do SharedPreferences
     */
    fun getUserFromPrefs(): User? {
        val userJson = sharedPreferences.getString("user_data", null)
        Log.d("UserRepository", "Loading user from SharedPreferences: $userJson")
        return userJson?.let {
            try {
                json.decodeFromString(User.serializer(), it)
            } catch (e: Exception) {
                Log.e("UserRepository", "Error parsing user data from prefs: ${e.message}")
                null
            }
        }
    }

    /**
     * Recupera dados adicionais do usuário do SharedPreferences
     */
    fun getUserAdditionalDataFromPrefs(): Triple<String?, Int, Double> {
        val countryName = sharedPreferences.getString("country_name", null)
        val contributions = sharedPreferences.getInt("contributions", 0)
        val averageRating = sharedPreferences.getFloat("average_rating", 0f).toDouble()

        Log.d("UserRepository", "Loading additional data from SharedPreferences:")
        Log.d("UserRepository", "Country Name: $countryName")
        Log.d("UserRepository", "Contributions: $contributions")
        Log.d("UserRepository", "Average Rating: $averageRating")

        return Triple(countryName, contributions, averageRating)
    }

    /**
     * Verifica se há alterações offline pendentes
     */
    private fun hasOfflineChanges(): Boolean {
        return sharedPreferences.getBoolean("has_offline_changes", false)
    }

    /**
     * Marca as alterações offline como sincronizadas
     */
    private fun markOfflineChangesSynced() {
        sharedPreferences.edit().apply {
            putBoolean("has_offline_changes", false)
            apply()
        }
    }

    /**
     * Sincroniza alterações offline com o servidor
     */
    private suspend fun syncOfflineChanges(user: User) {
        if (hasOfflineChanges()) {
            try {
                SupabaseClient.client
                    .from("Users")
                    .update(user) {
                        filter {
                            eq("id", user.id)
                        }
                    }
                markOfflineChangesSynced()
            } catch (e: Exception) {
                Log.e("UserRepository", "Error syncing offline changes: ${e.message}")
                throw e
            }
        }
    }

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
                Log.e("UserRepository", "Error checking username: ${e.message}")
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

        try {
            SupabaseClient.client
                .from("Users")
                .insert(user)

            // Salva no SharedPreferences após criar
            saveUserToPrefs(user, contributions = 0, averageRating = 0.0)
        } catch (e: Exception) {
            // Se falhar ao criar online, salva apenas localmente
            saveUserToPrefs(user, contributions = 0, averageRating = 0.0)
            throw e
        }
    }

    /**
     * Obtém um utilizador pelo ID
     * @param userId ID do utilizador
     * @return Utilizador ou null se não encontrado
     */
    suspend fun getUserById(userId: String): User? {
        return try {
            val user = SupabaseClient.client
                .from("Users")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<User>()

            // Se houver alterações offline, mantém as alterações locais
            if (hasOfflineChanges()) {
                val localUser = getUserFromPrefs()
                if (localUser != null) {
                    // Tenta sincronizar as alterações offline
                    syncOfflineChanges(localUser)
                    return localUser
                }
            }

            // Se não houver alterações offline, salva apenas os dados do usuário (preservando dados adicionais)
            user?.let { saveUserOnlyToPrefs(it) }
            user
        } catch (e: Exception) {
            // Em caso de erro, retorna os dados locais
            getUserFromPrefs()
        }
    }

    /**
     * Obtém um utilizador pelo username
     * @param username Username do utilizador
     * @return Utilizador ou null se não encontrado
     */
    suspend fun getUserByUsername(username: String): User? {
        return try {
            val user = SupabaseClient.client
                .from("Users")
                .select {
                    filter {
                        eq("username", username)
                    }
                }
                .decodeSingleOrNull<User>()

            // Se houver alterações offline, mantém as alterações locais
            if (hasOfflineChanges()) {
                val localUser = getUserFromPrefs()
                if (localUser != null) {
                    // Tenta sincronizar as alterações offline
                    syncOfflineChanges(localUser)
                    return localUser
                }
            }

            // Se não houver alterações offline, salva apenas os dados do usuário (preservando dados adicionais)
            user?.let { saveUserOnlyToPrefs(it) }
            user
        } catch (e: Exception) {
            // Em caso de erro, retorna os dados locais
            getUserFromPrefs()
        }
    }

    suspend fun getUsersByIds(userIds: List<String>): List<User> = withContext(Dispatchers.IO) {
        try {
            if (userIds.isEmpty()) return@withContext emptyList()

            val users = SupabaseClient.client
                .from("Users")
                .select {
                    filter {
                        User::id isIn userIds
                    }
                }
                .decodeList<User>()

            // Se houver alterações offline para o usuário atual, mantém as alterações locais
            if (hasOfflineChanges()) {
                val localUser = getUserFromPrefs()
                if (localUser != null && userIds.contains(localUser.id)) {
                    // Tenta sincronizar as alterações offline
                    syncOfflineChanges(localUser)
                    return@withContext users.map { if (it.id == localUser.id) localUser else it }
                }
            }

            // Se não houver alterações offline, salva apenas os dados do usuário (preservando dados adicionais)
            users.find { it.id == userIds.firstOrNull() }?.let { saveUserOnlyToPrefs(it) }
            users
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching users: ${e.message}", e)
            // Em caso de erro, retorna os dados locais
            getUserFromPrefs()?.let { listOf(it) } ?: emptyList()
        }
    }

    suspend fun updateUser(user: User, countryName: String? = null, contributions: Int? = null, averageRating: Double? = null) {
        try {
            SupabaseClient.client
                .from("Users")
                .update(user) {
                    filter {
                        eq("id", user.id)
                    }
                }

            // Salva no SharedPreferences após atualizar
            saveUserToPrefs(user, countryName, contributions, averageRating)
            markOfflineChangesSynced() // Marca como sincronizado após sucesso
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user: ${e.message}")
            // Mesmo com erro na atualização online, salva localmente
            saveUserToPrefs(user, countryName, contributions, averageRating)
            throw e
        }
    }
}