package com.vincent.forexbook.entity

enum class Bank(val chineseName: String,
                val exchangeRateUrl: String,
                val supportingCurrencyType: List<CurrencyType>) {

    FUBON("富邦銀行", "https://www.findrate.tw/bank/8/",
        listOf(
            CurrencyType.USD,
            CurrencyType.CNY,
            CurrencyType.JPY,
            CurrencyType.EUR,
            CurrencyType.HKD,
            CurrencyType.AUD,
            CurrencyType.ZAR,
            CurrencyType.CAD,
            CurrencyType.GBP,
            CurrencyType.SGD,
            CurrencyType.CHF,
            CurrencyType.NZD,
            CurrencyType.SEK,
            CurrencyType.THB
        )),

    TAISHIN("台新銀行", "https://www.findrate.tw/bank/9/",
        listOf(
            CurrencyType.USD,
            CurrencyType.CNY,
            CurrencyType.JPY,
            CurrencyType.EUR,
            CurrencyType.HKD,
            CurrencyType.AUD,
            CurrencyType.ZAR,
            CurrencyType.CAD,
            CurrencyType.GBP,
            CurrencyType.SGD,
            CurrencyType.CHF,
            CurrencyType.NZD,
            CurrencyType.SEK,
            CurrencyType.THB
        )),

    RICHART("台新銀行（Richart）", "https://www.findrate.tw/bank/9/",
        listOf(
            CurrencyType.USD,
            CurrencyType.CNY,
            CurrencyType.JPY,
            CurrencyType.EUR,
            CurrencyType.HKD,
            CurrencyType.AUD,
            CurrencyType.ZAR,
            CurrencyType.CAD,
            CurrencyType.GBP,
            CurrencyType.SGD,
            CurrencyType.CHF,
            CurrencyType.NZD,
            CurrencyType.SEK
        ));

    companion object {
        fun getChineseNames() =
            values().asSequence()
                .map { it.chineseName }
                .toList()

        fun findByChineseName(chineseName: String) =
            values().asSequence()
                .filter { it.chineseName == chineseName }
                .firstOrNull()
    }
}