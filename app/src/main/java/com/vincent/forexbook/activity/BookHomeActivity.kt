package com.vincent.forexbook.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.vincent.forexbook.Constants
import com.vincent.forexbook.R
import kotlinx.android.synthetic.main.activity_book_home.*

class BookHomeActivity : AppCompatActivity() {

    private lateinit var bookId: String
    private lateinit var bookName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_home)

        val bundle = intent.extras!!
        bookId = bundle.getString(Constants.FIELD_ID)!!
        bookName = bundle.getString(Constants.FIELD_NAME)!!

        initToolbar()

        btnCreateEntry.setOnClickListener {
            Toast.makeText(this, "建立帳目", Toast.LENGTH_SHORT).show()
        }

        Toast.makeText(this, bookId, Toast.LENGTH_SHORT).show()
    }

    private fun initToolbar() {
        toolbar.title = bookName
        setSupportActionBar(toolbar)

        // listener should be set after setting to activity
        toolbar.setNavigationOnClickListener { finish() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_home_page_menu_items, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_edit ->
                Toast.makeText(this, getString(R.string.edit), Toast.LENGTH_SHORT).show()
            R.id.action_delete ->
                Toast.makeText(this, getString(R.string.delete), Toast.LENGTH_SHORT).show()
        }

        return true
    }

}
