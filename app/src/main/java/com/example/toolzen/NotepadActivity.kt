package com.example.toolzen

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotepadActivity : AppCompatActivity() {

    private lateinit var etNoteTitle: EditText
    private lateinit var etNote: EditText
    private lateinit var btnSave: Button
    private lateinit var btnNew: Button
    private lateinit var btnDelete: Button
    private lateinit var lvNotes: ListView
    private lateinit var noteAdapter: ArrayAdapter<String>
    private var noteFiles: MutableList<String> = mutableListOf()
    private var currentNoteFile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notepad)

        etNoteTitle = findViewById(R.id.etNoteTitle)
        etNote = findViewById(R.id.etNote)
        btnSave = findViewById(R.id.btnSave)
        btnNew = findViewById(R.id.btnNew)
        btnDelete = findViewById(R.id.btnDelete)
        lvNotes = findViewById(R.id.lvNotes)


        noteAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, noteFiles.map { it.removeSuffix(".txt") })
        lvNotes.adapter = noteAdapter


        loadNoteFiles()


        lvNotes.setOnItemClickListener { _, _, position, _ ->
            currentNoteFile = noteFiles[position]
            loadNote(currentNoteFile!!)
        }


        btnSave.setOnClickListener {
            val noteText = etNote.text.toString().trim()
            val noteTitle = etNoteTitle.text.toString().trim()
            if (noteText.isNotBlank() && noteTitle.isNotBlank()) {
                saveNote(noteTitle, noteText)
            } else {
                Toast.makeText(this, "Title or note content is empty!", Toast.LENGTH_SHORT).show()
            }
        }


        btnNew.setOnClickListener {
            etNoteTitle.setText("")
            etNote.setText("")
            currentNoteFile = null
            Toast.makeText(this, "Ready to create a new note", Toast.LENGTH_SHORT).show()
        }


        btnDelete.setOnClickListener {
            if (currentNoteFile != null) {
                deleteNote(currentNoteFile!!)
            } else {
                Toast.makeText(this, "No note selected to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveNote(title: String, text: String) {
        try {

            val sanitizedTitle = title.replace(Regex("[^a-zA-Z0-9_ ]"), "").trim().replace(" ", "_")
            if (sanitizedTitle.isEmpty()) {
                Toast.makeText(this, "Invalid title! Use letters, numbers, or spaces.", Toast.LENGTH_SHORT).show()
                return
            }


            var fileName = "$sanitizedTitle.txt"
            var counter = 1
            while (File(filesDir, fileName).exists() && fileName != currentNoteFile) {
                fileName = "${sanitizedTitle}_$counter.txt"
                counter++
            }


            openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(text.toByteArray())
            }
            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()


            if (!noteFiles.contains(fileName)) {
                noteFiles.add(fileName)
                noteAdapter.clear()
                noteAdapter.addAll(noteFiles.map { it.removeSuffix(".txt") })
                noteAdapter.notifyDataSetChanged()
            }
            currentNoteFile = fileName
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save note.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadNote(fileName: String) {
        try {
            val file = File(filesDir, fileName)
            if (file.exists()) {
                val content = file.readText()
                etNote.setText(content)
                etNoteTitle.setText(fileName.removeSuffix(".txt").replace("_", " "))
            } else {
                Toast.makeText(this, "Note not found!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to load note.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteNote(fileName: String) {
        try {
            val file = File(filesDir, fileName)
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    noteFiles.remove(fileName)
                    noteAdapter.clear()
                    noteAdapter.addAll(noteFiles.map { it.removeSuffix(".txt") })
                    noteAdapter.notifyDataSetChanged()
                    etNoteTitle.setText("")
                    etNote.setText("")
                    currentNoteFile = null
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

    private fun loadNoteFiles() {
        try {
            val files = filesDir.listFiles()?.filter { it.name.endsWith(".txt") }?.map { it.name }
            if (files != null) {
                noteFiles.clear()
                noteFiles.addAll(files)
                noteAdapter.clear()
                noteAdapter.addAll(noteFiles.map { it.removeSuffix(".txt") })
                noteAdapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading notes.", Toast.LENGTH_SHORT).show()
        }
    }
}