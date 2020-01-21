package com.vincent.forexbook.util

import com.vincent.forexbook.entity.EntryPO
import com.vincent.forexbook.entity.EntryVO

object EntityConverter {

    fun convertToEntryVO(id: String, entryPO: EntryPO): EntryVO =
        EntryVO(
            id,
            entryPO.entryType,
            entryPO.fcyAmt,
            entryPO.twdAmt,
            entryPO.currencyType,
            entryPO.transactionDate)
}