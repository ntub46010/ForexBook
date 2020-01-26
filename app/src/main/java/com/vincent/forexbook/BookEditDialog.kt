package com.vincent.forexbook

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.BookVO

class BookEditDialog(context: Context,
                     title: String,
                     message: String)
    : AlertDialog(context) {

    private lateinit var tilBookName: TextInputLayout
    private lateinit var editBookName: EditText
    private lateinit var spinnerBank: Spinner
    private lateinit var spinnerCurrencyType: Spinner

    private var submitOnClickListener: DialogInterface.OnClickListener? = null

    init {
        setTitle(title)
        setMessage(message)

        val layout = findView()
        setView(layout)
        initBankSpinner()

        setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), submitOnClickListener)
        setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel))
            { dialogInterface, which -> }
    }

    override fun show() {
        super.show()

        tilBookName.error = null
        editBookName.text = null
        spinnerCurrencyType.setSelection(0)
    }

    fun show(book: BookVO) {
        super.show()

        editBookName.setText(book.name)

        val bank = book.bank
        spinnerBank.setSelection(bank.ordinal)

        val supportingCurrencyType = bank.supportingCurrencyType
        val currencyTypeIndex = supportingCurrencyType.indexOf(book.currencyType)
        spinnerCurrencyType.setSelection(currencyTypeIndex)
    }

    fun setSubmitOnClickListener(listener: DialogInterface.OnClickListener) {
        submitOnClickListener = listener
    }

    private fun findView(): View {
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_create_book, null)
        tilBookName = layout.findViewById(R.id.tilBookName)
        editBookName = layout.findViewById(R.id.editBookName)
        spinnerBank = layout.findViewById(R.id.spinnerBank)
        spinnerCurrencyType = layout.findViewById(R.id.spinnerCurrencyType)

        return layout
    }

    private fun initBankSpinner() {
        spinnerBank.adapter = ArrayAdapter(context,
            android.R.layout.simple_spinner_dropdown_item, Bank.getChineseNames())
        spinnerBank.setSelection(0, true)
        spinnerBank.onItemSelectedListener = getBankSelectedListener()
    }

    private fun getBankSelectedListener(): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val bank = Bank.findByChineseName(parent?.selectedItem.toString())!!
                val currencyNames = bank.supportingCurrencyType.asSequence()
                    .map { it.getTitle() }
                    .toList()

                val adapter = spinnerCurrencyType.adapter
                if (adapter == null) {
                    spinnerCurrencyType.adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_dropdown_item, currencyNames)
                } else {
                    adapter as ArrayAdapter<String>
                    adapter.clear()
                    adapter.addAll(currencyNames)
                }

                spinnerCurrencyType.setSelection(0, true)
            }
        }
    }
}