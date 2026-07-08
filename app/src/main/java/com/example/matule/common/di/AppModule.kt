package com.example.matule.common.di

import com.example.matule.data.repositories.MockAuthRepository
import com.example.matule.domain.repositories.AuthRepository
import com.example.matule.presentation.auth.ForgotPasswordViewModel
import com.example.matule.presentation.auth.LoginViewModel
import com.example.matule.presentation.auth.OTPVerificationViewModel
import com.example.matule.presentation.auth.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<AuthRepository> { MockAuthRepository() }

    viewModel { LoginViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { ForgotPasswordViewModel() }
    viewModel { OTPVerificationViewModel() }
}