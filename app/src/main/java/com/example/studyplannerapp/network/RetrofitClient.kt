package com.example.studyplannerapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit setup for the AI backend (your local dev server).
 *
 * IMPORTANT for the Android emulator:
 *   - 10.0.2.2 is a special alias that points to your computer's localhost.
 *     So a server running on your PC at localhost:8000 is reached here as
 *     http://10.0.2.2:8000/ .
 *   - On a REAL phone use your PC's LAN IP instead, e.g. http://192.168.1.20:8000/ .
 *
 * TODO: change BASE_URL to match the port your AI server actually runs on.
 * AI responses can be slow, so the timeouts below are generous.
 */
object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8000/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: APIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }
}
