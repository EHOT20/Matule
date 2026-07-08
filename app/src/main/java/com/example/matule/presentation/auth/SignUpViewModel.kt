package com.example.matule.presentation.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.matule.common.BaseViewModel
import com.example.matule.domain.models.User
import com.example.matule.domain.repositories.AuthRepository
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _registrationResult = MutableLiveData<Result<User>?>()
    val registrationResult: LiveData<Result<User>?> = _registrationResult

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.register(email, password)
            _isLoading.value = false
            _registrationResult.value = result
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Ошибка регистрации"
            }
        }
    }

    fun validateEmail(email: String): Boolean {
        val pattern = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")
        return pattern.matches(email)
    }

    fun openPrivacyPolicy(context: Context) {
        // Здесь будет загрузка PDF (реализуем позже)
        android.widget.Toast.makeText(context, "Политика конфиденциальности (PDF)", android.widget.Toast.LENGTH_SHORT).show()
    }
}