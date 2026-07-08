package com.example.matule.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.matule.R
import com.example.matule.common.BaseFragment
import com.example.matule.databinding.FragmentSignUpBinding
import com.example.matule.presentation.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    private val viewModel: SignUpViewModel by viewModel()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSignUpBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val agreed = binding.checkboxAgree.isChecked

            if (!agreed) {
                showErrorDialog("Необходимо согласие с условиями")
                return@setOnClickListener
            }

            if (!viewModel.validateEmail(email)) {
                showErrorDialog("Некорректный email")
                return@setOnClickListener
            }

            if (password.length < 6) {
                showErrorDialog("Пароль должен быть не менее 6 символов")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showErrorDialog("Пароли не совпадают")
                return@setOnClickListener
            }

            viewModel.register(email, password)
        }

        binding.tvLoginLink.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login)
        }

        binding.tvPrivacyPolicy.setOnClickListener {
            viewModel.openPrivacyPolicy(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showErrorDialog(it)
                viewModel.clearError()
            }
        }

        viewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), "Регистрация успешна", Toast.LENGTH_SHORT).show()
                // Переход на главный экран
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}