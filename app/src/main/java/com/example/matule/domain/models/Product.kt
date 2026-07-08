package com.example.matule.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String,
    val title: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val isFavorite: Boolean = false
) : Parcelable