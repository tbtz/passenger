package com.example.project

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class DetailsActivity : AppCompatActivity() {

    private lateinit var copyButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button

    private lateinit var titleView: TextView
    private lateinit var passwordField: EditText

    private lateinit var db: PassengerDatabase
    private lateinit var masterPassword: String
    private lateinit var credentials: Credentials

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)


        titleView = findViewById(R.id.textViewDetailsTitle)
        passwordField = findViewById(R.id.editTextDetailsPassword)

        copyButton = findViewById(R.id.buttonCopy)
        updateButton = findViewById(R.id.buttonUpdate)
        deleteButton = findViewById(R.id.buttonDelete)

        copyButton.setOnClickListener { handleCopy() }
        deleteButton.setOnClickListener { handleDelete() }
        updateButton.setOnClickListener { handleUpdate() }

        masterPassword = intent.getStringExtra("MASTER_PASSWORD")!!
        val id = intent.getLongExtra("ID", -1)

        db = initDatabase(this)
        credentials = db.credentials().findById(id).decrypt(masterPassword)

        titleView.text = credentials.title
        passwordField.setText(credentials.password)
    }

    private fun handleDelete() {
        db.credentials().delete(credentials)
        finish()
        Toast.makeText(
            applicationContext,
            "Deleted ${credentials.title} successfully",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun handleUpdate() {
        if (passwordField.text.isNotEmpty()) {
            credentials.password = passwordField.text.toString()
            db.credentials().update(credentials.encrypt(masterPassword))
            finish()
        }

        Toast.makeText(
            applicationContext,
            "Updated ${credentials.title} successfully",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun handleCopy() {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Password", credentials.password))

        Toast.makeText(
            applicationContext,
            "Copied ${credentials.title} successfully",
            Toast.LENGTH_SHORT
        ).show()
    }
}
