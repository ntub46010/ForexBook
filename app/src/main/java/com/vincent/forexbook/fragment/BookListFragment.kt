package com.vincent.forexbook.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.vincent.forexbook.BookEditDialog
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

    private lateinit var dialogCreateBook: BookEditDialog
    private lateinit var dialogWaiting: Dialog

    private val bookItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val book = listBook.adapter.getItem(position) as BookVO

        val intent = Intent(context!!, BookHomeActivity::class.java)
        intent.putExtra(Constants.KEY_BOOK, book)
        startActivityForResult(intent, Constants.REQUEST_ACCESS_BOOK)
    }

    private val createBookClickListener = object : BookEditDialog.OnSubmitListener {
        override fun onSubmit(bookName: String, bank: Bank, currencyType: CurrencyType) {
            if (bookName.isEmpty()) {
                tilBookName.error = context!!.getString(R.string.mandatory_field)
                return
            }

            val request = BookPO(
                bookName,
                bank,
                currencyType,
                createdTime = Date())

            dialogCreateBook.dismiss()
            dialogWaiting.show()
            BookService.createBook(request, bookCreatedListener)
        }
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

        initCreateBookDialog()
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

    private fun initCreateBookDialog() {
        dialogCreateBook = BookEditDialog(context!!,
            getString(R.string.title_create_book), getString(R.string.desc_create_book))
        dialogCreateBook.setSubmitOnClickListener(createBookClickListener)
        tilBookName = dialogCreateBook.getView().findViewById(R.id.tilBookName)
    }

    private fun initWaitingDialog() {
        dialogWaiting = Dialog(context!!)
        dialogWaiting.setContentView(R.layout.dialog_waiting)
        dialogWaiting.setCancelable(false)
    }

    private fun onNotifyBookDeleted(data: Intent?) {
        val bookId = data?.getStringExtra(Constants.KEY_BOOK_ID) ?: return
        (listBook.adapter as BookListAdapter).removeItem(bookId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_ACCESS_BOOK && resultCode == Constants.RESULT_DELETE_BOOK) {
            onNotifyBookDeleted(data)
        }
    }

}