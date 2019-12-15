package com.vincent.forexbook.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

// https://codertw.com/android-%E9%96%8B%E7%99%BC/331678/
class EntryPagerAdapter(titlePageMap: Map<String, Fragment>,
                        fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val titles: List<String>
    private val pages: List<Fragment>

    init {
        titles = mutableListOf()
        pages = mutableListOf()
        for ((title, page) in titlePageMap) {
            titles.add(title)
            pages.add(page)
        }
    }

    override fun getItem(position: Int) = pages[position]

    override fun getCount() = pages.size

    override fun getPageTitle(position: Int) = titles[position]

}