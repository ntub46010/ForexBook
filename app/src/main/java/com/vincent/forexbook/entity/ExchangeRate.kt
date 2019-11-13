package com.vincent.forexbook.entity

data class ExchangeRate(var currencyType: CurrencyType,
                        var credit: Double,
                        var debit: Double)