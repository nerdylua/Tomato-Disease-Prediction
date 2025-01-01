package com.example.sasya2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome) // Render the 'welcome' layout (e.g., a splash screen)

        // Firebase Authentication instance
        val firebaseAuth = FirebaseAuth.getInstance()

        // Navigate based on whether the user is signed in
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if the user is already signed in
            if (firebaseAuth.currentUser != null) {
                // If the user is signed in, navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // If the user is not signed in, navigate to SignUpActivity
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            finish() // Close SplashActivity so it doesn't appear again
        }, 3000) // 3-second delay
    }

}