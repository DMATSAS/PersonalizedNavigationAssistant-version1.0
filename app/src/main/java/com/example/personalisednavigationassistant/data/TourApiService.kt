package com.example.personalisednavigationassistant.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class PoiRequest(val poi_id: String, val user_language: String, val userMessage: String? = null)
data class StoryResponse(val story: String)

interface N8nApiService {
    @POST("webhook/Tour-Guide")
    suspend fun getStory(@Body request: PoiRequest): StoryResponse
}

object RetrofitClient {
    private const val BASE_URL = " https://customary-stapling-image.ngrok-free.dev"
    val apiService: N8nApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(N8nApiService::class.java)
    }
}