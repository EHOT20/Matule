package com.example.matule.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.matule.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OTPVerificationViewModel : BaseViewModel() {

    private val _verificationResult = MutableLiveData<Result<Unit>?>()
    val verificationResult: LiveData<Result<Unit>?> = _verificationResult

    fun verifyOTP(otp: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)
            _isLoading.value = false
            if (otp == "1234") { // правильный код
                _verificationResult.value = Result.success(Unit)
            } else {
                _error.value = "Неверный код"
            }
        }
    }

    fun resendCode() {
        // Имитация повторной отправки
        viewModelScope.launch {
            delay(500)
            android.widget.Toast.makeText(
                android.app.Application().applicationContext,
                "Новый код отправлен",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun generatePasswordFromPhrase(phrase: String): String {
        // Замена символов на похожие: I -> 1, o -> 0, e -> 3, a -> @ и т.д.
        val map = mapOf(
            'I' to '1', 'i' to '1',
            'O' to '0', 'o' to '0',
            'E' to '3', 'e' to '3',
            'A' to '@', 'a' to '@',
            'T' to '7', 't' to '7',
            'S' to '5', 's' to '5'
        )
        return phrase.map { map[it] ?: it }.joinToString("")
    }
}