package com.vincent.forexbook.service

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.BookVO
import com.vincent.forexbook.entity.BookPO
import com.vincent.forexbook.entity.CurrencyType

object BookService {

    private var collection = FirebaseFirestore.getInstance().collection("book")

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

    private fun convertToBook(snapshot: DocumentSnapshot): BookVO {
        val map = snapshot.data!!

        return BookVO(
            snapshot.id,
            map["name"].toString(),
            Bank.valueOf(map["bank"].toString()),
            CurrencyType.valueOf(map["currencyType"].toString()),
            snapshot.getDate("createdTime")!!)
    }

}