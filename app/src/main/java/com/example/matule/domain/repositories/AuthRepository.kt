package com.example.matule.domain.repositories

import com.example.matule.domain.models.User

/**
 * Интерфейс репозитория для работы с аутентификацией.
 * Дата создания: 2026-07-05
 * Автор: разработчик
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
}