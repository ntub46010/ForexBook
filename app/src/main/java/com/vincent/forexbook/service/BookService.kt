package com.vincent.forexbook.service

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.vincent.forexbook.Constants
import com.vincent.forexbook.GeneralCallback
import com.vincent.forexbook.cache.CacheData
import com.vincent.forexbook.cache.ICacheService
import com.vincent.forexbook.entity.Bank
import com.vincent.forexbook.entity.BookVO
import com.vincent.forexbook.entity.BookPO
import com.vincent.forexbook.entity.CurrencyType
import com.vincent.forexbook.util.EntityConverter

object BookService
    : ICacheService<String, BookVO> {

    override val cacheMap = mutableMapOf<String, CacheData<BookVO>>()

    private val collection = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_BOOK)

    fun createBook(bookPO: BookPO, clientCallback: GeneralCallback<BookVO>) {
        bookPO.creator = AuthenticationService.getUserId()!!

        collection
            .add(bookPO)
            .addOnSuccessListener { docRef ->
                docRef.get().addOnSuccessListener { snapshot ->
                    val book = EntityConverter.convertToBookVO(snapshot)
                    saveCache(book.id, book)

                    clientCallback.onFinish(book)
                }
            }
            .addOnFailureListener { clientCallback.onException(it) }
    }

    fun loadBooks(clientCallback: GeneralCallback<List<BookVO>>) {
        val cache = loadAllCache()
        if (cache.isNotEmpty()) {
            clientCallback.onFinish(cache)
            return
        }

        val userId = AuthenticationService.getUserId()!!

        collection
            .whereEqualTo(Constants.FIELD_CREATOR, userId)
            .orderBy(Constants.FIELD_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val snapshots = querySnapshot.documents
                val books = EntityConverter.convertToBookVOs(snapshots)
                books.forEach { saveCache(it.id, it) }

                clientCallback.onFinish(books)
            }
            .addOnFailureListener { clientCallback.onException(it) }
    }

    fun patchBook(book: BookVO, bookInfo: Map<String, Any>, clientCallback: GeneralCallback<BookVO>) {
        if (book.currencyType == bookInfo[Constants.FIELD_CURRENCY_TYPE]) {
            collection
                .document(book.id)
                .set(bookInfo, SetOptions.merge())
                .addOnSuccessListener {
                    val updatedBook = mergeBookInfo(book, bookInfo)
                    saveCache(updatedBook.id, updatedBook)

                    clientCallback.onFinish(updatedBook)
                }
                .addOnFailureListener { clientCallback.onException(it) }
        } else {
            patchBookAndRelatedEntry(book, bookInfo, clientCallback)
        }
    }

    fun deleteBook(id: String, clientCallback: GeneralCallback<String>) {
        val entryDocumentsLoadedListener = object : GeneralCallback<List<DocumentReference>> {
            override fun onFinish(data: List<DocumentReference>?) {
                val entryDocs = data ?: emptyList()
                deleteBook(id, entryDocs, clientCallback)
            }

            override fun onException(e: Exception) {
                clientCallback.onException(e)
            }
        }

        EntryService.loadEntryDocuments(id, entryDocumentsLoadedListener)
    }

    private fun patchBookAndRelatedEntry(book: BookVO, bookInfo: Map<String, Any>,
                                         clientCallback: GeneralCallback<BookVO>) {
        val entryDocumentsLoadedCallback = object : GeneralCallback<List<DocumentReference>> {
            override fun onFinish(data: List<DocumentReference>?) {
                val entryDocs = data ?: emptyList()
                patchBookAndRelatedEntry(book, bookInfo, entryDocs, clientCallback)
            }

            override fun onException(e: Exception) {
                clientCallback.onException(e)
            }
        }

        EntryService.loadEntryDocuments(book.id, entryDocumentsLoadedCallback)
    }

    private fun patchBookAndRelatedEntry(book: BookVO, bookInfo: Map<String, Any>,
                                         entryDocuments: List<DocumentReference>,
                                         clientCallback: GeneralCallback<BookVO>) {
        val bookDoc = collection.document(book.id)
        val currencyType = bookInfo[Constants.FIELD_CURRENCY_TYPE] as CurrencyType

        val writeBatch = FirebaseFirestore.getInstance().batch()
        bookInfo.forEach { (field, value) -> writeBatch.update(bookDoc, field, value) }
        entryDocuments.forEach { writeBatch.update(it, Constants.FIELD_CURRENCY_TYPE, currencyType) }

        writeBatch
            .commit()
            .addOnSuccessListener {
                val updatedBook = mergeBookInfo(book, bookInfo)
                saveCache(updatedBook.id, updatedBook)

                clientCallback.onFinish(updatedBook)
            }
            .addOnFailureListener { clientCallback.onException(it) }
    }

    private fun deleteBook(bookId: String, entryDocuments: List<DocumentReference>,
                           clientCallback: GeneralCallback<String>) {
        val bookDoc = collection.document(bookId)
        val writeBatch = FirebaseFirestore.getInstance().batch()
        writeBatch.delete(bookDoc)
        entryDocuments.forEach { writeBatch.delete(it) }

        writeBatch
            .commit()
            .addOnSuccessListener {
                removeCache(bookId)
                clientCallback.onFinish(bookId)
            }
            .addOnFailureListener { clientCallback.onException(it) }
    }

    private fun mergeBookInfo(book: BookVO, bookInfo: Map<String, Any>): BookVO {
        return book.copy(
            name = bookInfo[Constants.FIELD_NAME].toString(),
            bank = bookInfo[Constants.FIELD_BANK] as Bank,
            currencyType = bookInfo[Constants.FIELD_CURRENCY_TYPE] as CurrencyType)
    }
}