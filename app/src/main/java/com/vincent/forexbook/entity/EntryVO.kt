package com.vincent.forexbook.entity

import java.util.*

data class EntryVO(val id: String,
                   val entryType: EntryType,
                   val fcyAmt: Double,
                   val twdAmt: Int,
                   val currencyType: CurrencyType,
                   val transactionDate: Date) {
}