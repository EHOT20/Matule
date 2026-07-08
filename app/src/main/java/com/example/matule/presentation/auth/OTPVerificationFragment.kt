package com.example.matule.presentation.auth

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.matule.R
import com.example.matule.common.BaseFragment
import com.example.matule.databinding.FragmentOtpVerificationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class OTPVerificationFragment : BaseFragment<FragmentOtpVerificationBinding>() {

    private val viewModel: OTPVerificationViewModel by viewModel()
    private lateinit var timer: CountDownTimer

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentOtpVerificationBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOTPInputs()
        startTimer()

        binding.btnVerify.setOnClickListener {
            val otp = getOTP()
            if (otp.length == 4) {
                viewModel.verifyOTP(otp)
            } else {
                showErrorDialog("Введите 4-значный код")
            }
        }

        binding.btnResend.setOnClickListener {
            if (binding.btnResend.isEnabled) {
                viewModel.resendCode()
                startTimer()
            }
        }

        observeViewModel()
    }

    private fun setupOTPInputs() {
        // Слушатели для автоматического перехода между полями
        val inputs = listOf(
            binding.otp1, binding.otp2, binding.otp3, binding.otp4
        )
        inputs.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && index < 3) {
                        inputs[index + 1].requestFocus()
                    }
                }
                override fun afterTextChanged(s: android.text.Editable?) {}
            })
        }
    }

    private fun getOTP(): String {
        return buildString {
            append(binding.otp1.text)
            append(binding.otp2.text)
            append(binding.otp3.text)
            append(binding.otp4.text)
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.tvTimer.text = "01:${String.format("%02d", seconds)}"
                binding.btnResend.isEnabled = false
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                binding.btnResend.isEnabled = true
            }
        }.start()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showErrorDialog(it)
                viewModel.clearError()
                // Подсвечиваем все поля красным
                listOf(
                    binding.otp1, binding.otp2, binding.otp3, binding.otp4
                ).forEach { it.setBackgroundResource(R.drawable.bg_otp_error) }
            }
        }

        viewModel.verificationResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                // Успешная верификация – генерируем пароль
                val newPassword = viewModel.generatePasswordFromPhrase("I_p0Mn|O_4y9n0e Mg№vEn|E")
                showPasswordDialog(newPassword)
            }
        }
    }

    private fun showPasswordDialog(password: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Ваш новый пароль")
            .setMessage("Скопируйте пароль: \n\n$password")
            .setPositiveButton("OK") { _, _ ->
                findNavController().navigate(R.id.action_otp_to_login)
            }
            .setNeutralButton("Копировать") { _, _ ->
                val clipboard = requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("password", password)
                clipboard.setPrimaryClip(clip)
                android.widget.Toast.makeText(requireContext(), "Пароль скопирован", android.widget.Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}