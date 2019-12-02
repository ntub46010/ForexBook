package com.vincent.forexbook.entity

import androidx.fragment.app.Fragment
import com.vincent.forexbook.fragment.BookListFragment
import com.vincent.forexbook.fragment.EmptyFragment
import com.vincent.forexbook.fragment.ExchangeRateListFragment

enum class NavigationPage {
    EXCHANGE_RATE, BOOK, REPORT;

    private var fragment: Fragment? = null

    companion object {
        fun getFragment(page: NavigationPage) =
            when (page) {
                EXCHANGE_RATE -> {
                    if (EXCHANGE_RATE.fragment == null) {
                        EXCHANGE_RATE.fragment = ExchangeRateListFragment()
                    }
                    EXCHANGE_RATE.fragment!!
                }
                BOOK -> {
                    if (BOOK.fragment == null) {
                        BOOK.fragment = BookListFragment()
                    }
                    BOOK.fragment!!
                }
                REPORT -> {
                    if (REPORT.fragment == null) {
                        REPORT.fragment = EmptyFragment()
                    }
                    REPORT.fragment!!
                }
            }
    }
}