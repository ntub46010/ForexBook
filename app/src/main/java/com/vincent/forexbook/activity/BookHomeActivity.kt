package com.vincent.forexbook.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.vincent.forexbook.BookEditDialog
import com.vincent.forexbook.Constants
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.R
import com.vincent.forexbook.adapter.EntryListAdapter
import com.vincent.forexbook.entity.*
import com.vincent.forexbook.service.BookService
import com.vincent.forexbook.service.EntryService
import com.vincent.forexbook.service.ExchangeRateService
import com.vincent.forexbook.util.FormatUtils
import kotlinx.android.synthetic.main.activity_book_home.*
import kotlinx.android.synthetic.main.content_book_home_dashboard.*
import kotlinx.android.synthetic.main.content_toolbar.toolbar
import java.math.BigDecimal

class BookHomeActivity : AppCompatActivity() {

    private lateinit var book: BookVO
    private val DEFAULT_INDEX = -1
    private var selectedEntryIndex = DEFAULT_INDEX

    private lateinit var tilBookName: TextInputLayout

    private lateinit var editBookDialog: BookEditDialog
    private lateinit var entryActionDialog: Dialog
    private lateinit var deleteEntryConfirmDialog: Dialog
    private lateinit var deleteBookConfirmDialog: Dialog
    private lateinit var dialogWaiting: Dialog

    private val entriesLoadedCallback = object : GeneralCallback<List<EntryVO>> {
        override fun onFinish(data: List<EntryVO>?) {
            runOnUiThread {
                val entries = data ?: emptyList()

                book.foreignBalance = entries.asSequence()
                    .map { it.fcyAmt }
                    .sum()
                book.taiwanBalance = entries.asSequence()
                    .map { it.twdAmt }
                    .sum()

                displayEntries(entries)
                ExchangeRateService
                    .loadExchangeRate(book.bank, book.currencyType, exchangeRateLoadedCallback)
            }
        }

        override fun onException(e: Exception) {
            runOnUiThread {
                prgBar.visibility = View.INVISIBLE
                Toast.makeText(this@BookHomeActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val exchangeRateLoadedCallback = object : GeneralCallback<ExchangeRate> {
        override fun onFinish(data: ExchangeRate?) {
            val exchangeRate = data ?: return
            runOnUiThread {
                displayDashboard(exchangeRate)
                prgBar.visibility = View.INVISIBLE
                listEntry.visibility = View.VISIBLE
            }
        }

        override fun onException(e: Exception) {
            Toast.makeText(this@BookHomeActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val editBookSubmitListener = object : BookEditDialog.OnSubmitListener {
        override fun onSubmit(bookName: String, bank: Bank, currencyType: CurrencyType) {
            if (bookName.isEmpty()) {
                tilBookName.error = getString(R.string.mandatory_field)
                return
            }

            val bookInfo = mapOf(
                Constants.FIELD_NAME to bookName,
                Constants.FIELD_BANK to bank,
                Constants.FIELD_CURRENCY_TYPE to currencyType)

            editBookDialog.dismiss()
            dialogWaiting.show()

            BookService.patchBook(book, bookInfo, bookUpdatedCallback)
        }
    }

    @Deprecated("No need this listener so far")
    private val entryItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val entry = listEntry.adapter.getItem(position) as EntryVO
        Toast.makeText(this, entry.id, Toast.LENGTH_SHORT).show()
    }

    private val entryItemLongClickListener =
        AdapterView.OnItemLongClickListener { parent, view, position, id ->
            selectedEntryIndex = position
            entryActionDialog.show()

            true
        }

    private val entryActionClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            Constants.INDEX_EDIT -> {
                val entry = listEntry.adapter.getItem(selectedEntryIndex) as EntryVO

                val intent = Intent(this, EntryEditActivity::class.java)
                intent.putExtra(Constants.KEY_ACTION, Constants.ACTION_UPDATE)
                intent.putExtra(Constants.KEY_BOOK, book)
                intent.putExtra(Constants.KEY_ENTRY, entry)
                startActivityForResult(intent, Constants.REQUEST_EDIT_ENTRY)
            }
            Constants.INDEX_DELETE -> deleteEntryConfirmDialog.show()
        }
    }

    private val deleteBookConfirmListener = DialogInterface.OnClickListener { dialogInterface, which ->
        if (which == DialogInterface.BUTTON_POSITIVE) {
            dialogWaiting.show()
            BookService.deleteBook(book.id, bookDeletedListener)
        }
    }

    private val deleteEntryConfirmListener = DialogInterface.OnClickListener { dialogInterface, which ->
        if (which == DialogInterface.BUTTON_POSITIVE) {
            dialogWaiting.show()
            val entry = listEntry.adapter.getItem(selectedEntryIndex) as EntryVO
            EntryService.deleteEntry(entry.id, entryDeletedListener)
        }
    }

    private val bookUpdatedCallback = object : GeneralCallback<BookVO> {
        override fun onFinish(data: BookVO?) {
            dialogWaiting.dismiss()

            prgBar.visibility = View.VISIBLE
            listEntry.visibility = View.INVISIBLE
            txtForeignBalance.visibility = View.INVISIBLE
            txtForeignCurrency.visibility = View.INVISIBLE
            txtSellValue.visibility = View.INVISIBLE
            txtROI.visibility = View.INVISIBLE
            txtROIRate.visibility = View.INVISIBLE

            book = data!!
            toolbar.title = book.name
            EntryService.loadEntries(book.id, entriesLoadedCallback)
            // TODO: set activity result
        }

        override fun onException(e: Exception) {
            dialogWaiting.dismiss()
            Toast.makeText(this@BookHomeActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val bookDeletedListener = object : GeneralCallback<String> {
        override fun onFinish(data: String?) {
            dialogWaiting.dismiss()
            Toast.makeText(this@BookHomeActivity, getString(R.string.message_delete_completed), Toast.LENGTH_SHORT).show()

            val bookId = data ?: return

            val intent = Intent()
            intent.putExtra(Constants.KEY_BOOK_ID, bookId)
            setResult(Constants.RESULT_DELETE_BOOK, intent)
            finish()
        }

        override fun onException(e: Exception) {
            dialogWaiting.dismiss()
            Toast.makeText(this@BookHomeActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val entryDeletedListener = object : GeneralCallback<Void> {
        override fun onFinish(data: Void?) {
            dialogWaiting.dismiss()
            Toast.makeText(this@BookHomeActivity, getString(R.string.message_delete_completed), Toast.LENGTH_SHORT).show()

            val adapter = (listEntry.adapter as EntryListAdapter)
            adapter.removeItem(selectedEntryIndex)
            selectedEntryIndex = DEFAULT_INDEX

            val entries = adapter.getAllItems()
            entriesLoadedCallback.onFinish(entries)
        }

        override fun onException(e: Exception) {
            dialogWaiting.dismiss()
            Toast.makeText(this@BookHomeActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val createEntryButtonClickListener = View.OnClickListener {
        val intent = Intent(this, EntryEditActivity::class.java)
        intent.putExtra(Constants.KEY_BOOK, book)
        intent.putExtra(Constants.KEY_ACTION, Constants.ACTION_CREATE)
        startActivityForResult(intent, Constants.REQUEST_CREATE_ENTRY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_home)

        val bundle = intent.extras!!
        book = bundle.getSerializable(Constants.KEY_BOOK) as BookVO

        initToolbar(book.name)
        initEditBookDialog()
        initDeleteBookConfirmDialog()
        initEntryActionDialog()
        initDeleteEntryConfirmDialog()
        initWaitingDialog()

        listEntry.onItemClickListener = entryItemClickListener
        listEntry.onItemLongClickListener = entryItemLongClickListener
        btnCreateEntry.setOnClickListener(createEntryButtonClickListener)

        listEntry.visibility = View.INVISIBLE
        EntryService.loadEntries(book.id, entriesLoadedCallback)
    }

    private fun initToolbar(title: String) {
        toolbar.title = title
        setSupportActionBar(toolbar)

        // listener should be set after setting toolbar to activity
        toolbar.setNavigationOnClickListener { finish() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initDeleteBookConfirmDialog() {
        deleteBookConfirmDialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.message_delete_book_confirm))
            .setPositiveButton(getString(R.string.ok), deleteBookConfirmListener)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
    }

    private fun initEntryActionDialog() {
        val actions = mutableListOf<String>()
        actions.add(Constants.INDEX_EDIT, getString(R.string.edit))
        actions.add(Constants.INDEX_DELETE, getString(R.string.delete))

        entryActionDialog = AlertDialog.Builder(this)
            .setItems(actions.toTypedArray(), entryActionClickListener)
            .create()
    }

    private fun initDeleteEntryConfirmDialog() {
        deleteEntryConfirmDialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.message_delete_entry_confirm))
            .setPositiveButton(getString(R.string.ok), deleteEntryConfirmListener)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
    }

    private fun initWaitingDialog() {
        dialogWaiting = Dialog(this)
        dialogWaiting.setContentView(R.layout.dialog_waiting)
        dialogWaiting.setCancelable(false)
    }

    private fun initEditBookDialog() {
        editBookDialog = BookEditDialog(this,
            getString(R.string.title_edit_book), getString(R.string.desc_create_book))
        editBookDialog.setSubmitOnClickListener(editBookSubmitListener)
        tilBookName = editBookDialog.getView().findViewById(R.id.tilBookName)
    }

    private fun displayEntries(entries: List<EntryVO>) {
        val adapter = listEntry.adapter

        if (adapter == null) {
            listEntry.adapter = EntryListAdapter(this, entries.toMutableList())
        } else {
            (listEntry.adapter as EntryListAdapter).setItems(entries)
        }

        listEntry.visibility = View.VISIBLE
    }

    private fun displayDashboard(exchangeRate: ExchangeRate) {
        val twdSellValue = BigDecimal(book.foreignBalance)
            .multiply(BigDecimal(exchangeRate.debit))
            .divide(BigDecimal(1), 0, BigDecimal.ROUND_HALF_DOWN)
            .toInt()

        val roi = twdSellValue - book.taiwanBalance
        val roiPercentage =
            if (twdSellValue == 0) 0.0
            else {
                BigDecimal(roi)
                    .divide(BigDecimal(book.taiwanBalance), 4, BigDecimal.ROUND_HALF_DOWN)
                    .multiply(BigDecimal(100))
                    .toDouble()
            }

        txtForeignBalance.text = FormatUtils.formatMoney(book.foreignBalance)
        txtForeignCurrency.text = book.currencyType.name
        txtSellValue.text = FormatUtils.formatMoney(twdSellValue)
        txtROI.text = FormatUtils.formatMoney(roi)
        txtROIRate.text = StringBuilder("($roiPercentage%)").toString()

        txtForeignBalance.visibility = View.VISIBLE
        txtForeignCurrency.visibility = View.VISIBLE
        txtSellValue.visibility = View.VISIBLE
        txtROI.visibility = View.VISIBLE
        txtROIRate.visibility = View.VISIBLE
    }

    private fun onReceiveCreatedEntry(data: Intent?) {
        val entry = data?.getSerializableExtra(Constants.KEY_ENTRY) as EntryVO

        val entries = (listEntry.adapter as EntryListAdapter).getAllItems()
        entries.add(0, entry)
        entriesLoadedCallback.onFinish(entries)
    }

    private fun onReceiveUpdatedEntry(data: Intent?) {
        val entry = data?.getSerializableExtra(Constants.KEY_ENTRY) as EntryVO

        val adapter = (listEntry.adapter as EntryListAdapter)
        adapter.setItem(selectedEntryIndex, entry)

        val entries = adapter.getAllItems()
        entriesLoadedCallback.onFinish(entries)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_CREATE_ENTRY && resultCode == Activity.RESULT_OK) {
            onReceiveCreatedEntry(data)
        } else if (requestCode == Constants.REQUEST_EDIT_ENTRY && resultCode == Activity.RESULT_OK) {
            onReceiveUpdatedEntry(data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_home_page_menu_items, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_edit -> editBookDialog.show(book)
            R.id.action_delete -> deleteBookConfirmDialog.show()
        }

        return true
    }

}
