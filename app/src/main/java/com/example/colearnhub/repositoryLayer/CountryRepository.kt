package com.example.colearnhub.repositoryLayer;

import com.example.colearnhub.modelLayer.Country;
import com.example.colearnhub.modelLayer.SupabaseClient
import io.github.jan.supabase.postgrest.from

class CountryRepository {

    /**
     * Get all countries
     */

    suspend fun getAllCountries(): List<Country> {
        return SupabaseClient.client
            .from("Country")
            .select()
            .decodeList()
    }

    /**
     * get Country by id
     */

    suspend fun getCountryById(id: Int): Country? {
        return SupabaseClient.client
            .from("Country")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull()
    }

}
