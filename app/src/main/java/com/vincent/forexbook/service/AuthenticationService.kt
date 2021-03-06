package com.vincent.forexbook.service

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vincent.forexbook.Constants
import com.vincent.forexbook.R

object AuthenticationService {

    var currentLoginUser: FirebaseUser? = null

    private val authProvider = listOf(
        AuthUI.IdpConfig.FacebookBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    fun authenticate(activity: Activity) {
        val authStateListener = FirebaseAuth.AuthStateListener {
            currentLoginUser = it.currentUser
            if (currentLoginUser == null) {
                val intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(authProvider)
                    .setLogo(R.drawable.logo)
                    .setTheme(R.style.LoginTheme)
                    .setAlwaysShowSignInMethodScreen(true)
                    .setIsSmartLockEnabled(false)
                    .build()

                activity.startActivityForResult(intent, Constants.RC_SIGN_IN)
            }
        }

        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    fun logout(context: Context) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnSuccessListener {
                Toast.makeText(context, "已登出", Toast.LENGTH_SHORT).show()
            }
    }

    fun getUserId() = currentLoginUser?.uid
}