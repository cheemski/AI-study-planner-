package com.example.studyplannerapp.network

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient

/**
 * Single shared Supabase client for the whole app.
 * Only the ANON / PUBLIC key belongs here, never the service role key.
 *
 * TODO: replace the two placeholders below with your real project values
 * from Supabase dashboard -> Project Settings -> API.
 */
object SupabaseClient {

    private const val SUPABASE_URL = "https://lhwyvbcrmswfvnmwwdxa.supabase.co/"
    private const val SUPABASE_ANON_KEY = "sb_publishable_2zIippoNDNE4l-jX-nHysg_mSs2KX2Y"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth) {
            // Deep link used to catch the password reset email link.
            // Must match the intent-filter in AndroidManifest.xml.
            scheme = "studyplanner"
            host = "reset-callback"
        }
    }

    val auth get() = client.auth
}
