package com.master.mvvmwithusecases

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.master.coupon.presentation.coupons.ViewCouponActivity
import dagger.hilt.android.AndroidEntryPoint

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.tv)
        textView.setOnClickListener {
            startActivity(Intent(this, ViewCouponActivity::class.java))
        }

    }
}
