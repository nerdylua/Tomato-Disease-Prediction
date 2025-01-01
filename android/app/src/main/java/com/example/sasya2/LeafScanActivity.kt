package com.example.sasya2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sasya2.databinding.ActivityLeafScanBinding
import com.example.sasya2.ml.Model
import com.example.sasya2.ui.home.HomeFragment
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min

class LeafScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeafScanBinding
    private val imageSize = 224
    private val classes = arrayOf("Tomato_healthy", "Tomato_Leaf_Mold", "Tomato_Early_blight", "Tomato_Late_blight", "Tomato_Bacterial_spot", "Potato_healthy", "Potato_Late_blight", "Potato_Early_blight", "Pepper_Bacterial_spot", "Pepper_healthy", "Grape_healthy", "Grape_Leaf_blight", "Grape_Black_rot", "Cherry_healthy", "Cherry_Powdery_mildew", "Apple_healthy", "Apple_Black_rot", "Apple_Apple_scab")
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) launchCamera() else showError("Camera permission required")
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.extras?.get("data")?.let { image ->
            processAndClassifyImage(image as Bitmap)
        } ?: showError("Failed to capture image")
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            result.data?.data?.let { uri ->
                val image = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                processAndClassifyImage(image)
            }
        } catch (e: Exception) {
            showError("Failed to load image: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeafScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(binding) {
            backButton.setOnClickListener { navigateToHome() }
            captureButton.setOnClickListener { checkCameraPermission() }
            galleryButton.setOnClickListener { openGallery() }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> launchCamera()
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun navigateToHome() {
        Intent(this, HomeFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            finish()
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).let {
            galleryLauncher.launch(it)
        }
    }

    private fun launchCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { cameraLauncher.launch(it) }
    }

    private fun processAndClassifyImage(image: Bitmap) {
        showLoading(true)
        try {
            val dimension = min(image.width, image.height)
            val squareImage = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
            binding.imageView.setImageBitmap(squareImage)

            val resizedImage = Bitmap.createScaledBitmap(squareImage, imageSize, imageSize, false)
            classifyImage(resizedImage)
        } finally {
            showLoading(false)
        }
    }

    private fun classifyImage(image: Bitmap) {
        try {
            val model = Model.newInstance(applicationContext)
            try {
                val inputFeature0 = createInputFeature(image)
                val outputs = model.process(inputFeature0)
                processResults(outputs.outputFeature0AsTensorBuffer.floatArray)
            } finally {
                model.close()
            }
        } catch (e: Exception) {
            showError("Classification error: ${e.message}")
        }
    }

    private fun createInputFeature(image: Bitmap): TensorBuffer {
        val inputFeature0 = TensorBuffer.createFixedSize(
            intArrayOf(1, imageSize, imageSize, 3),
            org.tensorflow.lite.DataType.FLOAT32
        )

        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            .order(ByteOrder.nativeOrder())

        val pixels = IntArray(imageSize * imageSize)
        image.getPixels(pixels, 0, image.width, 0, 0, image.width, image.height)

        pixels.forEach { pixel ->
            byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f)
            byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)
            byteBuffer.putFloat((pixel and 0xFF) / 255.0f)
        }

        inputFeature0.loadBuffer(byteBuffer)
        return inputFeature0
    }

    private fun processResults(confidences: FloatArray) {
        confidences.indices.maxByOrNull { confidences[it] }?.let { maxIndex ->
            val confidencePercentage = (confidences[maxIndex] * 100).toInt()
            with(binding) {
                result.text = classes[maxIndex]
                confidenceText.text = "$confidencePercentage%"
                confidenceProgress.progress = confidencePercentage
            }
        } ?: showError("Classification failed")
    }

    private fun showError(message: String) {
        with(binding) {
            result.text = "Error"
            confidenceText.text = "0%"
            confidenceProgress.progress = 0
        }
        showLoading(false)
        showToast(message)
    }

    private fun showLoading(show: Boolean) {
        binding.loadingOverlay.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}