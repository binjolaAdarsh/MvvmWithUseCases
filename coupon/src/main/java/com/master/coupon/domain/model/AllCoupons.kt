package com.master.coupon.domain.model

import android.os.Parcelable
import com.master.coupon.domain.model.Coupon
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllCoupons (val appliedCoupon: List<Coupon>? = emptyList(),
                       val applicableCoupons: List<Coupon>?= emptyList(),
                       val nonApplicableCoupons: List<Coupon>?= emptyList()):Parcelable
