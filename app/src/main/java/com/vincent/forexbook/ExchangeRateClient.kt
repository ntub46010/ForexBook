package com.vincent.forexbook

import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.CurrencyType
import com.vincent.forexbook.entity.ExchangeRate
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException

object ExchangeRateClient {

    private val client = OkHttpClient().newBuilder().build()

    fun loadExchangeRate(bank: Bank, callback: GeneralCallback<List<ExchangeRate>>) {
        val request = Request.Builder()
            .url(bank.exchangeRateUrl)
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val htmlContent = response.body?.string() ?: ""
                    val exchangeRates = parseHtmlToEntities(htmlContent)
                    callback.onFinish(exchangeRates)
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onException(e)
                }
            })
    }

    private fun parseHtmlToEntities(htmlContent: String): List<ExchangeRate> {
        val tableRows = Jsoup.parse(htmlContent)
            .select("div[id=right]")
            .select("table>tbody>tr")
            .drop(1)
            .dropLast(4)

        return tableRows.asSequence()
            .map {
                val tableDatas = it.select("td")
                val currencyCode = tableDatas[0].select("a").text().split(" ")[1]
                val currencyType = CurrencyType.valueOf(currencyCode)

                ExchangeRate(
                    currencyType,
                    tableDatas[4].text().toDouble(),
                    tableDatas[3].text().toDouble())
            }
            .toList()
    }

}