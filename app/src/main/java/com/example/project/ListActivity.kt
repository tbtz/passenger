package com.example.project

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ListActivity : AppCompatActivity() {

    private lateinit var listView: LinearLayout

    private lateinit var masterPassword: String
    private lateinit var db: PassengerDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        masterPassword = intent.getStringExtra("MASTER_PASSWORD")!!

        db = initDatabase(this)
        syncEntries(masterPassword)
    }

    override fun onResume() {
        super.onResume()
        syncEntries(masterPassword)
    }

    private fun syncEntries(masterPassword: String) {
        listView = findViewById(R.id.credentialsList)
        listView.removeAllViewsInLayout()

        val allCredentials = db.credentials().findAll()
        for (cred: Credentials in allCredentials) {
            val cred = cred.decrypt(masterPassword)
            val entry = generateEntry(cred)
            listView.addView(entry)
        }

        if (allCredentials.isEmpty()) {
            val titleView = TextView(this)

            titleView.textSize = 18f
            titleView.setPadding(0, 0, 0, 40)
            titleView.text = "— no saved passwords —"
            titleView.setTextColor(Color.argb(200, 255, 255, 255))

            listView.addView(titleView)
        }
    }

    private fun generateEntry(cred: Credentials): TextView {
        val titleView = TextView(this)

        titleView.textSize = 28f
        titleView.setPadding(0, 0, 0, 40)
        titleView.text = "› ${cred.title}"
        titleView.setTextColor(Color.WHITE)
        titleView.typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)

        titleView.setOnClickListener { handleEntryClick(cred.uid) }

        return titleView
    }

    private fun handleEntryClick(uid: Long?) {
        val intent = Intent(baseContext, DetailsActivity::class.java)
        intent.putExtra("MASTER_PASSWORD", masterPassword)
        intent.putExtra("ID", uid)
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                finish()
                true
            }
            R.id.add_credentials -> {
                val intent = Intent(baseContext, AddActivity::class.java)
                intent.putExtra("MASTER_PASSWORD", masterPassword)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
