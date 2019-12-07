package com.vincent.forexbook.service

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vincent.forexbook.Constants
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.BookVO
import com.vincent.forexbook.entity.BookPO
import com.vincent.forexbook.entity.CurrencyType

object BookService {

    private var collection = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_BOOK)

    fun createBook(bookPO: BookPO, uiCallback: GeneralCallback<BookVO>) {
        bookPO.creator = AuthenticationService.getUserId()!!

        collection
            .add(bookPO)
            .addOnSuccessListener { docRef ->
                docRef.get().addOnSuccessListener { snapshot ->
                    val book = convertToBook(snapshot)
                    // TODO: save in cache
                    uiCallback.onFinish(book)
                }
            }
            .addOnFailureListener {
                uiCallback.onException(it)
            }
    }

    fun loadBooks(uiCallback: GeneralCallback<List<BookVO>>) {
        val userId = AuthenticationService.getUserId()!!

        collection
            .whereEqualTo(Constants.FIELD_CREATOR, userId)
            .orderBy(Constants.FIELD_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val snapshots = querySnapshot.documents
                val books = snapshots.asSequence()
                    .map { convertToBook(it) }
                    .toList()

                uiCallback.onFinish(books)
            }
            .addOnFailureListener {
                uiCallback.onException(it)
            }
    }

    private fun convertToBook(snapshot: DocumentSnapshot): BookVO {
        val map = snapshot.data!!

        return BookVO(
            snapshot.id,
            map[Constants.FIELD_NAME].toString(),
            Bank.valueOf(map[Constants.FIELD_BANK].toString()),
            CurrencyType.valueOf(map[Constants.FIELD_CURRENCY_TYPE].toString()),
            snapshot.getDate(Constants.FIELD_CREATED_TIME)!!)
    }

}