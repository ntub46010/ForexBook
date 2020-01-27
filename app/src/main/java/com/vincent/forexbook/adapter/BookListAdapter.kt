package com.vincent.forexbook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.vincent.forexbook.R
import com.vincent.forexbook.entity.BookVO

class BookListAdapter(private val context: Context,
                      private val books: MutableList<BookVO>) : BaseAdapter() {

    override fun getItem(position: Int): Any {
        return books[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return books.size
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val bookView = view ?:
                LayoutInflater.from(context).inflate(R.layout.item_book, parent, false)

        val imgCurrency = bookView.findViewById<ImageView>(R.id.imgCurrency)
        val txtName = bookView.findViewById<TextView>(R.id.txtName)

        val book = books[position]
        imgCurrency.setImageResource(book.currencyType.iconResource)
        txtName.text = book.name

        return bookView
    }

    fun addItemToFirst(book: BookVO) {
        books.add(0, book)
        notifyDataSetChanged()
    }

    fun replaceItem(book: BookVO) {
        val index = books.indexOfFirst { it.id == book.id }
        if (index > -1) {
            books[index] = book
            notifyDataSetChanged()
        }
    }

    fun removeItem(bookId: String) {
        val book = books.asSequence()
            .filter { it.id == bookId }
            .firstOrNull()

        if (book != null) {
            books.remove(book)
            notifyDataSetChanged()
        }
    }

}