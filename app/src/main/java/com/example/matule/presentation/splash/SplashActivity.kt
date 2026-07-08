package com.example.matule.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.matule.presentation.auth.LoginActivity
import com.example.matule.presentation.main.MainActivity
import com.example.matule.presentation.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return SplashViewModel(applicationContext) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { true }

        viewModel.checkFirstLaunch()

        viewModel.navigationEvent.observe(this) { destination ->
            splashScreen.setKeepOnScreenCondition { false }
            when (destination) {
                SplashDestination.ONBOARDING -> {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                }
                SplashDestination.LOGIN -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                SplashDestination.MAIN -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            finish()
        }
    }
}

sealed class SplashDestination {
    object ONBOARDING : SplashDestination()
    object LOGIN : SplashDestination()
    object MAIN : SplashDestination()
}