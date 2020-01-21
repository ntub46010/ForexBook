package com.vincent.forexbook.activity

import android.app.Activity
import android.content.Intent
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
import com.vincent.forexbook.entity.*
import com.vincent.forexbook.service.EntryService
import com.vincent.forexbook.service.ExchangeRateService
import com.vincent.forexbook.util.EntityConverter
import com.vincent.forexbook.util.FormatUtils
import kotlinx.android.synthetic.main.activity_book_home.*
import kotlinx.android.synthetic.main.content_book_home_dashboard.*
import kotlinx.android.synthetic.main.content_toolbar.toolbar
import java.math.BigDecimal

class BookHomeActivity : AppCompatActivity() {

    private lateinit var book: BookVO

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

    private val entryItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val entry = listEntry.adapter.getItem(position) as EntryVO
        Toast.makeText(this, entry.id, Toast.LENGTH_SHORT).show()
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
        listEntry.onItemClickListener = entryItemClickListener
        btnCreateEntry.setOnClickListener(createEntryButtonClickListener)

        listEntry.visibility = View.INVISIBLE
        //EntryService.loadEntries(book.id, entriesLoadedCallback)
        EntryService.loadMockEntries(book.currencyType, entriesLoadedCallback)
    }

    private fun initToolbar(title: String) {
        toolbar.title = title
        setSupportActionBar(toolbar)

        // listener should be set after setting toolbar to activity
        toolbar.setNavigationOnClickListener { finish() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun displayEntries(entries: List<EntryVO>) {
        val adapter = listEntry.adapter

        if (adapter == null) {
            listEntry.adapter = EntryListAdapter(this, entries.toMutableList())
        } else {
            (listEntry.adapter as EntryListAdapter).setItems(entries)
        }
    }

    private fun displayDashboard(exchangeRate: ExchangeRate) {
        val twdSellValue = BigDecimal(book.foreignBalance)
            .multiply(BigDecimal(exchangeRate.debit))
            .divide(BigDecimal(1), 0, BigDecimal.ROUND_HALF_DOWN)
            .toInt()

        val roi = twdSellValue - book.taiwanBalance
        val roiPercentage = BigDecimal(roi)
            .divide(BigDecimal(twdSellValue), 4, BigDecimal.ROUND_HALF_DOWN)
            .multiply(BigDecimal(100))
            .toDouble()

        txtForeignBalance.text = FormatUtils.formatMoney(book.foreignBalance)
        txtForeignCurrency.text = book.currencyType.name
        txtSellValue.text = FormatUtils.formatMoney(twdSellValue)
        txtROI.text = FormatUtils.formatMoney(roi)
        txtROIRate.text = StringBuilder("($roiPercentage%)").toString()
    }

    private fun onReceiveCreatedEntry(data: Intent?) {
        val id = data?.getStringExtra(Constants.KEY_ID) ?: return
        val entryPO = data.getSerializableExtra(Constants.KEY_ENTRY) as EntryPO
        val entryVO = EntityConverter.convertToEntryVO(id, entryPO)

        val entries = (listEntry.adapter as EntryListAdapter).getAllItems()
        entries.add(0, entryVO)
        entriesLoadedCallback.onFinish(entries)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_CREATE_ENTRY && resultCode == Activity.RESULT_OK) {
            onReceiveCreatedEntry(data)
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
