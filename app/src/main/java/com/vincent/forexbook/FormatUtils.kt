package com.vincent.forexbook

import java.util.*

object FormatUtils {

    fun formatDecimalPlaces(number: Double, place: Int): String =
        java.lang.String.format(Locale.US, "%.${place}f", number)

}