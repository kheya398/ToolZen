package com.example.toolzen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
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
import net.glxn.qrgen.android.QRCode
import java.io.File
import java.io.FileOutputStream

class QrgeneratorActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btnSave: Button
    private lateinit var btnGenerateQR: Button
    private lateinit var inputText: EditText
    private lateinit var qrImageView: ImageView
    private var qrImage: Bitmap? = null
    private val EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qrgenerator)

        btnSave = findViewById(R.id.btn_save)
        btnGenerateQR = findViewById(R.id.btn_generateqr)
        inputText = findViewById(R.id.input_text)
        qrImageView = findViewById(R.id.grImage_qrCode)

        btnSave.setOnClickListener(this)
        btnGenerateQR.setOnClickListener(this)

        if (!checkPermissionForExternalStorage()) {
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
                if (!checkPermissionForExternalStorage()) {
                    Toast.makeText(this, "External storage permission needed", Toast.LENGTH_SHORT).show()
                } else {
                    qrImage?.let { saveImage(it) }
                }
            }
        }
    }

    private fun checkPermissionForExternalStorage(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(this, "Requesting storage permission", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getTimestamp(): String {
        val tsLong = System.currentTimeMillis() / 1000
        return tsLong.toString()
    }

    private fun generateQRCode() {
        val text = inputText.text.toString()
        qrImage = QRCode.from(text).bitmap()
        qrImage?.let {
            qrImageView.setImageBitmap(it)
            btnSave.visibility = View.VISIBLE
        }
    }

    private fun saveImage(image: Bitmap): String {
        val imageFileName = "QR_${getTimestamp()}.jpg"
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Info")

        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Toast.makeText(this, "Error creating folder", Toast.LENGTH_SHORT).show()
            return ""
        }

        val imageFile = File(storageDir, imageFileName)
        val savedImagePath = imageFile.absolutePath

        try {
            val fOut = FileOutputStream(imageFile)
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.flush()
            fOut.close()

            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(imageFile)
            mediaScanIntent.data = contentUri
            sendBroadcast(mediaScanIntent)

            Toast.makeText(this, "QR Image saved to Info folder", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show()
        }

        return savedImagePath
    }
}