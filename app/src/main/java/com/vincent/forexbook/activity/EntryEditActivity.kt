package com.vincent.forexbook.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.vincent.forexbook.Constants
import com.vincent.forexbook.R
import kotlinx.android.synthetic.main.activity_entry_edit.*

class EntryEditActivity : AppCompatActivity() {

    private lateinit var bookId: String
    private lateinit var action: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_edit)

        action = intent.getStringExtra(Constants.KEY_ACTION)
        bookId = intent.getStringExtra(Constants.KEY_ID)
        Toast.makeText(this, bookId, Toast.LENGTH_SHORT).show()
        initToolbar()
    }

    private fun initToolbar() {
        toolbar.title =
            if (action == Constants.ACTION_CREATE) getString(R.string.title_create_entry)
            else getString(R.string.title_edit_entry)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.entry_edit_page_action_buttons, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_submit ->
                Toast.makeText(this, getString(R.string.submit), Toast.LENGTH_SHORT).show()
        }

        return true
    }
}
