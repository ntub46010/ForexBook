package com.vincent.forexbook.service

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.vincent.forexbook.Constants
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.entity.EntryPO
import com.vincent.forexbook.entity.EntryType
import com.vincent.forexbook.entity.EntryVO
import com.vincent.forexbook.util.EntityConverter
import java.util.*

object EntryService {

    private val collection = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_ENTRY)

    fun createEntry(entryPO: EntryPO, clientCallback: GeneralCallback<EntryVO>) {
        entryPO.creator = AuthenticationService.getUserId()!!

        collection
            .add(entryPO)
            .addOnSuccessListener { docRef ->
                docRef.get().addOnSuccessListener { snapshot ->
                    val entry = EntityConverter.convertToEntryVO(snapshot)
                    clientCallback.onFinish(entry)
                }
            }
            .addOnFailureListener { clientCallback.onException(it) }
    }

    fun loadEntries(bookId: String, clientCallback: GeneralCallback<List<EntryVO>>) {
        collection
            .whereEqualTo(Constants.FIELD_BOOK_ID, bookId)
            .orderBy(Constants.FIELD_TRANSACTION_DATE, Query.Direction.DESCENDING)
            .orderBy(Constants.FIELD_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val snapshots = it.documents
                val entries = EntityConverter.convertToEntryVOs(snapshots)

                clientCallback.onFinish(entries)
            }
            .addOnFailureListener { clientCallback.onException(it) }
    }

    fun loadEntryDocuments(bookId: String, clientCallback: GeneralCallback<List<DocumentReference>>) {
        collection
            .whereEqualTo(Constants.FIELD_BOOK_ID, bookId)
            .get()
            .addOnSuccessListener {
                val snapshots = it.documents
                val documentReferences = EntityConverter.toEntryDocuments(snapshots, collection)

                clientCallback.onFinish(documentReferences)
            }
            .addOnFailureListener { clientCallback.onException(it) }
    }

    fun patchEntry(entry: EntryVO, entryInfo: Map<String, Any>, clientCallback: GeneralCallback<EntryVO>) {
        collection
            .document(entry.id)
            .set(entryInfo, SetOptions.merge())
            .addOnSuccessListener {
                val updatedEntry = mergeEntryInfo(entry, entryInfo)
                clientCallback.onFinish(updatedEntry)
            }
            .addOnFailureListener { clientCallback.onException(it) }
    }

    fun deleteEntry(id: String, clientCallback: GeneralCallback<Void>) {
        collection
            .document(id)
            .delete()
            .addOnSuccessListener { clientCallback.onFinish(it) }
            .addOnFailureListener { clientCallback.onException(it) }
    }

    private fun mergeEntryInfo(entry: EntryVO, entryInfo: Map<String, Any>): EntryVO {
        return entry.copy(
            entryType = entryInfo[Constants.FIELD_ENTRY_TYPE] as EntryType,
            transactionDate = entryInfo[Constants.FIELD_TRANSACTION_DATE] as Date,
            fcyAmt = entryInfo[Constants.FIELD_FCY_AMT] as Double,
            twdAmt = entryInfo[Constants.FIELD_TWD_AMT] as Int)
    }
}