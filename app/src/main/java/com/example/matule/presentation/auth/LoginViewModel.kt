package com.example.matule.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.matule.common.BaseViewModel
import com.example.matule.domain.models.User
import com.example.matule.domain.repositories.AuthRepository
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана входа.
 * Обрабатывает авторизацию, валидацию и состояние загрузки.
 * Дата создания: 2026-07-05
 * Автор: разработчик
 */
class LoginViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _loginResult = MutableLiveData<Result<User>?>()
    val loginResult: LiveData<Result<User>?> = _loginResult

    /**
     * Выполняет вход пользователя.
     * @param email Email пользователя
     * @param password Пароль
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.login(email, password)
            _isLoading.value = false
            _loginResult.value = result
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
            }
        }
    }

    /**
     * Проверяет email на соответствие паттерну.
     * Паттерн: только маленькие латинские буквы и цифры, домен >2 символов.
     */
    fun validateEmail(email: String): Boolean {
        val pattern = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")
        return pattern.matches(email)
    }

    /**
     * Проверяет пароль на пустоту.
     */
    fun validatePassword(password: String): Boolean {
        return password.isNotBlank()
    }
}