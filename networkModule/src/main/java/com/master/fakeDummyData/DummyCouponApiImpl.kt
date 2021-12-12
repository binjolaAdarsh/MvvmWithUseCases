package com.master.fakeDummyData

import com.master.api.CouponApi
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class DummyCouponApiImpl @Inject constructor() :CouponApi {
     override suspend fun getCoupons(): String {
        return "dummy fake coupon"
    }
}