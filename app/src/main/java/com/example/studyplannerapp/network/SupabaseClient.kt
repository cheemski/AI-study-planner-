package com.example.authdemo.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth

/**
 * Single shared Supabase client for the whole app.
 * Only the ANON/PUBLIC key belongs here — never the service role key.
 */
object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://YOUR_PROJECT_REF.supabase.co",
        supabaseKey = "YOUR_ANON_PUBLIC_KEY"
    ) {
        install(Auth) {
            // Deep link scheme used to catch the password-reset email link.
            // Must match the intent-filter in AndroidManifest.xml
            scheme = "authdemo"
            host = "reset-callback"
        }
    }

    val auth get() = client.auth
}
