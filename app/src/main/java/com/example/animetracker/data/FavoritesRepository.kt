package com.example.animetracker.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Anime(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val lastEpisode: Int = 0
)

object FavoritesRepository {

    private const val PREFS_NAME = "anime_favorites"
    private const val KEY_FAVORITES = "favorites_list"
    private val gson = Gson()

    fun getFavorites(context: Context): List<Anime> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_FAVORITES, null) ?: return emptyList()
        val type = object : TypeToken<List<Anime>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addFavorite(context: Context, anime: Anime) {
        val favorites = getFavorites(context).toMutableList()
        if (favorites.none { it.id == anime.id }) {
            favorites.add(anime)
            saveFavorites(context, favorites)
        }
    }

    fun removeFavorite(context: Context, animeId: Int) {
        val favorites = getFavorites(context).toMutableList()
        favorites.removeAll { it.id == animeId }
        saveFavorites(context, favorites)
    }

    fun isFavorite(context: Context, animeId: Int): Boolean {
        return getFavorites(context).any { it.id == animeId }
    }

    private fun saveFavorites(context: Context, favorites: List<Anime>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(favorites)
        prefs.edit().putString(KEY_FAVORITES, json).apply()
    }
}