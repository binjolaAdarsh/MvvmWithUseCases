package com.master.coupon.domain.model

import android.os.Parcelable
import com.master.coupon.common.Success
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CouponDetails(val couponCode: String? = "",
                         val couponId: Int = -1,
                         val title: String? = "",
                         val subtitle: String? = "",
                         val description: String? = "",
                         val prive: Boolean = false,
                         var tnc: List<String> = emptyList(),
                         var imageUrl: String? = "",
                         var freebies: List<FreeBies>? = null,
                         val couponTncUrl: String? = "",
                         var deltaText: String? = "",
                         val saveRs: String= "",
                         val label: String? = "",
                         val isLocked: Boolean = false,
                         val applied: Boolean = false,
                         val domain: String = ""): Success.ViewState(),Parcelable
@Parcelize
data class FreeBies(
    val name: String? = "",
    val rating: Double? = 0.0,
    val imageUrl: String? = "",
    val description: String? = ""
):Parcelable