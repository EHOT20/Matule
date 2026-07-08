package com.example.matule.presentation.checkout

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.matule.R
import com.example.matule.databinding.FragmentCheckoutBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class CheckoutFragment : Fragment() {

    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var viewModel: CheckoutViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequest = LocationRequest.Builder(
        LocationRequest.PRIORITY_HIGH_ACCURACY, 10000
    ).setMinUpdateDistanceMeters(10f).build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            if (location != null) {
                viewModel.updateLocation(location)
                updateAddress(location)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(CheckoutViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Явно разрешаем редактирование (на всякий случай)
        binding.etPhone.isEnabled = true
        binding.etEmail.isEnabled = true
        binding.etAddress.isEnabled = true

        setupListeners()
        observeViewModel()
        loadProfileData()
        checkLocationPermission()
    }

    private fun setupListeners() {
        binding.btnConfirm.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()

            if (phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.confirmOrder(phone, email, address)
        }

        binding.etPhone.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.savePhone(binding.etPhone.text.toString())
            }
        }

        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.saveEmail(binding.etEmail.text.toString())
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        viewModel.confirmationResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                showConfirmationDialog()
            }
        }
    }

    private fun loadProfileData() {
        viewModel.loadProfileData { phone, email, address ->
            binding.etPhone.setText(phone)
            binding.etEmail.setText(email)
            binding.etAddress.setText(address)
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            loadProfileData()
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun updateAddress(location: Location) {
        val address = "Координаты: ${location.latitude}, ${location.longitude}"
        binding.etAddress.setText(address)
        viewModel.saveAddress(address)
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Подтверждение заказа")
            .setMessage("Заказ оформлен успешно! Спасибо за покупку.")
            .setPositiveButton("Вернуться к покупкам") { _, _ ->
                findNavController().navigate(R.id.action_checkout_to_home)
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}