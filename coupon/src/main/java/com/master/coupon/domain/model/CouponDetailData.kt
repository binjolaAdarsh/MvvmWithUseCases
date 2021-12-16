package com.master.coupon.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CouponDetailData(
    val couponId: Long = 0L, val isLocked: Boolean = false,
    val isApplied: Boolean = false, val deltaText: String = "",
) : Parcelable
