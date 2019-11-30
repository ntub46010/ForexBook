package com.vincent.forexbook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.vincent.forexbook.R
import com.vincent.forexbook.entity.Book

class BookListAdapter(private val context: Context,
                      private val books: List<Book>) : BaseAdapter() {

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

}