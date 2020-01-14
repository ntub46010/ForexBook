package com.vincent.forexbook.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.vincent.forexbook.Constants
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.R
import com.vincent.forexbook.activity.BookHomeActivity
import com.vincent.forexbook.adapter.BookListAdapter
import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.BookVO
import com.vincent.forexbook.entity.BookPO
import com.vincent.forexbook.entity.CurrencyType
import com.vincent.forexbook.service.BookService
import kotlinx.android.synthetic.main.fragment_book_list.*
import java.util.*

class BookListFragment : Fragment() {

    private lateinit var tilBookName: TextInputLayout
    private lateinit var editBookName: EditText
    private lateinit var spinnerBank: Spinner
    private lateinit var spinnerCurrencyType: Spinner

    private lateinit var dialogCreateBook: AlertDialog
    private lateinit var dialogWaiting: Dialog

    private val bookItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val book = listBook.adapter.getItem(position) as BookVO

        val intent = Intent(context!!, BookHomeActivity::class.java)
        intent.putExtra(Constants.FIELD_ID, book.id)
        intent.putExtra(Constants.FIELD_NAME, book.name)
        startActivity(intent)
    }

    private val dialogShowListener = DialogInterface.OnShowListener {
        tilBookName.error = null
        editBookName.text = null
        spinnerCurrencyType.setSelection(0)

        // button is available after dialog shows
        dialogCreateBook.getButton(DialogInterface.BUTTON_POSITIVE)
            .setOnClickListener(dialogCreateClickListener)
    }

    private val spinnerBankListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val bank = Bank.findByChineseName(parent?.selectedItem.toString())!!
            val currencyNames = bank.supportingCurrencyType.asSequence()
                .map { it.getTitle() }
                .toList()

            val adapter = spinnerCurrencyType.adapter
            if (adapter == null) {
                spinnerCurrencyType.adapter = ArrayAdapter(context!!,
                    android.R.layout.simple_spinner_dropdown_item, currencyNames)
            } else {
                adapter as ArrayAdapter<String>
                adapter.clear()
                adapter.addAll(currencyNames)
            }

            spinnerCurrencyType.setSelection(0, true)
        }
    }

    private val dialogCreateClickListener = View.OnClickListener {
        val name = editBookName.text
        if (name == null || name.isEmpty()) {
            tilBookName.error = context!!.getString(R.string.mandatory_field)
            return@OnClickListener
        }

        val strBank = spinnerBank.selectedItem.toString()
        val strCurrencyType = spinnerCurrencyType.selectedItem.toString()
        val currencyCode = strCurrencyType.split(" ")[1]

        val request = BookPO(
            name.toString(),
            Bank.findByChineseName(strBank)!!,
            CurrencyType.valueOf(currencyCode),
            createdTime = Date())

        dialogCreateBook.dismiss()
        dialogWaiting.show()
        BookService.createBook(request, bookCreatedListener)
    }

    private val bookCreatedListener = object : GeneralCallback<BookVO> {
        override fun onFinish(data: BookVO?) {
            Toast.makeText(context!!, context!!.getString(R.string.message_create_successful), Toast.LENGTH_SHORT).show()
            dialogWaiting.dismiss()
            (listBook.adapter as BookListAdapter).addItemToFirst(data!!)
        }

        override fun onException(e: Exception) {
            Toast.makeText(context!!, e.message, Toast.LENGTH_SHORT).show()
            dialogWaiting.dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initCreateDialog()
        initWaitingDialog()
        btnCreateBook.setOnClickListener { dialogCreateBook.show() }
        listBook.onItemClickListener = bookItemClickListener

        prgBar.visibility = View.VISIBLE
        loadBooks()
    }

    private fun loadBooks() {
        val callback = object : GeneralCallback<List<BookVO>> {
            override fun onFinish(data: List<BookVO>?) {
                activity?.runOnUiThread {
                    displayBooks(data ?: emptyList())
                }
            }

            override fun onException(e: Exception) {
                activity?.runOnUiThread {
                    prgBar.visibility = View.INVISIBLE
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        BookService.loadBooks(callback)
    }

    private fun displayBooks(books: List<BookVO>) {
        prgBar.visibility = View.INVISIBLE
        val adapter = listBook.adapter

        if (adapter == null) {
            listBook.adapter = BookListAdapter(context!!, books.toMutableList())
        }
    }

    private fun initCreateDialog() {
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_create_book, null)
        tilBookName = layout.findViewById(R.id.tilBookName)
        editBookName = layout.findViewById(R.id.editBookName)
        spinnerBank = layout.findViewById(R.id.spinnerBank)
        spinnerCurrencyType = layout.findViewById(R.id.spinnerCurrencyType)

        spinnerBank.adapter = ArrayAdapter(context!!,
            android.R.layout.simple_spinner_dropdown_item, Bank.getChineseNames())
        spinnerBank.setSelection(0, true)
        spinnerBank.onItemSelectedListener = spinnerBankListener

        dialogCreateBook = AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.title_create_book))
            .setMessage(getString(R.string.desc_create_book))
            .setView(layout)
            .setPositiveButton(getString(R.string.ok), null)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialogCreateBook.setOnShowListener(dialogShowListener)
    }

    private fun initWaitingDialog() {
        dialogWaiting = Dialog(context!!)
        dialogWaiting.setContentView(R.layout.dialog_waiting)
        dialogWaiting.setCancelable(false)
    }

}