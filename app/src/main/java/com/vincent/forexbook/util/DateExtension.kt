package com.vincent.forexbook.util

import java.util.*
import kotlin.math.ceil

fun Date.nextClock(): Date {
    val oneHourTimeMill = 3600000
    val nowTimeMill = this.time

    val nextClockTimeMill = if (nowTimeMill % oneHourTimeMill == 0L) {
        nowTimeMill + oneHourTimeMill
    } else {
        ceil(nowTimeMill / oneHourTimeMill.toDouble()).toLong() * oneHourTimeMill
    }

    return Date(nextClockTimeMill)
}

fun Date.addMinutes(minute: Int) = Date(this.time + minute * 60000)

fun Date.minus(date: Date): Long = this.time - date.time