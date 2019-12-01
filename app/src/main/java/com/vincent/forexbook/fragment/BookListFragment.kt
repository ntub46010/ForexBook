package com.vincent.forexbook.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.vincent.forexbook.R
import com.vincent.forexbook.adapter.BookListAdapter
import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.Book
import com.vincent.forexbook.entity.CurrencyType
import kotlinx.android.synthetic.main.fragment_book_list.*
import java.util.*

class BookListFragment : Fragment() {

    private lateinit var tilBookName: TextInputLayout
    private lateinit var editBookName: EditText
    private lateinit var spinnerBank: Spinner
    private lateinit var spinnerCurrencyType: Spinner

    private lateinit var dialogCreateBook: AlertDialog

    private val bookItemListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val book = listBook.adapter.getItem(position) as Book
        Toast.makeText(context!!, book.id, Toast.LENGTH_SHORT).show()
    }

    private val dialogShowListener = DialogInterface.OnShowListener {
        tilBookName.error = null
        editBookName.text = null
        spinnerCurrencyType.setSelection(0)

        // button is available after dialog shows
        dialogCreateBook.getButton(DialogInterface.BUTTON_POSITIVE)
            .setOnClickListener(dialogCreateBookListener)
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

    private val dialogCreateBookListener = View.OnClickListener {
        val name = editBookName.text
        if (name == null || name.isEmpty()) {
            tilBookName.error = context!!.getString(R.string.mandatory_field)
            return@OnClickListener
        }

        val strBank = spinnerBank.selectedItem.toString()
        val strCurrencyType = spinnerCurrencyType.selectedItem.toString()
        val currencyCode = strCurrencyType.split(" ")[1]

        val book = Book(
            null,
            name.toString(),
            Bank.findByChineseName(strBank)!!,
            CurrencyType.valueOf(currencyCode),
            Date())

        dialogCreateBook.dismiss()
        Toast.makeText(context!!, book.toString(), Toast.LENGTH_SHORT).show()
        (listBook.adapter as BookListAdapter).addItem(book)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnCreateBook.setOnClickListener {
            dialogCreateBook.show()
        }

        val books = getDefaultBookMockData()
        val adapter = listBook.adapter
        if (adapter == null) {
            listBook.adapter = BookListAdapter(context!!, books.toMutableList())
        }
        listBook.onItemClickListener = bookItemListener

        initCreateDialog()
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

    private fun getDefaultBookMockData(): List<Book> {
        val books = mutableListOf<Book>()

        for (type in CurrencyType.values()) {
            books.add(Book(type.iconResource.toString(), type.getTitle(),
                Bank.FUBON, type, Date()))
        }

        return books
    }
}