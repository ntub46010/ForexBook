package com.vincent.forexbook.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vincent.forexbook.adapter.ExchangeRateListAdapter
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.R
import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.CurrencyType
import com.vincent.forexbook.entity.ExchangeRate
import com.vincent.forexbook.service.ExchangeRateService
import kotlinx.android.synthetic.main.fragment_exchange_rate_list.*

class ExchangeRateListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_exchange_rate_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listExchangeRate.layoutManager = LinearLayoutManager(context)

        loadExchangeRate(Bank.FUBON)
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
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        ExchangeRateService.loadExchangeRate(bank, callback)
    }

    private fun displayExchangeRate(exchangeRates: List<ExchangeRate>) {
        val adapter = listExchangeRate.adapter

        if (adapter == null) {
            listExchangeRate.adapter = ExchangeRateListAdapter(exchangeRates)
        } else {
            (adapter as ExchangeRateListAdapter).refreshData(exchangeRates)
        }
    }

    private fun getMockExchangeRate() =
        listOf(
            ExchangeRate(CurrencyType.USD, 30.5810, 30.4810),
            ExchangeRate(CurrencyType.CNY, 4.3807, 4.3307),
            ExchangeRate(CurrencyType.JPY, 0.2828, 0.2792),
            ExchangeRate(CurrencyType.EUR, 33.8452, 33.4452),
            ExchangeRate(CurrencyType.HKD, 3.9269, 3.8729),
            ExchangeRate(CurrencyType.AUD, 20.8760, 20.5760),
            ExchangeRate(CurrencyType.ZAR, 2.1200, 2.0100),
            ExchangeRate(CurrencyType.CAD, 23.2045, 22.9045),
            ExchangeRate(CurrencyType.GBP, 39.5487, 39.0687),
            ExchangeRate(CurrencyType.SGD, 22.5364, 22.2964),
            ExchangeRate(CurrencyType.CHF, 30.9761, 30.6561),
            ExchangeRate(CurrencyType.NZD, 19.6162, 19.3262),
            ExchangeRate(CurrencyType.SEK, 3.1861, 3.1261),
            ExchangeRate(CurrencyType.THB, 1.0301, 0.9901)
        )
}