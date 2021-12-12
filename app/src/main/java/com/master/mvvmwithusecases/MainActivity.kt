package com.master.mvvmwithusecases

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.master.api.CouponApi
import com.master.networkmodule.FakeCoupon
import com.master.networkmodule.FakeCouponApi
import com.master.networkmodule.RealCouponApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    @FakeCoupon
    lateinit var couponApi: CouponApi
//    @Inject
//    @RealCouponApi
//    lateinit var realCouponApi: CouponApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.tv)
    lifecycleScope.launch(Dispatchers.Main) {
        textView.text = couponApi.getCoupons()
    }

    }
}