package com.example.matule.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoritesRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addFavorite(productId: String) {
        val favorites = getFavorites().toMutableList()
        if (!favorites.contains(productId)) {
            favorites.add(productId)
        }
        saveFavorites(favorites)
    }

    fun removeFavorite(productId: String) {
        val favorites = getFavorites().toMutableList()
        favorites.remove(productId)
        saveFavorites(favorites)
    }

    fun getFavorites(): List<String> {
        val json = prefs.getString("favorites_items", "[]")
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clearFavorites() {
        saveFavorites(emptyList())
    }

    private fun saveFavorites(favorites: List<String>) {
        val json = gson.toJson(favorites)
        prefs.edit().putString("favorites_items", json).apply()
    }
}