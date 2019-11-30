package com.vincent.forexbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vincent.forexbook.R
import com.vincent.forexbook.entity.ExchangeRate
import com.vincent.forexbook.util.FormatUtils

class ExchangeRateListAdapter(var exchangeRates: List<ExchangeRate>)
    : RecyclerView.Adapter<ExchangeRateListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exchange_rate, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return exchangeRates.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exchangeRate = exchangeRates[position]
        holder.bindView(exchangeRate)
    }

    fun refreshData(exchangeRates: List<ExchangeRate>) {
        this.exchangeRates = exchangeRates
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val imgIcon = v.findViewById<ImageView>(R.id.imgIcon)
        private val txtTitle = v.findViewById<TextView>(R.id.txtTitle)
        private val txtCredit = v.findViewById<TextView>(R.id.txtCredit)
        private val txtDebit = v.findViewById<TextView>(R.id.txtDebit)

        fun bindView(exchangeRate: ExchangeRate) {
            imgIcon.setImageResource(exchangeRate.currencyType.iconResource)
            txtTitle.text = exchangeRate.currencyType.getTitle()
            txtCredit.text = FormatUtils.formatDecimalPlaces(exchangeRate.credit, 4)
            txtDebit.text = FormatUtils.formatDecimalPlaces(exchangeRate.debit, 4)
        }
    }
}