package com.example.matule.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Базовый ViewModel с общей логикой загрузки и ошибок.
 * Дата создания: 2026-07-05
 * Автор: разработчик
 */
abstract class BaseViewModel : ViewModel() {
    protected val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    protected val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun clearError() {
        _error.value = null
    }
}