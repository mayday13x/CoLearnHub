package com.example.colearnhub.repositoryLayer

import android.util.Log
import com.example.colearnhub.modelLayer.LanguageData
import com.example.colearnhub.modelLayer.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LanguageRepository {

    /**
     * Busca uma language por ID
     */
    suspend fun getLanguageById(languageId: Long): LanguageData? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("LanguageRepository", "Buscando language ID: $languageId")

            val result = SupabaseClient.client
                .from("Language")
                .select {
                    filter {
                        eq("id", languageId)
                    }
                }
                .decodeSingleOrNull<LanguageData>()

            if (result != null) {
                Log.d("LanguageRepository", "Language encontrada: ${result.language}")
            } else {
                Log.w("LanguageRepository", "Language $languageId não encontrada")
            }

            result
        } catch (e: Exception) {
            Log.e("LanguageRepository", "Erro ao buscar language: ${e.message}")
            null
        }
    }

    /**
     * Busca todas as languages
     */
    suspend fun getAllLanguages(): List<LanguageData> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = SupabaseClient.client
                .from("Language")
                .select()
                .decodeList<LanguageData>()

            Log.d("LanguageRepository", "Encontradas ${result.size} language")
            result
        } catch (e: Exception) {
            Log.e("LanguageRepository", "Erro ao buscar languages: ${e.message}")
            emptyList()
        }
    }

    /**
     * Mapeia ID para nome e bandeira
     */
    fun getLanguageDisplayInfo(languageId: Long?): Pair<String, String> {
        return when (languageId) {
            1L -> Pair("Português", "🇵🇹")
            2L -> Pair("Inglês", "🇬🇧")
            else -> Pair("Outra", "🌍")
        }
    }

    /**
     * Mapeia nome para ID
     */
    fun getLanguageIdByName(languageName: String): Long {
        return when (languageName.lowercase()) {
            "português", "portuguese" -> 1L
            "english", "inglês" -> 2L
            else -> 3L // Outra
        }
    }
}