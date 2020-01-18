package com.vincent.forexbook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.vincent.forexbook.R
import com.vincent.forexbook.entity.EntryType
import com.vincent.forexbook.entity.EntryVO
import com.vincent.forexbook.util.FormatUtils

class EntryListAdapter(private val context: Context,
                       private val entries: MutableList<EntryVO>)
    : BaseAdapter() {

    override fun getItem(position: Int) = entries[position]
    override fun getItemId(position: Int) = 0L
    override fun getCount() = entries.size

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val entryView = view ?:
            LayoutInflater.from(context).inflate(R.layout.item_entry, parent, false)

        val txtDate = entryView.findViewById<TextView>(R.id.txtDate)
        val txtEntryType = entryView.findViewById<TextView>(R.id.txtEntryType)
        val txtForeignAmt = entryView.findViewById<TextView>(R.id.txtForeignAmt)
        val txtTaiwanAmt = entryView.findViewById<TextView>(R.id.txtTaiwanAmt)
        val txtCurrencyType = entryView.findViewById<TextView>(R.id.txtForeignType)

        val entry = getItem(position)

        txtDate.text = FormatUtils.formatDate(entry.createdTime)
        txtEntryType.text =
            if (entry.entryType === EntryType.CREDIT) context.getString(R.string.credit)
            else context.getString(R.string.debit)
        txtForeignAmt.text = FormatUtils.formatMoney(entry.fcyAmt)
        txtTaiwanAmt.text = FormatUtils.formatMoney(entry.twdAmt)
        txtCurrencyType.text = entry.currencyType.name

        return entryView
    }
}