package com.vincent.forexbook.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vincent.forexbook.Constants
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.entity.BookVO
import com.vincent.forexbook.entity.BookPO
import com.vincent.forexbook.util.EntityConverter

object BookService {

    private var collection = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_BOOK)

    fun createBook(bookPO: BookPO, clientCallback: GeneralCallback<BookVO>) {
        bookPO.creator = AuthenticationService.getUserId()!!

        collection
            .add(bookPO)
            .addOnSuccessListener { docRef ->
                docRef.get().addOnSuccessListener { snapshot ->
                    val book = EntityConverter.convertToBookVO(snapshot)
                    // TODO: save in cache
                    clientCallback.onFinish(book)
                }
            }
            .addOnFailureListener { clientCallback.onException(it) }
    }

    fun loadBooks(clientCallback: GeneralCallback<List<BookVO>>) {
        val userId = AuthenticationService.getUserId()!!

        collection
            .whereEqualTo(Constants.FIELD_CREATOR, userId)
            .orderBy(Constants.FIELD_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val snapshots = querySnapshot.documents
                val books = EntityConverter.convertToBookVOs(snapshots)

                clientCallback.onFinish(books)
            }
            .addOnFailureListener { clientCallback.onException(it) }
    }
}