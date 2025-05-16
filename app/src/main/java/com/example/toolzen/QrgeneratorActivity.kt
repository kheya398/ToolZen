package com.example.toolzen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QrgeneratorActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btnSave: Button
    private lateinit var btnGenerateQR: Button
    private lateinit var inputText: EditText
    private lateinit var qrImageView: ImageView
    private var qrImage: Bitmap? = null
    private val STORAGE_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qrgenerator)

        btnSave = findViewById(R.id.btn_save)
        btnGenerateQR = findViewById(R.id.btn_generateqr)
        inputText = findViewById(R.id.input_text)
        qrImageView = findViewById(R.id.imageview_qrcode)

        btnSave.setOnClickListener(this)
        btnGenerateQR.setOnClickListener(this)
        btnSave.visibility = View.GONE // Hide save button until QR is generated

        // Check permissions only for Android versions requiring it
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !checkPermissionForExternalStorage()) {
            requestPermissionForExternalStorage()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_generateqr -> {
                if (inputText.text.toString().isNotEmpty()) {
                    generateQRCode()
                } else {
                    inputText.error = "This field is required"
                }
            }
            R.id.btn_save -> {
                checkPermissionAndSave()
            }
        }
    }

    private fun checkPermissionAndSave() {
        // For Android 10+, no permission needed for app-specific storage
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        } else {
            qrImage?.let { saveImage(it) } ?: Toast.makeText(this, "No QR code to save", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            qrImage?.let { saveImage(it) }
        } else {
            Toast.makeText(this, "Permission denied! Cannot save image.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionForExternalStorage(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) {
            Toast.makeText(this, "Storage permission needed to save QR code", Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    private fun generateQRCode() {
        val text = inputText.text.toString()
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            qrImage = bmp
            qrImageView.setImageBitmap(bmp)
            btnSave.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImage(image: Bitmap) {
        try {
            // Use app-specific storage
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
                File(it, "Info").apply { if (!exists()) mkdirs() }
            } ?: run {
                Toast.makeText(this, "Error accessing storage", Toast.LENGTH_SHORT).show()
                return
            }

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFile = File(storageDir, "QR_$timeStamp.jpg")

            FileOutputStream(imageFile).use { fOut ->
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.flush()
            }

            // Get content URI for sharing or gallery scanning
            val contentUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                imageFile
            )

            // Notify gallery
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
                data = contentUri
            }
            sendBroadcast(mediaScanIntent)

            Toast.makeText(this, "QR Image saved to Info folder", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show()
        }
    }
}