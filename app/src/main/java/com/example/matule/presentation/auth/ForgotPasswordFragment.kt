package com.example.matule.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.matule.R
import com.example.matule.common.BaseFragment
import com.example.matule.databinding.FragmentForgotPasswordBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {

    private val viewModel: ForgotPasswordViewModel by viewModel()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentForgotPasswordBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSend.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (viewModel.validateEmail(email)) {
                viewModel.sendResetCode(email)
            } else {
                showErrorDialog("Введите корректный email")
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_forgot_to_login)
        }

        observeViewModel()
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

        viewModel.sendResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                showSuccessDialog(
                    "Код отправлен",
                    "На ваш email отправлен код для восстановления пароля"
                ) {
                    findNavController().navigate(R.id.action_forgot_to_otp)
                }
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

    private fun showSuccessDialog(title: String, message: String, onOk: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> onOk() }
            .show()
    }
}