package com.example.matule.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("cart", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addProduct(productId: String) {
        val cart = getCart().toMutableList()
        cart.add(productId)   // всегда добавляем, даже если уже есть
        saveCart(cart)
    }

    fun removeProduct(productId: String) {
        val cart = getCart().toMutableList()
        cart.remove(productId)
        saveCart(cart)
    }

    fun getCart(): List<String> {
        val json = prefs.getString("cart_items", "[]")
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clearCart() {
        saveCart(emptyList())
    }

    private fun saveCart(cart: List<String>) {
        val json = gson.toJson(cart)
        prefs.edit().putString("cart_items", json).apply()
    }
}