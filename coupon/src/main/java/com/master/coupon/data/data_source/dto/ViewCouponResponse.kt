package com.master.coupon.data.data_source.dto

import androidx.annotation.Keep

// Wrapper for the network response (all response follow this pattern)
// T is the Class type for DATA which changes in response

@Keep
data class ViewCouponResponse<T>(
    val success: Boolean = false,
    val message: String = "",
    val responseProtocol: String? = null,
    val requestProtocol: String? = null,
    val userTrackingId: String = "",
    val statusCode: String = "",
    val data: T? = null
)