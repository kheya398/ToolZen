package com.example.toolzen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomepageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.homepage)

        var qrview = findViewById<LinearLayout>(R.id.qrlayout)

        qrview.setOnClickListener()
        {
            var qrintent = Intent(this,QrscannerActivity::class.java)
            startActivity(qrintent)
        }

    }
}