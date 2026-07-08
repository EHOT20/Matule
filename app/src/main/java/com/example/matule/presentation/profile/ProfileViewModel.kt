package com.example.matule.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _saveResult = MutableLiveData<Result<Unit>?>()
    val saveResult: LiveData<Result<Unit>?> = _saveResult

    fun loadProfile(callback: (String, String, String, String?) -> Unit) {
        callback("Иван Иванов", "+7 999 123-45-67", "ivan@example.com", null)
    }

    fun saveProfile(name: String, phone: String, email: String) {
        _saveResult.value = Result.success(Unit)
    }

    fun saveAvatarUri(uri: String) {}

    fun getUserId(): String {
        return "12345"
    }
}