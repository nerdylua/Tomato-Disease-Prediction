package com.example.sasya2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sasya2.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeFirebase()
        setupClickListeners()
    }

    private fun initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    private fun setupClickListeners() {
        binding.textView.setOnClickListener {
            navigateToSignIn()
        }

        binding.button.setOnClickListener {
            attemptSignUp()
        }
    }

    private fun navigateToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    private fun attemptSignUp() {
        val name = binding.nameEt.text.toString().trim()
        val email = binding.emailEt.text.toString().trim()
        val password = binding.passET.text.toString()
        val confirmPassword = binding.confirmPassEt.text.toString()

        when {
            name.isEmpty() -> showError("Please enter your name")
            email.isEmpty() -> showError("Please enter your email")
            !isValidEmail(email) -> showError("Please enter a valid email address")
            password.isEmpty() -> showError("Please enter a password")
            confirmPassword.isEmpty() -> showError("Please confirm your password")
            !isValidPassword(password) -> showError(
                "Password must be at least 8 characters long and contain uppercase, lowercase, number, and special character"
            )
            password != confirmPassword -> showError("Passwords do not match")
            else -> performFirebaseSignUp(name, email, password)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    private fun performFirebaseSignUp(name: String, email: String, password: String) {
        binding.button.isEnabled = false // Prevent multiple submissions

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.button.isEnabled = true

                if (task.isSuccessful) {
                    saveUserData(name, email)
                } else {
                    handleSignUpError(task.exception)
                }
            }
    }

    private fun saveUserData(name: String, email: String) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener {
                    showSuccess("Account created successfully!")
                    navigateToSignIn()
                }
                .addOnFailureListener { e ->
                    showError("Failed to save user data: ${e.message}")
                }
        }
    }

    private fun handleSignUpError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthWeakPasswordException -> "Password is too weak"
            is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
            is FirebaseAuthUserCollisionException -> "An account already exists with this email"
            else -> "Sign up failed: ${exception?.message}"
        }
        showError(errorMessage)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}