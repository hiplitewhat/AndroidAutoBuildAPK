package com.example.animetracker.network

import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object AnimeApiService {

    private const val TAG = "AnimeApiService"
    private const val BASE_URL = "https://api.jikan.moe/v4"

    /**
     * Check if an anime has a new episode
     * Uses Jikan API (MyAnimeList unofficial API)
     */
    suspend fun checkNewEpisode(animeId: Int): Boolean {
        return try {
            val url = URL("$BASE_URL/anime/$animeId")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(response)
                val data = json.getJSONObject("data")
                val isAiring = data.getString("status") == "Currently Airing"

                Log.d(TAG, "Anime $animeId — airing: $isAiring")
                isAiring
            } else {
                Log.w(TAG, "API returned code: $responseCode")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking anime $animeId: ${e.message}")
            false
        }
    }

    /**
     * Fetch anime details by ID
     */
    suspend fun getAnimeDetails(animeId: Int): AnimeDetails? {
        return try {
            val url = URL("$BASE_URL/anime/$animeId")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(response).getJSONObject("data")

                AnimeDetails(
                    id = json.getInt("mal_id"),
                    title = json.getString("title"),
                    episodes = json.optInt("episodes", 0),
                    status = json.getString("status"),
                    imageUrl = json.getJSONObject("images")
                        .getJSONObject("jpg")
                        .getString("image_url")
                )
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching details: ${e.message}")
            null
        }
    }
}

data class AnimeDetails(
    val id: Int,
    val title: String,
    val episodes: Int,
    val status: String,
    val imageUrl: String
)