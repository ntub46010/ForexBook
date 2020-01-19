package com.vincent.forexbook.entity

import java.io.Serializable
import java.util.*

data class BookVO(val id: String,
                  val name: String,
                  val bank: Bank,
                  val currencyType: CurrencyType,
                  val createdTime: Date)
    : Serializable {

    var foreignBalance: Double = 0.0
    var taiwanBalance: Int = 0
}