
package com.example.toolzen

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.toolzen.R
import java.io.File

class NotepadActivity : AppCompatActivity() {

    private lateinit var etNote: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button

    private val fileName = "my_note.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notepad)

        etNote = findViewById(R.id.etNote)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)

        loadNote()

        btnSave.setOnClickListener {
            val noteText = etNote.text.toString()
            if (noteText.isNotBlank()) {
                saveNote(noteText)
            } else {
                Toast.makeText(this, "Note is empty!", Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            deleteNote()
        }
    }

    private fun saveNote(text: String) {
        try {
            openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(text.toByteArray())
            }
            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save note.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadNote() {
        try {
            val file = File(filesDir, fileName)
            if (file.exists()) {
                val content = file.readText()
                etNote.setText(content)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteNote() {
        try {
            val file = File(filesDir, fileName)
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    etNote.setText("")
                    Toast.makeText(this, "Note deleted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to delete note.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No note found to delete.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error deleting note.", Toast.LENGTH_SHORT).show()
        }
    }
}