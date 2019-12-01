package com.vincent.forexbook.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.vincent.forexbook.R
import com.vincent.forexbook.adapter.BookListAdapter
import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.Book
import com.vincent.forexbook.entity.CurrencyType
import kotlinx.android.synthetic.main.fragment_book_list.*
import java.util.*

class BookListFragment : Fragment() {

    private lateinit var dialogCreateBook: AlertDialog

    private val listBookListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val book = listBook.adapter.getItem(position) as Book
        Toast.makeText(context!!, book.id, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnCreateBook.setOnClickListener {
            Toast.makeText(context!!, "create book", Toast.LENGTH_SHORT).show()
        }

        val books = getDefaultBookMockData()
        val adapter = listBook.adapter
        if (adapter == null) {
            listBook.adapter = BookListAdapter(context!!, books)
        }
        listBook.onItemClickListener = listBookListener
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