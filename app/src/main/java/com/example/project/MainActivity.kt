package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var editTextPassword: EditText
    private lateinit var buttonEnter: Button
    private lateinit var buttonReset: Button

    private lateinit var db: PassengerDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextPassword = findViewById(R.id.editTextNewMasterPass2)
        buttonEnter = findViewById(R.id.buttonEnter)
        buttonReset = findViewById(R.id.buttonReset)

        buttonEnter.setOnClickListener { handleLogin() }
        buttonReset.setOnClickListener { handleReset() }

        db = initDatabase(this)
    }

    private fun handleLogin() {
        val masterPassword = editTextPassword.text.toString()

        if (masterPassword.isNotEmpty()) {
            if (checkOrCreateMasterPassword(masterPassword, this)) {
                val intent = Intent(baseContext, ListActivity::class.java)
                intent.putExtra("MASTER_PASSWORD", masterPassword)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Wrong password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleReset() {
        dropEncryptedData(db, this)
        Toast.makeText(applicationContext, "Reset successful", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        editTextPassword.setText("")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.change_password -> {
                val intent = Intent(baseContext, ChangeActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
