package com.vincent.forexbook.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.vincent.forexbook.Constants
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.R
import com.vincent.forexbook.adapter.EntryListAdapter
import com.vincent.forexbook.entity.EntryVO
import com.vincent.forexbook.service.EntryService
import kotlinx.android.synthetic.main.activity_book_home.*

class BookHomeActivity : AppCompatActivity() {

    private lateinit var bookId: String

    private val entriesLoadedCallback = object : GeneralCallback<List<EntryVO>> {
        override fun onFinish(data: List<EntryVO>?) {
            runOnUiThread {
                displayEntries(data ?: emptyList())
            }
        }

        override fun onException(e: Exception) {
            runOnUiThread {
                prgBar.visibility = View.INVISIBLE
                Toast.makeText(this@BookHomeActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val entryItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val entry = listEntry.adapter.getItem(position) as EntryVO
        Toast.makeText(this, entry.id, Toast.LENGTH_SHORT).show()
    }

    private val createEntryButtonClickListener = View.OnClickListener {
        Toast.makeText(this, "建立帳目 $bookId", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_home)

        val bundle = intent.extras!!
        bookId = bundle.getString(Constants.FIELD_ID)!!
        val bookName = bundle.getString(Constants.FIELD_NAME) ?: ""

        initToolbar(bookName)
        listEntry.onItemClickListener = entryItemClickListener
        btnCreateEntry.setOnClickListener(createEntryButtonClickListener)

        EntryService.loadEntries(bookId, entriesLoadedCallback)
    }

    private fun initToolbar(title: String) {
        toolbar.title = title
        setSupportActionBar(toolbar)

        // listener should be set after setting toolbar to activity
        toolbar.setNavigationOnClickListener { finish() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun displayEntries(entries: List<EntryVO>) {
        prgBar.visibility = View.INVISIBLE
        val adapter = listEntry.adapter

        if (adapter == null) {
            listEntry.adapter = EntryListAdapter(this, entries.toMutableList())
        }
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
