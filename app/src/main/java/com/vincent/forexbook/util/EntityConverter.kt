package com.vincent.forexbook.util

import com.google.firebase.firestore.DocumentSnapshot
import com.vincent.forexbook.Constants
import com.vincent.forexbook.entity.*

object EntityConverter {

    fun convertToBookVOs(snapshots: List<DocumentSnapshot>): List<BookVO> {
        return snapshots.asSequence()
            .map { convertToBookVO(it) }
            .toList()
    }

    fun convertToBookVO(snapshot: DocumentSnapshot): BookVO {
        val map = snapshot.data!!

        return BookVO(
            snapshot.id,
            map[Constants.FIELD_NAME].toString(),
            Bank.valueOf(map[Constants.FIELD_BANK].toString()),
            CurrencyType.valueOf(map[Constants.FIELD_CURRENCY_TYPE].toString()),
            snapshot.getDate(Constants.FIELD_CREATED_TIME)!!)
    }

    fun convertToEntryVOs(snapshots: List<DocumentSnapshot>): List<EntryVO> {
        return snapshots.asSequence()
            .map { convertToEntryVO(it) }
            .toList()
    }

    fun convertToEntryVO(snapshot: DocumentSnapshot): EntryVO {
        val map = snapshot.data!!

        return EntryVO(
            snapshot.id,
            EntryType.valueOf(map[Constants.FIELD_ENTRY_TYPE].toString()),
            map[Constants.FIELD_FCY_AMT] as Double,
            map[Constants.FIELD_TWD_AMT] as Long,
            CurrencyType.valueOf(map[Constants.FIELD_CURRENCY_TYPE].toString()),
            snapshot.getDate(Constants.FIELD_TRANSACTION_DATE)!!)
    }

}