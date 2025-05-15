package com.example.toolzen

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.security.SecureRandom

class PasswordgeneratorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.passwordgenerator)

        // Initialize UI elements using findViewById
        val passwordLength = findViewById<EditText>(R.id.passwordLength)
        val includeNumber = findViewById<CheckBox>(R.id.IncludeNumber)
        val includeSymbol = findViewById<CheckBox>(R.id.IncludeSymbol)
        val includeUppercase = findViewById<CheckBox>(R.id.IncludeUppercaseCharacter)
        val generatePasswordButton = findViewById<Button>(R.id.generatePasswordButton)
        val tvGeneratePasswordText = findViewById<TextView>(R.id.tvGeneratePasswordText)

        // Set click listener for the generate password button
        generatePasswordButton.setOnClickListener {
            val length = passwordLength.text.toString().toIntOrNull()
            if (length != null && length > 0) {
                val password = generatePassword(
                    length,
                    includeNumber.isChecked,
                    includeSymbol.isChecked,
                    includeUppercase.isChecked
                )
                tvGeneratePasswordText.text = password
            } else {
                Toast.makeText(this, "Please enter a valid password length", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generatePassword(
        length: Int,
        includeNumbers: Boolean,
        includeSymbols: Boolean,
        includeUppercase: Boolean
    ): String {
        val lowercase = "abcdefghijklmnopqrstuvwxyz"
        val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numbers = "0123456789"
        val symbols = "!@#$%^&*()_+-=[]{}|;:,.<>?"

        var allowedChars = lowercase
        if (includeUppercase) allowedChars += uppercase
        if (includeNumbers) allowedChars += numbers
        if (includeSymbols) allowedChars += symbols

        val random = SecureRandom()
        val password = StringBuilder()

        for (i in 0 until length) {
            val randomIndex = random.nextInt(allowedChars.length)
            password.append(allowedChars[randomIndex])
        }

        return password.toString()
    }
}