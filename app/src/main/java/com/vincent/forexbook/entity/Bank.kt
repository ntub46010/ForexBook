package com.vincent.forexbook.entity

enum class Bank(val chineseName: String,
                val exchangeRateUrl: String,
                private val supportingCurrency: List<CurrencyType>) {

    FUBON("富邦銀行", "https://www.findrate.tw/bank/8/#.XHv2PKBS8dU",
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

    TAISHIN("台新銀行", "https://www.findrate.tw/bank/9/#.XM5YhthS8dU",
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

    RICHART("台新銀行（Richart）", "https://www.findrate.tw/bank/9/#.XM5YhthS8dU",
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
}