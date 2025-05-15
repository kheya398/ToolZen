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
        var websitesearchid = findViewById<LinearLayout>(R.id.linearLayout)
        var unitconverter = findViewById<LinearLayout>(R.id.linearLayout2)
        var passwordgenerator = findViewById<LinearLayout>(R.id.linearLayout3)


        qrview.setOnClickListener()
        {
            var qrintent = Intent(this, QrgeneratorActivity::class.java)
            startActivity(qrintent)
        }

        websitesearchid.setOnClickListener()
        {
            var websiteintent = Intent(this, WebsitesearchActivity::class.java)
            startActivity(websiteintent)
        }

        unitconverter.setOnClickListener()
        {
            var unitconverterintent = Intent(this, UnitConverterActivity::class.java)
            startActivity(unitconverterintent)

        }

        passwordgenerator.setOnClickListener()
        {
            var passwordganarate = Intent(this, PasswordgeneratorActivity::class.java)
            startActivity(passwordganarate)
        }
    }

}