package com.vincent.forexbook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogout.setOnClickListener {
            AuthenticationService.logout(this)
        }

        FirebaseApp.initializeApp(this)
        AuthenticationService.authenticate(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val user = AuthenticationService.currentLoginUser
                txtName.text = user?.displayName
                Toast.makeText(this, "已登入", Toast.LENGTH_SHORT).show()
            } else {
                val response = IdpResponse.fromResultIntent(data)
                Toast.makeText(this, response?.error?.errorCode.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
