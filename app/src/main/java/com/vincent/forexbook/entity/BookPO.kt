package com.vincent.forexbook.entity

import java.util.*

data class BookPO(val name: String,
                  val bank: Bank,
                  val currencyType: CurrencyType,
                  var creator: String = "", // TODO: find way to use cloud function to set these data
                  val createdTime: Date) {
}