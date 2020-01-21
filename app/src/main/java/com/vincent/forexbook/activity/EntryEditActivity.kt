package com.vincent.forexbook.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.vincent.forexbook.Constants
import com.vincent.forexbook.R
import com.vincent.forexbook.entity.BookVO
import com.vincent.forexbook.entity.EntryPO
import com.vincent.forexbook.entity.EntryType
import com.vincent.forexbook.util.FormatUtils
import kotlinx.android.synthetic.main.activity_entry_edit.*
import java.util.*

class EntryEditActivity : AppCompatActivity() {

    private lateinit var book: BookVO
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
        book = intent.getSerializableExtra(Constants.KEY_BOOK) as BookVO
        Toast.makeText(this, book.id, Toast.LENGTH_SHORT).show()

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

    private fun createEntry() {
        if (!validate()) {
            return
        }

        val entryType =
            if (radioFcyDebit.isChecked) EntryType.DEBIT
            else EntryType.CREDIT

        val twdAmt =
            if (radioInterestCredit.isChecked) 0
            else editTwdAmt.text.toString().toInt()

        val request = EntryPO(
            book.id,
            entryType,
            editFcyAmt.text.toString().toDouble(),
            twdAmt,
            book.currencyType,
            FormatUtils.formatDate(editDate.text.toString()),
            createdTime = Date()
        )

        // TODO: call entry service to save in DB
        Toast.makeText(this, request.toString(), Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.putExtra(Constants.KEY_ID, "0")
        intent.putExtra(Constants.KEY_ENTRY, request)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun validate(): Boolean {
        val dateText = editDate.text
        val fcyAmtText = editFcyAmt.text
        val twdAmtText = editTwdAmt.text
        var result = true

        if (radioGroupEntryType.checkedRadioButtonId == RadioGroup.NO_ID) {
            result = false
            Toast.makeText(this, getString(R.string.message_no_entry_type), Toast.LENGTH_SHORT).show()
        }

        if (dateText == null || dateText.isEmpty()) {
            result = false
            tilDate.error = getString(R.string.mandatory_field)
        } else {
            tilDate.error = null
        }

        if (fcyAmtText == null || fcyAmtText.isEmpty()) {
            result = false
            tilFcyAmt.error = getString(R.string.mandatory_field)
        } else if (radioFcyDebit.isChecked &&
            fcyAmtText.toString().toDouble() > book.foreignBalance) {
            result = false
            tilFcyAmt.error = getString(R.string.message_short_balance)
        } else {
            tilFcyAmt.error = null
        }

        if (!radioInterestCredit.isChecked &&
            (twdAmtText == null || twdAmtText.isEmpty())) {
            result = false
            tilTwdAmt.error = getString(R.string.mandatory_field)
        } else {
            tilTwdAmt.error = null
        }

        return result
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.entry_edit_page_action_buttons, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_submit -> {
                if (action == Constants.ACTION_CREATE) {
                    createEntry()
                }
            }
        }

        return true
    }
}
