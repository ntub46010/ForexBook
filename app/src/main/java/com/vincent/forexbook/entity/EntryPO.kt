package com.vincent.forexbook.entity

import java.util.*

data class EntryPO(val bookId: String,
                   val entryType: EntryType,
                   val fcyAmt: Double,
                   val twdAmt: Int,
                   val currencyType: CurrencyType,
                   val transactionDate: Date,
                   var creator: String = "", // TODO: find way to use cloud function to set these data
                   val createdTime: Date) {
}