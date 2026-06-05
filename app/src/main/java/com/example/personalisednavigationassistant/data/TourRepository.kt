package com.example.personalisednavigationassistant.data

class TourRepository {
    suspend fun fetchStory(poiId: String, language: String, userMessage: String? = null): Result<String> {
        return try {

            val request = PoiRequest(
                poi_id = poiId,
                user_language = language,
                userMessage = userMessage
            )
            val response = RetrofitClient.apiService.getStory(request)


            Result.success(response.story)
        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}