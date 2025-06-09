package com.example.colearnhub.repositoryLayer

import android.util.Log
import com.example.colearnhub.modelLayer.Material
import com.example.colearnhub.modelLayer.Rating
import com.example.colearnhub.modelLayer.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RatingRepository {

    /**
     * Get rating by user ID and material ID
     */
    suspend fun getRating(userId: String, materialId: Long): Rating? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("RatingRepository", "Buscando rating para user: $userId e material: $materialId")

            val result = SupabaseClient.client
                .from("Ratings")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("material_id", materialId)
                    }
                }
                .decodeSingleOrNull<Rating>()

            if (result != null) {
                Log.d("RatingRepository", "Rating encontrado: ${result.rating}")
            } else {
                Log.w("RatingRepository", "Rating não encontrado para user: $userId e material: $materialId")
            }

            result
        } catch (e: Exception) {
            Log.e("RatingRepository", "Erro ao buscar rating: ${e.message}")
            null
        }
    }

    /**
     * Get all ratings for a specific material
     */
    suspend fun getMaterialRatings(materialId: Long): List<Rating> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("RatingRepository", "Buscando ratings para material: $materialId")

            val result = SupabaseClient.client
                .from("Ratings")
                .select {
                    filter {
                        eq("material_id", materialId)
                    }
                }
                .decodeList<Rating>()

            Log.d("RatingRepository", "Encontrados ${result.size} ratings para o material")
            result
        } catch (e: Exception) {
            Log.e("RatingRepository", "Erro ao buscar ratings do material: ${e.message}")
            emptyList()
        }
    }

    /**
     * Get all ratings made by a specific user
     */
    suspend fun getUserRatings(userId: String): List<Rating> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("RatingRepository", "Buscando ratings do usuário: $userId")

            val result = SupabaseClient.client
                .from("Ratings")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Rating>()

            Log.d("RatingRepository", "Encontrados ${result.size} ratings do usuário")
            result
        } catch (e: Exception) {
            Log.e("RatingRepository", "Erro ao buscar ratings do usuário: ${e.message}")
            emptyList()
        }
    }

    /**
     * Create or update a rating
     */
    suspend fun upsertRating(rating: Rating): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("RatingRepository", "Inserindo/atualizando rating para user: ${rating.user_id} e material: ${rating.material_id}")

            SupabaseClient.client
                .from("Ratings")
                .upsert(rating)

            Log.d("RatingRepository", "Rating inserido/atualizado com sucesso")
            true
        } catch (e: Exception) {
            Log.e("RatingRepository", "Erro ao inserir/atualizar rating: ${e.message}")
            false
        }
    }

    /**
     * Delete a rating
     */
    suspend fun deleteRating(userId: String, materialId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("RatingRepository", "Deletando rating para user: $userId e material: $materialId")

            SupabaseClient.client
                .from("Ratings")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("material_id", materialId)
                    }
                }

            Log.d("RatingRepository", "Rating deletado com sucesso")
            true
        } catch (e: Exception) {
            Log.e("RatingRepository", "Erro ao deletar rating: ${e.message}")
            false
        }
    }

    /**
     * Get average rating for a material
     */
    suspend fun getAverageRating(materialId: Long): Double = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("RatingRepository", "Calculando média de ratings para material: $materialId")

            val ratings = getMaterialRatings(materialId)
            if (ratings.isEmpty()) {
                return@withContext 0.0
            }

            val average = ratings.mapNotNull { it.rating }.average()
            Log.d("RatingRepository", "Média de ratings: $average")
            average
        } catch (e: Exception) {
            Log.e("RatingRepository", "Erro ao calcular média de ratings: ${e.message}")
            0.0
        }
    }

    /**
     * Get average rating for all materials uploaded by a user
     */
    suspend fun getAverageRatingForUserMaterials(userId: String): Double = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("RatingRepository", "Calculando média de ratings para materiais do usuário: $userId")

            // Primeiro, buscar todos os materiais do usuário
            val userMaterials = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        eq("author_id", userId)
                    }
                }
                .decodeList<Material>()

            if (userMaterials.isEmpty()) {
                Log.d("RatingRepository", "Usuário não possui materiais")
                return@withContext 0.0
            }

            // Para cada material, buscar suas avaliações
            val allRatings = mutableListOf<Long>()

            userMaterials.forEach { material ->
                material.id?.let { materialId ->
                    val ratings = getMaterialRatings(materialId)
                    ratings.mapNotNull { it.rating }.let { allRatings.addAll(it) }
                }
            }

            if (allRatings.isEmpty()) {
                Log.d("RatingRepository", "Nenhuma avaliação encontrada para os materiais do usuário")
                return@withContext 0.0
            }

            val average = allRatings.average()
            Log.d("RatingRepository", "Média de ratings para materiais do usuário: $average")
            average
        } catch (e: Exception) {
            Log.e("RatingRepository", "Erro ao calcular média de ratings dos materiais do usuário: ${e.message}")
            0.0
        }
    }

    /**
     * Get total number of materials uploaded by a user
     */
    suspend fun getUserContributions(userId: String): Int = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("RatingRepository", "Calculando total de contribuições do usuário: $userId")

            val result = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        eq("author_id", userId)
                    }
                }
                .decodeList<Material>()

            Log.d("RatingRepository", "Total de contribuições: ${result.size}")
            result.size
        } catch (e: Exception) {
            Log.e("RatingRepository", "Erro ao calcular total de contribuições: ${e.message}")
            0
        }
    }
} 