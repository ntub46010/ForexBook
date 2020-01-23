package com.vincent.forexbook.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.vincent.forexbook.Constants
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.R
import com.vincent.forexbook.entity.BookVO
import com.vincent.forexbook.entity.EntryPO
import com.vincent.forexbook.entity.EntryType
import com.vincent.forexbook.entity.EntryVO
import com.vincent.forexbook.service.EntryService
import com.vincent.forexbook.util.FormatUtils
import kotlinx.android.synthetic.main.activity_entry_edit.*
import kotlinx.android.synthetic.main.content_toolbar.toolbar
import java.util.*

class EntryEditActivity : AppCompatActivity() {

    private lateinit var book: BookVO
    private lateinit var action: String

    private lateinit var dialogWaiting: Dialog

    private val transactionDateClickListener = View.OnClickListener {
        val calendar = Calendar.getInstance()

        val transactionDateText = editDate.text
        if (transactionDateText != null && transactionDateText.isNotEmpty()) {
            calendar.time = FormatUtils.formatDate(transactionDateText.toString())
        }

        DatePickerDialog(this, dateSelectedListener,
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
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

    private val entryCreatedListener = object : GeneralCallback<EntryVO> {
        override fun onFinish(data: EntryVO?) {
            val entry = data ?: return

            val intent = Intent()
            intent.putExtra(Constants.KEY_ENTRY, entry)
            setResult(Activity.RESULT_OK, intent)

            dialogWaiting.dismiss()
            Toast.makeText(this@EntryEditActivity, getString(R.string.message_create_successful), Toast.LENGTH_SHORT).show()
            finish()
        }

        override fun onException(e: Exception) {
            Toast.makeText(this@EntryEditActivity, e.message, Toast.LENGTH_SHORT).show()
            dialogWaiting.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_edit)

        action = intent.getStringExtra(Constants.KEY_ACTION)
        book = intent.getSerializableExtra(Constants.KEY_BOOK) as BookVO

        initToolbar()
        initWaitingDialog()
        editDate.setOnClickListener(transactionDateClickListener)
        editDate.setText(FormatUtils.formatDate(Date()))
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

        val fcyAmt: Double
        val twdAmt: Int
        if (entryType == EntryType.CREDIT) {
            fcyAmt = editFcyAmt.text.toString().toDouble()
            val twdAmtText = editTwdAmt.text
            twdAmt = if (twdAmtText == null || twdAmtText.isEmpty()) 0
                else twdAmtText.toString().toInt()
        } else {
            fcyAmt = -editFcyAmt.text.toString().toDouble()
            twdAmt = -editTwdAmt.text.toString().toInt()
        }

        val request = EntryPO(
            book.id,
            entryType,
            fcyAmt,
            twdAmt,
            book.currencyType,
            FormatUtils.formatDate(editDate.text.toString()),
            createdTime = Date()
        )

        dialogWaiting.show()
        EntryService.createEntry(request, entryCreatedListener)
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

    private fun initWaitingDialog() {
        dialogWaiting = Dialog(this)
        dialogWaiting.setContentView(R.layout.dialog_waiting)
        dialogWaiting.setCancelable(false)
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
