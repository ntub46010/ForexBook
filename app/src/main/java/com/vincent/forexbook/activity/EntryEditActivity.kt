package com.vincent.forexbook.activity

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.vincent.forexbook.Constants
import com.vincent.forexbook.R
import com.vincent.forexbook.util.FormatUtils
import kotlinx.android.synthetic.main.activity_entry_edit.*
import java.util.*

class EntryEditActivity : AppCompatActivity() {

    private lateinit var bookId: String
    private lateinit var action: String

    private val editDateClickListener = View.OnClickListener {
        val now = Calendar.getInstance()
        DatePickerDialog(this, dateSelectedListener,
            now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            .show()
    }

    private val dateSelectedListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        editDate.setText(FormatUtils.formatDate(year, month, dayOfMonth))
    }

    private val entryTypeCheckListener = RadioGroup.OnCheckedChangeListener { radioGroup, checkedId ->
        when (checkedId) {
            R.id.radioInterestCredit -> {
                tilTwdAmt.visibility = View.GONE
                editTwdAmt.text = null
            }
            else -> tilTwdAmt.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_edit)

        action = intent.getStringExtra(Constants.KEY_ACTION)
        bookId = intent.getStringExtra(Constants.KEY_ID)
        Toast.makeText(this, bookId, Toast.LENGTH_SHORT).show()

        initToolbar()
        editDate.setOnClickListener(editDateClickListener)
        radioGroupEntryType.setOnCheckedChangeListener(entryTypeCheckListener)
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
