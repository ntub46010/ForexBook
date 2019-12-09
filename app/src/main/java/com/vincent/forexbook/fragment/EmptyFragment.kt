package com.vincent.forexbook.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vincent.forexbook.R
import kotlinx.android.synthetic.main.fragment_empty.*
import java.text.SimpleDateFormat
import java.util.*

class EmptyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_empty, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.TAIWAN)
        textView.text = sdf.format(Date(System.currentTimeMillis()))

        Toast.makeText(context!!, "onActivityCreated", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(context!!, "onResume", Toast.LENGTH_SHORT).show()
    }

}