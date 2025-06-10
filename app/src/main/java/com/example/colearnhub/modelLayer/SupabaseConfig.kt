package com.example.colearnhub.modelLayer

import com.example.colearnhub.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient{
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.supabaseurl,
        supabaseKey = BuildConfig.supabasekey
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)

    }
}