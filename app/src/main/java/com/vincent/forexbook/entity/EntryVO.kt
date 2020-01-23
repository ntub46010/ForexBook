package com.vincent.forexbook.entity

import java.io.Serializable
import java.util.*

data class EntryVO(val id: String,
                   val entryType: EntryType,
                   val fcyAmt: Double,
                   val twdAmt: Int,
                   val currencyType: CurrencyType,
                   val transactionDate: Date)
    : Serializable {

    constructor(id: String,
                entryType: EntryType,
                fcyAmt: Double,
                twdAmt: Long,
                currencyType: CurrencyType,
                transactionDate: Date)
            : this(id, entryType, fcyAmt, twdAmt.toInt(), currencyType, transactionDate)
}