package com.vincent.forexbook.service

import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.entity.CurrencyType
import com.vincent.forexbook.entity.EntryType
import com.vincent.forexbook.entity.EntryVO
import java.util.*

object EntryService {

    fun loadEntries(bookId: String, uiCallback: GeneralCallback<List<EntryVO>>) {
        uiCallback.onFinish(emptyList())
    }

    fun loadMockEntries(currencyType: CurrencyType, uiCallback: GeneralCallback<List<EntryVO>>) {
        val entries =
            if (currencyType === CurrencyType.CHF) getMockCHFEntries()
            else getMockUSDEntries()

        uiCallback.onFinish(entries)
    }

    private fun getMockCHFEntries() =
        listOf(
            EntryVO("1", EntryType.CREDIT, 1000.0, 31271, CurrencyType.CHF, Date()),
            EntryVO("2", EntryType.CREDIT, 500.0, 15580, CurrencyType.CHF, Date()),
            EntryVO("3", EntryType.DEBIT, -900.0, -27645, CurrencyType.CHF, Date())
        )

    private fun getMockUSDEntries() =
        listOf(
            EntryVO("1", EntryType.CREDIT, 3000.0, 92531, CurrencyType.USD, Date()),
            EntryVO("2", EntryType.CREDIT, 27.5, 0, CurrencyType.USD, Date()),
            EntryVO("3", EntryType.DEBIT, -1027.5, -31697, CurrencyType.USD, Date()),
            EntryVO("4", EntryType.DEBIT, -1000.0, -30840, CurrencyType.USD, Date()),
            EntryVO("5", EntryType.CREDIT, 0.13, 0, CurrencyType.USD, Date()),
            EntryVO("6", EntryType.DEBIT, -1000.13, -30780, CurrencyType.USD, Date()),
            EntryVO("7", EntryType.CREDIT, 514.0, 16637, CurrencyType.USD, Date()),
            EntryVO("8", EntryType.CREDIT, 886.0, 27977, CurrencyType.USD, Date()),
            EntryVO("9", EntryType.CREDIT, 0.39, 0, CurrencyType.USD, Date()),
            EntryVO("10", EntryType.DEBIT, -1000.39, -31050, CurrencyType.USD, Date()),
            EntryVO("11", EntryType.DEBIT, -400.0, -12377, CurrencyType.USD, Date()),
            EntryVO("12", EntryType.CREDIT, 3000.0, 91662, CurrencyType.USD, Date()),
            EntryVO("13", EntryType.CREDIT, 28.42, 0, CurrencyType.USD, Date()),
            EntryVO("14", EntryType.CREDIT, 0.09, 0, CurrencyType.USD, Date())
        )

}