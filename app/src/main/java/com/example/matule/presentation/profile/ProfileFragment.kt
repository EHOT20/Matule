package com.example.matule.presentation.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.matule.R
import com.example.matule.databinding.FragmentProfileBinding
import com.example.matule.presentation.barcode.BarcodeActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private var isEditing = false

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data
            imageUri?.let { uri ->
                binding.ivAvatar.setImageURI(uri)
                viewModel.saveAvatarUri(uri.toString())
            }
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Разрешение на камеру не получено", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        loadProfileData()
        generateBarcode()
        setupListeners()
        observeViewModel()
    }

    private fun loadProfileData() {
        viewModel.loadProfile { name, phone, email, avatarUri ->
            binding.etName.setText(name)
            binding.etPhone.setText(phone)
            binding.etEmail.setText(email)
            avatarUri?.let { uri ->
                binding.ivAvatar.load(uri) {
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                }
            }
        }
    }

    private fun generateBarcode() {
        val userId = viewModel.getUserId()
        val barcodeBitmap = generateBarcodeBitmap(userId)
        binding.ivBarcode.setImageBitmap(barcodeBitmap)
        binding.ivBarcode.setOnClickListener {
            val intent = Intent(requireContext(), BarcodeActivity::class.java)
            intent.putExtra("user_id", viewModel.getUserId())
            startActivity(intent)
        }
    }

    private fun generateBarcodeBitmap(data: String): Bitmap {
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(data, BarcodeFormat.CODE_128, 500, 200)
        val encoder = BarcodeEncoder()
        return encoder.createBitmap(bitMatrix)
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            if (!isEditing) {
                enableEditing(true)
                isEditing = true
                binding.btnEditProfile.text = "Отмена"
                binding.btnSaveProfile.visibility = View.VISIBLE
            } else {
                enableEditing(false)
                isEditing = false
                binding.btnEditProfile.text = "Редактировать"
                binding.btnSaveProfile.visibility = View.GONE
                loadProfileData()
            }
        }

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            viewModel.saveProfile(name, phone, email)
            enableEditing(false)
            isEditing = false
            binding.btnEditProfile.text = "Редактировать"
            binding.btnSaveProfile.visibility = View.GONE
            Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
        }

        binding.btnChangeAvatar.setOnClickListener {
            showAvatarDialog()
        }

        binding.ivAvatar.setOnClickListener {
            showAvatarDialog()
        }
    }

    private fun enableEditing(enabled: Boolean) {
        binding.etName.isEnabled = enabled
        binding.etPhone.isEnabled = enabled
        binding.etEmail.isEnabled = enabled
    }

    private fun showAvatarDialog() {
        val options = arrayOf("Камера", "Галерея", "Кандинский")
        AlertDialog.Builder(requireContext())
            .setTitle("Изменить фото")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                    2 -> generateKandinskyAvatar()
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(intent, 100)
        } else {
            Toast.makeText(requireContext(), "Камера недоступна", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun generateKandinskyAvatar() {
        Toast.makeText(requireContext(), "Генерация через Kandinsky (заглушка)", Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), "Данные сохранены", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.ivAvatar.setImageBitmap(imageBitmap)
            viewModel.saveAvatarUri("") // можно сохранить URI
        }
    }
}