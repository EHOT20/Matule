package com.example.matule.presentation.splash

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel(private val context: Context) : ViewModel() {

    private val _navigationEvent = MutableLiveData<SplashDestination>()
    val navigationEvent: LiveData<SplashDestination> = _navigationEvent

    fun checkFirstLaunch() {
        // Проверяем, был ли показан онбординг
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val onboardingShown = prefs.getBoolean("onboarding_shown", false)

        _navigationEvent.value = if (onboardingShown) {
            SplashDestination.MAIN
        } else {
            SplashDestination.ONBOARDING
        }
    }
}