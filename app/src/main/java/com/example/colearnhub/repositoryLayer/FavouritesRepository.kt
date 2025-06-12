package com.example.colearnhub.repositoryLayer

import android.util.Log
import com.example.colearnhub.modelLayer.FavouritesData
import com.example.colearnhub.modelLayer.Material
import com.example.colearnhub.modelLayer.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavouritesRepository {
    /**
     * Add a material to user's favorites
     */
    suspend fun addToFavourites(userId: String, materialId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("FavouritesRepository", "Adding material $materialId to favorites for user $userId")

            val favouriteData = FavouritesData(
                user_id = userId,
                material_id = materialId
            )

            SupabaseClient.client
                .from("Favourites")
                .insert(favouriteData)

            Log.d("FavouritesRepository", "Material added to favorites successfully")
            true
        } catch (e: Exception) {
            Log.e("FavouritesRepository", "Error adding material to favorites: ${e.message}")
            false
        }
    }

    /**
     * Remove a material from user's favorites
     */
    suspend fun removeFromFavourites(userId: String, materialId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("FavouritesRepository", "Removing material $materialId from favorites for user $userId")

            SupabaseClient.client
                .from("Favourites")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("material_id", materialId)
                    }
                }

            Log.d("FavouritesRepository", "Material removed from favorites successfully")
            true
        } catch (e: Exception) {
            Log.e("FavouritesRepository", "Error removing material from favorites: ${e.message}")
            false
        }
    }

    /**
     * Check if a material is in user's favorites
     */
    suspend fun isFavourite(userId: String, materialId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("FavouritesRepository", "Checking if material $materialId is in favorites for user $userId")

            val result = SupabaseClient.client
                .from("Favourites")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("material_id", materialId)
                    }
                }
                .decodeList<FavouritesData>()

            result.isNotEmpty()
        } catch (e: Exception) {
            Log.e("FavouritesRepository", "Error checking favorite status: ${e.message}")
            false
        }
    }

    /**
     * Get all favorite materials for a user
     */
    suspend fun getUserFavourites(userId: String): List<Material> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("FavouritesRepository", "Getting favorites for user $userId")

            // First get the favorite material IDs
            val favorites = SupabaseClient.client
                .from("Favourites")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<FavouritesData>()

            if (favorites.isEmpty()) {
                return@withContext emptyList()
            }

            // Get the material IDs
            val materialIds = favorites.map { it.material_id }

            // Get the materials
            val materials = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        isIn("id", materialIds)
                    }
                }
                .decodeList<Material>()

            Log.d("FavouritesRepository", "Found ${materials.size} favorite materials")
            materials
        } catch (e: Exception) {
            Log.e("FavouritesRepository", "Error getting user favorites: ${e.message}")
            emptyList()
        }
    }
} 