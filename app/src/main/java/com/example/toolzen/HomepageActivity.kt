package com.example.toolzen

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
//import com.example.unitconverter.UnitConverterActivity

class HomepageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.homepage)

        var qrview = findViewById<LinearLayout>(R.id.qrlayout)
        var websitesearchid = findViewById<LinearLayout>(R.id.linearLayout)
        var unitconverter = findViewById<LinearLayout>(R.id.linearLayout2)
        var passwordgenerator = findViewById<LinearLayout>(R.id.linearLayout3)
        var notepad = findViewById<LinearLayout>(R.id.linearLayout4)
        var  imagecompressor= findViewById<LinearLayout>(R.id.linearLayout5)


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

        notepad.setOnClickListener()
        {
            var Notepad = Intent(this, NotepadActivity::class.java)
            startActivity(Notepad)
        }

        imagecompressor.setOnClickListener()
        {
            var imagecompresor = Intent(this, ImageCompressorActivity::class.java)
            startActivity(imagecompresor)
        }
    }

}