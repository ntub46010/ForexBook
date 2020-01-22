package com.vincent.forexbook.util

import com.google.firebase.firestore.DocumentSnapshot
import com.vincent.forexbook.Constants
import com.vincent.forexbook.entity.*

object EntityConverter {

    fun convertToBookVO(snapshot: DocumentSnapshot): BookVO {
        val map = snapshot.data!!

        return BookVO(
            snapshot.id,
            map[Constants.FIELD_NAME].toString(),
            Bank.valueOf(map[Constants.FIELD_BANK].toString()),
            CurrencyType.valueOf(map[Constants.FIELD_CURRENCY_TYPE].toString()),
            snapshot.getDate(Constants.FIELD_CREATED_TIME)!!)
    }

    fun convertToEntryVO(id: String, entryPO: EntryPO): EntryVO {
        return EntryVO(
            id,
            entryPO.entryType,
            entryPO.fcyAmt,
            entryPO.twdAmt,
            entryPO.currencyType,
            entryPO.transactionDate
        )
    }
}