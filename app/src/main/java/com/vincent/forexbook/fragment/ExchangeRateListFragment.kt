package com.vincent.forexbook.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vincent.forexbook.Constants
import com.vincent.forexbook.adapter.ExchangeRateListAdapter
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.MyApplication
import com.vincent.forexbook.R
import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.ExchangeRate
import com.vincent.forexbook.service.ExchangeRateService
import kotlinx.android.synthetic.main.fragment_exchange_rate_list.*

class ExchangeRateListFragment : Fragment() {

    private var selectedBank = Bank.FUBON
    private var defaultBank: Bank? = null
    private var isInitSpinner = true

    private lateinit var preference: SharedPreferences

    private val checkBoxDefaultBankListener = View.OnClickListener {
        if (checkDefaultBank.isChecked) {
            val bank = Bank.values()[spinnerBank.selectedItemPosition]
            defaultBank = bank
            preference.edit().putString(Constants.KEY_DEFAULT_BANK, bank.name).apply()
        } else {
            defaultBank = null
            preference.edit().remove(Constants.KEY_DEFAULT_BANK).apply()
        }
    }

    private val spinnerBankListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (isInitSpinner) {
                isInitSpinner = false
                return
            }

            selectedBank = Bank.findByChineseName(parent?.selectedItem.toString())!!
            checkDefaultBank.isChecked = selectedBank == defaultBank
            prgBar.visibility = View.VISIBLE
            listExchangeRate.visibility = View.INVISIBLE

            loadExchangeRate(selectedBank)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_exchange_rate_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preference = context!!.getSharedPreferences(MyApplication.INSTANCE.packageName, Context.MODE_PRIVATE)
        val spDefaultBank = preference.getString(Constants.KEY_DEFAULT_BANK, null)
        if (spDefaultBank != null) {
            defaultBank = Bank.valueOf(spDefaultBank)
            selectedBank = defaultBank!!
        }

        listExchangeRate.layoutManager = LinearLayoutManager(context)

        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary, activity?.theme))
        swipeRefreshLayout.setOnRefreshListener { loadExchangeRate(selectedBank) }

        checkDefaultBank.isChecked = selectedBank == defaultBank
        checkDefaultBank.setOnClickListener(checkBoxDefaultBankListener)

        spinnerBank.adapter = ArrayAdapter(context!!,
            android.R.layout.simple_spinner_dropdown_item, Bank.getChineseNames())
        spinnerBank.setSelection(selectedBank.ordinal, true)
        spinnerBank.onItemSelectedListener = spinnerBankListener

        prgBar.visibility = View.VISIBLE
        loadExchangeRate(selectedBank)
    }

    private fun loadExchangeRate(bank: Bank) {
        val callback = object : GeneralCallback<List<ExchangeRate>> {
            override fun onFinish(data: List<ExchangeRate>?) {
                activity?.runOnUiThread {
                    displayExchangeRate(data ?: emptyList())
                }
            }

            override fun onException(e: Exception) {
                activity?.runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    prgBar.visibility = View.INVISIBLE
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        ExchangeRateService.loadExchangeRates(bank, callback)
    }

    private fun displayExchangeRate(exchangeRates: List<ExchangeRate>) {
        swipeRefreshLayout.isRefreshing = false
        prgBar.visibility = View.INVISIBLE
        listExchangeRate.visibility = View.VISIBLE
        val adapter = listExchangeRate.adapter

        if (adapter == null) {
            listExchangeRate.adapter = ExchangeRateListAdapter(exchangeRates)
        } else {
            (adapter as ExchangeRateListAdapter).refreshData(exchangeRates)
        }
    }

}