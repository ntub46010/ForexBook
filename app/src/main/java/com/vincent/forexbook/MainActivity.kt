package com.vincent.forexbook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var currentFragment: Fragment = EmptyFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        AuthenticationService.authenticate(this)

        setupNavigationBar()
        switchPage(NavigationPage.EXCHANGE_RATE)
    }

    private fun setupNavigationBar() {
        navBar.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navExchangeRate -> {
                    switchPage(NavigationPage.EXCHANGE_RATE)
                }
                R.id.navBook -> {
                    switchPage(NavigationPage.BOOK)
                }
                R.id.navReport -> {
                    switchPage(NavigationPage.REPORT)
                }
            }
            true
        }

        navBar.setOnNavigationItemReselectedListener {}
    }

    private fun switchPage(page: NavigationPage) {
        val fragment = NavigationPage.getFragment(page)
        val transaction = supportFragmentManager.beginTransaction()

        if (fragment.isAdded) {
            transaction.hide(currentFragment).show(fragment)
        } else {
            transaction.hide(currentFragment).add(R.id.pageContainer, fragment)
        }
        transaction.commit()

        currentFragment = fragment
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "已登入", Toast.LENGTH_SHORT).show()
            } else {
                val response = IdpResponse.fromResultIntent(data)
                Toast.makeText(this, response?.error?.errorCode.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
