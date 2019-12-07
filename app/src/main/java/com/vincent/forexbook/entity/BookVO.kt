package com.vincent.forexbook.entity

import java.util.*

data class BookVO(val id: String,
                  val name: String,
                  val bank: Bank,
                  val currencyType: CurrencyType,
                  val createdTime: Date) {
}