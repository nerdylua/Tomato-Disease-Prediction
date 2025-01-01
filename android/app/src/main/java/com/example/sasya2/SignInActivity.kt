package com.example.sasya2

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sasya2.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    companion object {
        private const val MAX_SIGN_IN_ATTEMPTS = 5
        private const val LOCK_DURATION_MS = 300000L // 5 minutes
    }

    private var signInAttempts = 0
    private var lastAttemptTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeFirebase()
        setupClickListeners()
    }

    private fun initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    private fun setupClickListeners() {
        binding.apply {
            textView.setOnClickListener {
                navigateToSignUp()
            }

            button.setOnClickListener {
                if (canAttemptSignIn()) {
                    attemptSignIn()
                } else {
                    showError("Too many attempts. Please try again later.")
                }
            }

            forgotPassword.setOnClickListener {
                handleForgotPassword()
            }
        }
    }

    private fun canAttemptSignIn(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAttemptTime > LOCK_DURATION_MS) {
            signInAttempts = 0
        }
        return signInAttempts < MAX_SIGN_IN_ATTEMPTS
    }

    private fun navigateToSignUp() {
        Intent(this, SignUpActivity::class.java).also { intent ->
            startActivity(intent)
        }
    }

    private fun attemptSignIn() {
        val email = binding.emailEt.text.toString().trim()
        val password = binding.passET.text.toString()

        when {
            email.isEmpty() -> showError("Email is required")
            !isValidEmail(email) -> showError("Please enter a valid email address")
            password.isEmpty() -> showError("Password is required")
            else -> performFirebaseSignIn(email, password)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun performFirebaseSignIn(email: String, password: String) {
        binding.button.isEnabled = false
        showLoading(true)

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.button.isEnabled = true
                showLoading(false)
                signInAttempts++
                lastAttemptTime = System.currentTimeMillis()

                if (task.isSuccessful) {
                    handleSuccessfulSignIn()
                } else {
                    handleSignInError(task.exception)
                }
            }
    }

    private fun handleSuccessfulSignIn() {
        logSignInActivity()
        setUserEmailInNavHeader()
        navigateToMain()
    }

    private fun logSignInActivity() {
        firebaseAuth.currentUser?.uid?.let { userId ->
            val signInLog = hashMapOf(
                "timestamp" to System.currentTimeMillis(),
                "device" to android.os.Build.MODEL,
                "platform" to "Android"
            )
            firestore.collection("users").document(userId)
                .collection("signin_logs")
                .add(signInLog)
        }
    }

    private fun handleSignInError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidUserException -> "No account found with this email"
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
            else -> "Sign in failed: ${exception?.localizedMessage ?: "Unknown error"}"
        }
        showError(errorMessage)
    }

    private fun handleForgotPassword() {
        val email = binding.emailEt.text.toString().trim()
        when {
            email.isEmpty() -> showError("Please enter your email first")
            !isValidEmail(email) -> showError("Please enter a valid email address")
            else -> {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        showSuccess("Password reset email sent")
                    }
                    .addOnFailureListener { e ->
                        showError("Failed to send reset email: ${e.message}")
                    }
            }
        }
    }

    private fun navigateToMain() {
        Intent(this, MainActivity::class.java).also { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        binding.button.isEnabled = !show
    }

    fun setUserEmailInNavHeader() {
        firebaseAuth.currentUser?.let { user ->
            user.email?.let { email ->
                findViewById<TextView>(R.id.usermail)?.apply {
                    text = email
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.currentUser?.let {
            handleSuccessfulSignIn()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.button.isEnabled = true
        showLoading(false)
    }
}