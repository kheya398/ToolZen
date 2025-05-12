package com.example.toolzen

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class UnitConverterActivity : AppCompatActivity() {

    private lateinit var fromUnitSpinner: Spinner
    private lateinit var toUnitSpinner: Spinner
    private lateinit var unitInput: EditText
    private lateinit var conversionResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.unit_converter)

        fromUnitSpinner = findViewById(R.id.fromUnitSpinner)
        toUnitSpinner = findViewById(R.id.toUnitSpinner)
        unitInput = findViewById(R.id.unitInput)
        conversionResult = findViewById(R.id.conversionResult)


        findViewById<Button>(R.id.convertButton).setOnClickListener { convertUnits() }


        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.unit_types,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        fromUnitSpinner.adapter = adapter
        toUnitSpinner.adapter = adapter
    }

    private fun convertUnits() {
        val fromUnit = fromUnitSpinner.selectedItem.toString()
        val toUnit = toUnitSpinner.selectedItem.toString()
        val inputText = unitInput.text.toString()

        if (inputText.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val inputValue = inputText.toDouble()
            val convertedValue = convert(fromUnit, toUnit, inputValue)
            conversionResult.text = String.format(Locale.getDefault(), "%.2f %s", convertedValue, toUnit)
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convert(fromUnit: String, toUnit: String, value: Double): Double {
        val conversionMap = mapOf(
            "Kilometer" to 1000.0,
            "Meter" to 1.0,
            "Centimeter" to 0.01,
            "Millimeter" to 0.001,
            "Mile" to 1609.34,
            "Yard" to 0.9144,
            "Foot" to 0.3048,
            "Inch" to 0.0254
        )

        val fromValue = conversionMap[fromUnit]
        val toValue = conversionMap[toUnit]

        if (fromValue == null || toValue == null) {
            Toast.makeText(this, "Invalid unit selection", Toast.LENGTH_SHORT).show()
            return 0.0
        }

        val valueInMeters = value * fromValue
        return valueInMeters / toValue
    }
}