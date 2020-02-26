package com.example.project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddActivity : AppCompatActivity() {


    private lateinit var titleField: EditText
    private lateinit var passwordField: EditText
    private lateinit var addButton: Button

    private lateinit var db: PassengerDatabase
    private lateinit var masterPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        titleField = findViewById(R.id.editTextNewMasterPass1)
        passwordField = findViewById(R.id.editTextNewMasterPass2)
        addButton = findViewById(R.id.buttonChangeMasterPass)

        addButton.setOnClickListener { handleAdd() }

        db = initDatabase(this)
        masterPassword = intent.getStringExtra("MASTER_PASSWORD")!!
    }

    private fun handleAdd() {
        val title = titleField.text.toString()
        val password = passwordField.text.toString()

        if (title.isNotEmpty() && password.isNotEmpty()) {
            db.credentials().insertAll(
                Credentials(title = title, password = password).encrypt(masterPassword)
            )
            Toast.makeText(applicationContext, "Added $title successfully", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }
}
