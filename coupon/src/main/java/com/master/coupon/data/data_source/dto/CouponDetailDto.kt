package com.master.coupon.data.data_source.dto

import android.os.Parcelable
import androidx.annotation.Keep
import com.master.coupon.domain.model.CouponDetails
import com.master.coupon.domain.model.FreeBies
import kotlinx.parcelize.Parcelize

@Keep
data class CouponDetailDto(
    val couponCode: String? = "",
    val description: String? = "",
    val title: String? = "",
    val subtitle: String? = "",
    var imageUrl: String? = "",
    var tnc: List<String> = emptyList(),
    val couponId: Int = -1,
    var freebies: List<FreeItemDto>? = null,
    val couponTncUrl: String? = "",
    val prive: Boolean = false)

fun CouponDetailDto.toCouponDetails() : CouponDetails {
    return CouponDetails(
        couponCode = couponCode,
        couponId = couponId,
        title = title,
        subtitle = subtitle,
        description = description,
        prive = prive,
        tnc = tnc,
        imageUrl = imageUrl,
        freebies = freebies?.map {it.toFreeBies()}?: emptyList(),
        couponTncUrl = couponTncUrl )

}

fun FreeItemDto.toFreeBies(): FreeBies {
    return FreeBies(name = name,rating = rating,imageUrl= imageUrl,description = description)
}

@Keep
@Parcelize
data class FreeItemDto(
    val name: String? = "",
    val rating: Double? = 0.0,
    val imageUrl: String? = "",
    val description: String? = "",

    val price: Double? = 0.0,
    val productUrl: String? = "",
    val sku: String? = ""
) : Parcelable

