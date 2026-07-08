package com.example.matule.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.matule.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : BaseViewModel() {

    private val _sendResult = MutableLiveData<Result<Unit>?>()
    val sendResult: LiveData<Result<Unit>?> = _sendResult

    fun sendResetCode(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Имитация отправки кода (в реальном проекте – запрос к серверу)
            delay(1500)
            _isLoading.value = false
            _sendResult.value = Result.success(Unit)
        }
    }

    fun validateEmail(email: String): Boolean {
        val pattern = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")
        return pattern.matches(email)
    }
}