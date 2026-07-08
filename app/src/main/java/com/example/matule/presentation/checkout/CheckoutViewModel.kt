package com.example.matule.presentation.checkout

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _confirmationResult = MutableLiveData<Result<Unit>?>()
    val confirmationResult: LiveData<Result<Unit>?> = _confirmationResult

    private var currentPhone = ""
    private var currentEmail = ""
    private var currentAddress = ""

    fun loadProfileData(callback: (String, String, String) -> Unit) {
        callback(
            "+7 999 123-45-67",
            "user@example.com",
            "г. Москва, ул. Тверская, д. 1"
        )
    }

    fun savePhone(phone: String) {
        currentPhone = phone
    }

    fun saveEmail(email: String) {
        currentEmail = email
    }

    fun saveAddress(address: String) {
        currentAddress = address
    }

    fun updateLocation(location: Location) {
        val address = "Координаты: ${location.latitude}, ${location.longitude}"
        currentAddress = address
    }

    fun confirmOrder(phone: String, email: String, address: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500)
            _isLoading.value = false
            _confirmationResult.value = Result.success(Unit)
        }
    }

    fun clearError() {
        _error.value = null
    }
}