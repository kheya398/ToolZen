package com.example.toolzen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WebsitesearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.websitesearch)


        val fb: ImageView = findViewById(R.id.facebook)
        val figma: ImageView = findViewById(R.id.fig)
        val geeks: ImageView = findViewById(R.id.geek)
        val gemini: ImageView = findViewById(R.id.gimini)
        val flowchart: ImageView = findViewById(R.id.flow)
        val google: ImageView = findViewById(R.id.goole)


        val urls = arrayOf(
            "https://www.facebook.com",
            "https://www.figma.com",
            "https://www.smartdraw.com",
            "https://www.geeksforgeeks.org",
            "https://gemini.google.com",
            "https://www.google.com"
        )


        fb.setOnClickListener {
            openWebsite(urls[0])
        }

        figma.setOnClickListener {
            openWebsite(urls[1])
        }

        geeks.setOnClickListener {
            openWebsite(urls[2])
        }

        gemini.setOnClickListener {
            openWebsite(urls[3])
        }

        flowchart.setOnClickListener {
            openWebsite(urls[4])
        }

        google.setOnClickListener {
            openWebsite(urls[5])
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun openWebsite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}