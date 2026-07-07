package com.example.matule.domain.models

/**
 * Модель пользователя.
 * Дата создания: 2026-07-05
 * Автор: разработчик
 */
data class User(
    val id: String,
    val email: String,
    val name: String? = null
)