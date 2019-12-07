package com.vincent.forexbook.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.vincent.forexbook.Constants
import com.vincent.forexbook.R

class BookHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_home)

        val bookId = intent.extras?.getString(Constants.FIELD_ID)
        if (bookId == null) {
            finish()
            return
        }

        Toast.makeText(this, bookId, Toast.LENGTH_SHORT).show()

    }
}
