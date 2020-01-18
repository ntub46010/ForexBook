package com.vincent.forexbook.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {

    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
    private val moneyFormat = NumberFormat.getNumberInstance(Locale.US)

    fun formatDecimalPlaces(number: Double, place: Int): String =
        java.lang.String.format(Locale.US, "%.${place}f", number)

    fun formatDate(date: Date?): String = dateFormat.format(date)

    fun formatMoney(amount: Int): String = moneyFormat.format(amount)

    fun formatMoney(amount: Double): String = moneyFormat.format(amount)

}