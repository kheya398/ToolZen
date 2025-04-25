package com.example.toolzen

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class HomepageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.homepage)

        var qrview = findViewById<LinearLayout>(R.id.qrlayout)

        qrview.setOnClickListener()
        {
            var qrintent = Intent(this,QrgeneratorActivity::class.java)
            startActivity(qrintent)
        }

    }
}