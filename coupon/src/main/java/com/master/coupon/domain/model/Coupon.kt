package com.master.coupon.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coupon (
    val couponCode: String = "",
    val description: String? = "",
    val couponId: Long = -1,
    val toDate: String? = "",
    val offerId: Int? = -1,
    var title: String? = "",
    val subTitle: String? = "",
    val imageUrl: String? = "",
    val offerType: String? = "",
    var label: String? = "",
    var headingText: String? = "",
    var subText: String? = "",
    var discount: Double? = 0.0,
    val deltaText: String = "",
    val prive: Boolean? = false,
    var isLocked: Boolean = false,
    var applied: Boolean = false,
    var saveRs:String="", // used in UI for showing the label in coupon item
    val expires: String="", // used in UI for showing the expiry date of coupon based on respective date format

): Parcelable{
    companion object{
        internal fun Coupon.asCouponDetailData():CouponDetailData{
            return CouponDetailData(couponId = couponId,isLocked = isLocked,isApplied = applied,deltaText = deltaText)
        }
    }
}