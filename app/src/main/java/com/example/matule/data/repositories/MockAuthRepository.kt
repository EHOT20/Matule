package com.example.matule.data.repositories

import com.example.matule.domain.models.User
import com.example.matule.domain.repositories.AuthRepository

class MockAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        // Имитация успешного входа
        return Result.success(User("123", "test@example.com", "Test User"))
    }

    override suspend fun register(email: String, password: String): Result<User> {
        return Result.success(User("456", "test@example.com", "Test User"))
    }

    override suspend fun logout(): Result<Unit> {
        return Result.success(Unit)
    }
}