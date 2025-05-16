package com.example.toolzen

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageCompressorActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnSelect: Button
    private lateinit var btnCompress: Button
    private lateinit var btnSave: Button
    private lateinit var tvResult: TextView

    private var selectedBitmap: Bitmap? = null
    private var compressedData: ByteArray? = null
    private var savedImageUri: Uri? = null // To store the saved image URI

    companion object {
        private const val PICK_IMAGE_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imagecompressor)

        imageView = findViewById(R.id.imageView)
        btnSelect = findViewById(R.id.btnSelect)
        btnCompress = findViewById(R.id.btnCompress)
        btnSave = findViewById(R.id.btnSave)
        tvResult = findViewById(R.id.tvResult)


        btnCompress.isEnabled = false
        btnSave.isEnabled = false

        btnSelect.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnCompress.setOnClickListener {
            selectedBitmap?.let { bitmap ->
                compressedData = compressImage(bitmap, 50)
                tvResult.text = "Compressed size: ${compressedData!!.size / 1024} KB"
                val compressedBitmap = BitmapFactory.decodeByteArray(compressedData, 0, compressedData!!.size)
                imageView.setImageBitmap(compressedBitmap)
                btnSave.isEnabled = true
            }
        }

        btnSave.setOnClickListener {
            compressedData?.let { data ->
                savedImageUri = saveImageToMediaStore(data)
                if (savedImageUri != null) {
                    tvResult.text = "${tvResult.text}\nImage saved successfully"
                    shareImage(savedImageUri!!) // Share using the stored URI
                } else {
                    tvResult.text = "${tvResult.text}\nFailed to save image"
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val bitmap = getBitmapFromUri(uri)
                if (bitmap != null) {
                    selectedBitmap = bitmap
                    imageView.setImageBitmap(bitmap)
                    btnCompress.isEnabled = true
                    btnSave.isEnabled = false
                    tvResult.text = "Original size: ${bitmap.byteCount / 1024} KB"
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream).also { inputStream?.close() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun compressImage(bitmap: Bitmap, quality: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }

    private fun saveImageToMediaStore(data: ByteArray): Uri? {
        val filename = "Compressed_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val contentResolver = contentResolver
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            try {
                val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
                outputStream?.use {
                    it.write(data)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }
                return uri // Return the Uri of the saved image.
            } catch (e: Exception) {
                e.printStackTrace()
                contentResolver.delete(uri, null, null)
                return null // Return null if saving fails.
            }
        }
        return null
    }

    private fun shareImage(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Compressed Image"))
    }
}
