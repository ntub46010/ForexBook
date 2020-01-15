package com.vincent.forexbook.service

import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.entity.CurrencyType
import com.vincent.forexbook.entity.EntryType
import com.vincent.forexbook.entity.EntryVO
import java.util.*

object EntryService {

    fun loadEntries(bookId: String, uiCallback: GeneralCallback<List<EntryVO>>) {
        val entries = getMockEntries()
        uiCallback.onFinish(entries)
    }

    private fun getMockEntries() =
        listOf(
            EntryVO("1", EntryType.CREDIT, 3000.0, 92531, CurrencyType.USD, Date()),
            EntryVO("2", EntryType.CREDIT, 27.5, 0, CurrencyType.USD, Date()),
            EntryVO("3", EntryType.DEBIT, -1027.5, -31697, CurrencyType.USD, Date()),
            EntryVO("4", EntryType.DEBIT, -1000.0, -31840, CurrencyType.USD, Date()),
            EntryVO("5", EntryType.CREDIT, 0.13, 0, CurrencyType.USD, Date()),
            EntryVO("6", EntryType.DEBIT, -1000.13, -30780, CurrencyType.USD, Date())
        )
}