package com.example.sasya2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RemediePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remedie_page)

            // Initialize the CardViews
        val tomatoCard: CardView = findViewById(R.id.tomatoCard)
        val potatoCard: CardView = findViewById(R.id.potatoCard)
        val cherryCard: CardView = findViewById(R.id.cherryCard)
        val pepperCard: CardView = findViewById(R.id.pepperCard)
        val grapeCard: CardView = findViewById(R.id.grapeCard)
        val appleCard: CardView = findViewById(R.id.appleCard)

            // Set click listeners for each card
        tomatoCard.setOnClickListener { navigateToCropDetail(TomatoActivity::class.java) }
        potatoCard.setOnClickListener { navigateToCropDetail(PotatoActivity::class.java) }
        cherryCard.setOnClickListener { navigateToCropDetail(CherryActivity::class.java) }
        pepperCard.setOnClickListener { navigateToCropDetail(PepperActivity::class.java) }
        grapeCard.setOnClickListener { navigateToCropDetail(GrapeActivity::class.java) }
        appleCard.setOnClickListener { navigateToCropDetail(AppleActivity::class.java) }
    }

    private fun navigateToCropDetail(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
