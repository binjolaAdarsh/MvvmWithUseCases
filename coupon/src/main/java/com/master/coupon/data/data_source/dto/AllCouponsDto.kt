package com.master.coupon.data.data_source.dto

import androidx.annotation.Keep
import com.master.coupon.domain.model.Coupon
import java.text.SimpleDateFormat
import java.util.*

@Keep
data class AllCouponsDto(
    val appliedCoupon: List<CouponDto>? = emptyList(),
    val applicableCoupons: List<CouponDto>?= emptyList(),
    val nonApplicableCoupons: List<CouponDto>?= emptyList()
)

@Keep
data class CouponDto(
    val couponCode: String = "",
    val description: String? = "",
    val couponId: Long = -1,
    val fromDate: String? = "",
    val toDate: String? = "",
    val usesPerCustomer: Long = -1,
    val timesUsed: Int? = -1,
    val infiniteUsage: Boolean? = false,
    val offerId: Int? = -1,
    var title: String? = "",
    val subTitle: String? = "",
    val imageUrl: String? = "",
    val offerType: String? = "",
    var label: String? = "",
    var headingText: String? = "",
    var subText: String? = "",
    val couponType: Int? = -1,
    var discount: Double? = 0.0,
    val deltaCouponDTO: DeltaDTO? = DeltaDTO(),
    val prive: Boolean? = false
)

@Keep
data class DeltaDTO(val deltaText: String? = "")

fun CouponDto.toCoupon(applied:Boolean = false): Coupon {
    return Coupon(
        couponCode = couponCode,
        description = description,
        couponId = couponId,
        toDate = toDate,
        offerId = offerId,
        title = title,
        subTitle = subTitle,
        imageUrl = imageUrl,
        offerType = offerType,
        label = label,
        headingText = headingText,
        subText = subText,
        discount = discount,
        deltaText = deltaCouponDTO?.deltaText ?: "",
        prive = prive,
        applied = applied,
        saveRs = if (!label.isNullOrEmpty()) label!! else "",
        expires = if (toDate.isNullOrEmpty()) "" else {
            var spf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val newDate: Date? = spf.parse(toDate)
                spf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                "Expires on ${spf.format(newDate!!)}"
            } catch (e: Exception) {
                ""
            }
        }
    )
}
