package com.example.storyapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.remote.Result
import com.example.storyapp.data.repository.AuthenticationRepositoryImpl
import com.example.storyapp.data.repository.UploadStoryRepositoryImpl
import com.example.storyapp.databinding.ActivityAddStoriesBinding
import com.example.storyapp.util.LoadingDialog
import com.example.storyapp.util.reduceFileImage
import com.example.storyapp.util.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoriesBinding
    private val loadingDialog = LoadingDialog(this)
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Float = 0F
    private var lon: Float = 0f
    private lateinit var dialog: AlertDialog

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.add_stories_activity_title)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val uploadStoryRepository = UploadStoryRepositoryImpl()
        val uploadStoryViewModelFactory = UploadStoryViewModelFactory(uploadStoryRepository)

        val uploadStoryViewModel = ViewModelProvider(this, uploadStoryViewModelFactory)[UploadStoryViewModel::class.java]

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.takepicBtn.setOnClickListener {
            startTakePhoto()
        }

        binding.takegalleryBtn.setOnClickListener {
            startGallery()
        }

        binding.uploadBtn.setOnClickListener {
            uploadStory()
        }

        uploadStoryViewModel.response.observe(this) { response ->
            when (response) {
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Upload Berhasil!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Upload gagal!", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    showLoading(true)
                }
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.cbLocation.setOnClickListener {
            if (binding.cbLocation.isChecked) {
                getMyLastLocation()
            }
        }

    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun getMyLastLocation() {
        if     (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude.toFloat()
                    lon = location.longitude.toFloat()
                } else {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.WrapContentDialog)
                    val inflater: LayoutInflater = layoutInflater
                    builder.setView(inflater.inflate(R.layout.no_location_permission_dialog, null))
                    dialog = builder.create()
                    dialog.show()
                    binding.cbLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)

            binding.ivPlaceholder.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoriesActivity)
            val file = reduceFileImage(myFile)
            getFile = file
            binding.ivPlaceholder.setImageURI(selectedImg)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        com.example.storyapp.util.createTempFile(application).also {
            val photoURI: Uri =
                FileProvider.getUriForFile(this@AddStoriesActivity, "com.example.storyapp", it)

            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)

        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadStory() {
        if (getFile != null) {

            val file = reduceFileImage(getFile as File)

            val description = binding.etDescription.text.toString().trim()
                .toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val pref = LoginPreferencesImpl.getInstance(dataStore)
            val authenticationRepository = AuthenticationRepositoryImpl()
            val loginViewModel =
                ViewModelProvider(this, LoginViewModelFactory(pref, authenticationRepository))[LoginViewModel::class.java]

            val uploadStoryRepository = UploadStoryRepositoryImpl()
            val uploadStoryViewModelFactory = UploadStoryViewModelFactory(uploadStoryRepository)

            val uploadStoryViewModel = ViewModelProvider(this, uploadStoryViewModelFactory)[UploadStoryViewModel::class.java]

            if (binding.cbLocation.isChecked) {
                loginViewModel.getUserLoginData().observe(this) { token: String ->
                    uploadStoryViewModel.uploadStory(token, imageMultipart, description, lat, lon)
                }

            }else {
                loginViewModel.getUserLoginData().observe(this) { token: String ->
                    uploadStoryViewModel.uploadStory(token, imageMultipart, description)
                }

            }

        } else {
            Toast.makeText(
                this@AddStoriesActivity,
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        loadingDialog.dismissDialog()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        loadingDialog.dismissDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismissDialog()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.startLoadingDialog()
        } else {
            loadingDialog.dismissDialog()
        }
    }
}