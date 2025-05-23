package com.example.toolzen

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UnitConverterActivity : AppCompatActivity() {

    private lateinit var unitInput: EditText
    private lateinit var fromUnitSpinner: Spinner
    private lateinit var toUnitSpinner: Spinner
    private lateinit var convertButton: Button
    private lateinit var conversionResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.unit_converter)

        unitInput = findViewById(R.id.unitInput)
        fromUnitSpinner = findViewById(R.id.fromUnitSpinner)
        toUnitSpinner = findViewById(R.id.toUnitSpinner)
        convertButton = findViewById(R.id.convertButton)
        conversionResult = findViewById(R.id.conversionResult)

        val fromAdapter = ArrayAdapter.createFromResource(this, R.array.unit_types, android.R.layout.simple_spinner_item)
        val toAdapter = ArrayAdapter.createFromResource(this, R.array.unit_types_to, android.R.layout.simple_spinner_item)
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromUnitSpinner.adapter = fromAdapter
        toUnitSpinner.adapter = toAdapter

        convertButton.setOnClickListener {
            val inputValue = unitInput.text.toString().toDoubleOrNull()
            val fromUnit = fromUnitSpinner.selectedItem.toString()
            val toUnit = toUnitSpinner.selectedItem.toString()

            if (inputValue == null || fromUnit == "Select from" || toUnit == "Select to") {
                conversionResult.text = "Please enter a valid value and select both units."
                return@setOnClickListener
            }

            val meterValue = convertToMeters(inputValue, fromUnit)
            val finalValue = convertFromMeters(meterValue, toUnit)
            conversionResult.text = "%.2f $fromUnit = %.2f $toUnit".format(inputValue, finalValue)
        }
    }

    private fun convertToMeters(value: Double, unit: String): Double {
        return when (unit) {
            "Kilometer" -> value * 1000
            "Meter" -> value
            "Centimeter" -> value / 100
            "Millimeter" -> value / 1000
            "Miles" -> value * 1609.34
            "Yards" -> value * 0.9144
            "Feet" -> value * 0.3048
            "Inch" -> value * 0.0254
            else -> 0.0
        }
    }

    private fun convertFromMeters(value: Double, unit: String): Double {
        return when (unit) {
            "Kilometer" -> value / 1000
            "Meter" -> value
            "Centimeter" -> value * 100
            "Millimeter" -> value * 1000
            "Miles" -> value / 1609.34
            "Yards" -> value / 0.9144
            "Feet" -> value / 0.3048
            "Inch" -> value / 0.0254
            else -> 0.0
        }
    }
}