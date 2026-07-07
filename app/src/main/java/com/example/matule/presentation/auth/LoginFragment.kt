package com.example.matule.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.matule.R
import com.example.matule.common.BaseFragment
import com.example.matule.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Фрагмент экрана входа.
 * Обрабатывает ввод данных, валидацию и вызов ViewModel.
 * Дата создания: 2026-07-05
 * Автор: разработчик
 */
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val viewModel: LoginViewModel by viewModel()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLoginBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        // Кнопка входа
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (validate(email, password)) {
                viewModel.login(email, password)
            }
        }

        // Кнопка "Забыли пароль?" – пока тост
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Переход на восстановление пароля", Toast.LENGTH_SHORT).show()
        }

        // Кнопка "Создать пользователя" – пока тост
        binding.tvSignUp.setOnClickListener {
            Toast.makeText(requireContext(), "Переход на регистрацию", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Валидация полей: email по паттерну, пароль не пустой.
     * При ошибке показывает диалог.
     * @return true, если валидация пройдена
     */
    private fun validate(email: String, password: String): Boolean {
        if (!viewModel.validateEmail(email)) {
            showErrorDialog("Некорректный email. Формат: name@domen.ru")
            return false
        }
        if (!viewModel.validatePassword(password)) {
            showErrorDialog("Пароль не может быть пустым")
            return false
        }
        return true
    }

    /**
     * Показывает диалоговое окно с ошибкой.
     */
    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun observeViewModel() {
        // Индикация загрузки
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !loading
        }

        // Ошибки от сервера или сети
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showErrorDialog(it)
                viewModel.clearError()
            }
        }

        // Результат авторизации
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), "Вход выполнен", Toast.LENGTH_SHORT).show()
                // TODO: переход на экран Home (реализуем после настройки навигации)
                // findNavController().navigate(R.id.action_login_to_home)
            }
        }
    }
}