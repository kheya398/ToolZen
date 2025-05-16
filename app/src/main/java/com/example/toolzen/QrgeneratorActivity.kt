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

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
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
        qrImageView = findViewById(R.id.imageview_qrcode)

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

                checkPermissionAndSave()
            }
        }
    }

    private fun checkPermissionAndSave() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                qrImage?.let { saveImage(it) }
            } else {

                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)
            }
        } else {

            qrImage?.let { saveImage(it) }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            qrImage?.let { saveImage(it) }
        } else {

            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
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