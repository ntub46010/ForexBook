package com.vincent.forexbook

import androidx.fragment.app.Fragment

enum class NavigationPage {
    EXCHANGE_RATE, BOOK, REPORT;

    private var fragment: Fragment? = null

    companion object {
        fun getFragment(page: NavigationPage) =
            when (page) {
                EXCHANGE_RATE -> {
                    if (EXCHANGE_RATE.fragment == null) {
                        EXCHANGE_RATE.fragment = EmptyFragment()
                    }
                    EXCHANGE_RATE.fragment!!
                }
                BOOK -> {
                    if (BOOK.fragment == null) {
                        BOOK.fragment = EmptyFragment()
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