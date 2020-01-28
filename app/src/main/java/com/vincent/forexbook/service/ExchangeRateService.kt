package com.vincent.forexbook.service

import com.vincent.forexbook.cache.CacheData
import com.vincent.forexbook.Constants
import com.vincent.forexbook.NetworkClient
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.cache.ICacheService
import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.CurrencyType
import com.vincent.forexbook.entity.ExchangeRate
import com.vincent.forexbook.util.addMinutes
import com.vincent.forexbook.util.minus
import com.vincent.forexbook.util.nextClock
import org.jsoup.Jsoup
import java.util.*

object ExchangeRateService
    : ICacheService<Bank, Map<CurrencyType, ExchangeRate>> {

    override val cacheMap = EnumMap<Bank, CacheData<Map<CurrencyType, ExchangeRate>>>(Bank::class.java)
    private const val updateInterval = 3

    fun loadExchangeRates(bank: Bank, clientCallback: GeneralCallback<List<ExchangeRate>>) {
        val cacheData = getCache(bank)
        if (cacheData != null) {
            clientCallback.onFinish(cacheData.values.toList())
            return
        }

        val now = Date()
        val cacheExpiredTime = calcCacheExpiredTime(now)
        val cacheMaxAgeTimeMill = cacheExpiredTime.minus(now).toInt()

        val networkCallback = object : GeneralCallback<String> {
            override fun onFinish(data: String?) {
                val htmlContent = data ?: ""

                val exchangeRateList = parseHtmlToEntities(htmlContent)
                    .sortedBy { it.currencyType.ordinal }
                if (bank == Bank.RICHART) {
                    calcRichartExchangeRate(exchangeRateList)
                }

                val exchangeRateMap = exchangeRateList
                    .map { it.currencyType to it }
                    .toMap()

                saveCache(bank, exchangeRateMap, cacheExpiredTime)
                clientCallback.onFinish(exchangeRateList)
            }

            override fun onException(e: Exception) {
                clientCallback.onException(e)
            }
        }

        NetworkClient.loadExchangeRate(bank.exchangeRateUrl, cacheMaxAgeTimeMill, networkCallback)
    }

    fun loadExchangeRate(bank: Bank, currencyType: CurrencyType,
                         clientCallback: GeneralCallback<ExchangeRate>) {
        val networkCallback = object : GeneralCallback<List<ExchangeRate>> {
            override fun onFinish(data: List<ExchangeRate>?) {
                val exchangeRate = data?.asSequence()
                    ?.firstOrNull { it.currencyType === currencyType }

                clientCallback.onFinish(exchangeRate)
            }

            override fun onException(e: Exception) {
                clientCallback.onException(e)
            }
        }

        loadExchangeRates(bank, networkCallback)
    }

    private fun calcRichartExchangeRate(exchangeRates: List<ExchangeRate>): List<ExchangeRate> {
        for (rate in exchangeRates) {
            when (rate.currencyType) {
                CurrencyType.USD -> {
                    rate.credit -= Constants.RICHART_DISCOUNT_USD
                    rate.debit += Constants.RICHART_DISCOUNT_USD
                }
                CurrencyType.JPY -> {
                    rate.credit -= Constants.RICHART_DISCOUNT_JPY
                    rate.debit += Constants.RICHART_DISCOUNT_JPY
                }
                CurrencyType.GBP -> {
                    rate.credit -= Constants.RICHART_DISCOUNT_GBP
                    rate.debit += Constants.RICHART_DISCOUNT_GBP
                }
                CurrencyType.CNY -> {
                    rate.credit -= Constants.RICHART_DISCOUNT_CNY
                    rate.debit += Constants.RICHART_DISCOUNT_CNY
                }
                CurrencyType.EUR -> {
                    rate.credit -= Constants.RICHART_DISCOUNT_EUR
                    rate.debit += Constants.RICHART_DISCOUNT_EUR
                }
                CurrencyType.HKD -> {
                    rate.credit -= Constants.RICHART_DISCOUNT_HKD
                    rate.debit += Constants.RICHART_DISCOUNT_HKD
                }
                CurrencyType.AUD -> {
                    rate.credit -= Constants.RICHART_DISCOUNT_AUD
                    rate.debit += Constants.RICHART_DISCOUNT_AUD
                }
                else -> {
                    // 優惠 = ( 牌告 - 中價 ) * 0.4
                    val discount = (rate.credit - rate.debit) / 5
                    rate.credit -= discount
                    rate.debit += discount
                }
            }
        }

        return exchangeRates
    }

    private fun calcCacheExpiredTime(now: Date): Date {
        val c = Calendar.getInstance()
        c.time = now

        val intervalStart = updateInterval
        val intervalEnd = 60 - updateInterval

        return if (c.get(Calendar.MINUTE) in intervalStart..intervalEnd) {
                now.nextClock().addMinutes(-updateInterval)
            } else {
                now
            }
    }

    private fun parseHtmlToEntities(htmlContent: String): List<ExchangeRate> {
        val tableRows = Jsoup.parse(htmlContent)
            .select("div[id=right]")
            .select("table>tbody>tr")
            .filter{ it.select("td").size == 5 }

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