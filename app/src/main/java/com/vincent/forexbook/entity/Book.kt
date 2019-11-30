package com.vincent.forexbook.entity

import java.util.*

data class Book(val id: String,
                var name: String,
                val bank: Bank,
                val currencyType: CurrencyType,
                val createdTime: Date) {
}