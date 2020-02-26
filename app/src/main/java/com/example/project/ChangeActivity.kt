package com.example.project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChangeActivity : AppCompatActivity() {

    private lateinit var editTextOldMasterPass: EditText
    private lateinit var editTextNewMasterPass1: EditText
    private lateinit var editTextNewMasterPass2: EditText
    private lateinit var buttonChangeMasterPass: Button

    private lateinit var db: PassengerDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change)

        editTextOldMasterPass = findViewById(R.id.editTextOldMasterPass)
        editTextNewMasterPass1 = findViewById(R.id.editTextNewMasterPass1)
        editTextNewMasterPass2 = findViewById(R.id.editTextNewMasterPass2)
        buttonChangeMasterPass = findViewById(R.id.buttonChangeMasterPass)

        buttonChangeMasterPass.setOnClickListener { handleChange() }

        db = initDatabase(this)
    }

    private fun handleChange() {
        val old = editTextOldMasterPass.text.toString()
        val new1 = editTextNewMasterPass1.text.toString()
        val new2 = editTextNewMasterPass2.text.toString()

        if (old.isNotEmpty() && new1.isNotEmpty() && new2.isNotEmpty()) {

            if (checkMasterPassword(old, this)) {
                if (new1 == new2) {
                    changeMasterPassword(old, new1)
                    finish()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Repeated password is incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(applicationContext, "Old password is incorrect", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun changeMasterPassword(old: String, new1: String) {
        val allCredentials = db.credentials().findAll()

        for (credentials in allCredentials) {
            val newCred = credentials.decrypt(old).encrypt(new1)
            db.credentials().update(newCred)
        }

        setMasterPassword(new1, this)
    }
}
