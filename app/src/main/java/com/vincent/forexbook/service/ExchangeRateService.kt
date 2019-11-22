package com.vincent.forexbook.service

import com.vincent.forexbook.CacheData
import com.vincent.forexbook.NetworkClient
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.CurrencyType
import com.vincent.forexbook.entity.ExchangeRate
import org.jsoup.Jsoup
import java.util.*
import kotlin.math.ceil

object ExchangeRateService {

    private val exchangeRateCache = EnumMap<Bank, CacheData<List<ExchangeRate>>>(Bank::class.java)

    fun loadExchangeRate(bank: Bank, uiCallback: GeneralCallback<List<ExchangeRate>>) {
        val cacheData = getCacheData(bank)
        if (cacheData != null) {
            println("============\nUse cache data\n============")
            uiCallback.onFinish(cacheData)
            return
        }

        println("============\nRequest network resource\n============")
        val networkCallback = object : GeneralCallback<String> {
            override fun onFinish(data: String?) {
                val htmlContent = data ?: ""
                val exchangeRates = parseHtmlToEntities(htmlContent)
                    .sortedBy { it.currencyType.ordinal }

                saveCacheData(bank, exchangeRates)
                uiCallback.onFinish(exchangeRates)
            }

            override fun onException(e: Exception) {
                uiCallback.onException(e)
            }
        }

        NetworkClient.loadExchangeRate(bank.exchangeRateUrl, networkCallback)
    }

    private fun getCacheData(bank: Bank): List<ExchangeRate>? {
        val cache = exchangeRateCache[bank]

        if (cache == null) {
            return null
        } else if (cache.isExpired()) {
            exchangeRateCache.remove(bank)
            return null
        } else {
            return cache.data
        }
    }

    private fun saveCacheData(bank: Bank, exchangeRates: List<ExchangeRate>) {

        println("============\nSave cache\n============")
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