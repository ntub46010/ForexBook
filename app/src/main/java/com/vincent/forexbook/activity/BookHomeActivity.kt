package com.vincent.forexbook.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.vincent.forexbook.Constants
import com.vincent.forexbook.R
import com.vincent.forexbook.adapter.EntryPagerAdapter
import com.vincent.forexbook.fragment.EmptyFragment
import kotlinx.android.synthetic.main.activity_book_home.*

class BookHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_home)

        val bookId = intent.extras?.getString(Constants.FIELD_ID)
        if (bookId == null) {
            finish()
            return
        }

        initEntryViewPager()
        Toast.makeText(this, bookId, Toast.LENGTH_SHORT).show()
    }

    private fun initEntryViewPager() {
        // TODO: maybe can change to use NavigationPage
        val titlePageMap = mapOf(
            getString(R.string.nav_title_credit) to EmptyFragment(),
            getString(R.string.nav_title_debit) to EmptyFragment(),
            getString(R.string.nav_title_balance) to EmptyFragment())

        val adapter = EntryPagerAdapter(titlePageMap, supportFragmentManager)
        pagerEntry.adapter = adapter
        pagerEntry.offscreenPageLimit = adapter.count - 1
        pagerEntry.currentItem = 0

        tab.setupWithViewPager(pagerEntry)
    }
}
