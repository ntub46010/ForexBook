package com.vincent.forexbook.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vincent.forexbook.Constants
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.entity.EntryPO
import com.vincent.forexbook.entity.EntryVO
import com.vincent.forexbook.util.EntityConverter

object EntryService {

    private var collection = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_ENTRY)

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

    fun deleteEntry(id: String, clientCallback: GeneralCallback<Void>) {
        collection
            .document(id)
            .delete()
            .addOnSuccessListener { clientCallback.onFinish(it) }
            .addOnFailureListener { clientCallback.onException(it) }
    }
}