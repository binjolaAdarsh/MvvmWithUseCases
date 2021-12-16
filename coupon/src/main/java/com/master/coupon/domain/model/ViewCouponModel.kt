package com.master.coupon.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.master.coupon.common.Success
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class CouponPage :Parcelable{
    MY_COUPON, VIEW_COUPON
}

@Parcelize
enum class OfferType : Parcelable {
    UNIVERSAL, PRODUCT, UNKNOWN
}

sealed class CouponClickEvent {
    class ApplyOrRemoveEvent(val coupon: Coupon) : CouponClickEvent()
    class ApplyEvent(val coupon: Coupon) : CouponClickEvent()
    class RemoveEvent(val coupon: Coupon) : CouponClickEvent()
    class ViewDetailEvent(val coupon: Coupon) : CouponClickEvent()
    class DeltaEvent(val coupon: Coupon) : CouponClickEvent()
}

interface CouponEventTrackListener {
    fun onPageLoad(couponPage: CouponPage, totalCoupons: Int, applicableCoupons: Int, unlockCoupons: Int)
    fun onViewDetail(coupon: Coupon)
    fun onApply(coupon: Coupon, actionPage: ApplyActionPage)
    fun onApplyCongratsClick(coupon: Coupon, couponPage: CouponPage){
    }
    fun onRemove(coupon: Coupon, actionPage: ActionPage, state: ActionState)
    fun onRemoveAndApply(coupon: Coupon, actionPage: ActionPage, state: ActionState)
    fun onLockedCouponDelta(coupon: Coupon)
    fun onTncClicked(coupon: Coupon)
    enum class ApplyActionPage { listPage, detailPage, searchBlock }
    enum class ActionPage { listPage, detailPage }
    enum class ActionState() { INITIATED, COMPLETED }
}


// This class is the collection of HEADER and the DATA of each header for view coupon list
sealed class DataItem {
    abstract val id: Long

    class Header(override val id: Long, val name: String) : DataItem()

    // data model wil be the data containing list item separately  with respect to the HEADER
    data class DataModel(val data: Coupon) : DataItem() {
        override val id: Long
            get() = data.couponId.toLong()
    }


}

fun List<DataItem>.asCoupon(id:Long): Coupon?{
     return this.filterIsInstance<DataItem.DataModel>().find { it.data.couponId == id }?.data
}

// data holder class when we apply coupon
@Keep
data class ApplyDataResponse(var message: String="", var couponCode:String=""): Success.ViewState(){
    companion object{
        fun ApplyDataResponse.asSearchDataResponse(): ApplySearchDataResponse {
            return ApplySearchDataResponse(message = message,couponCode = couponCode)
        }
    }
}
@Keep
data class ApplySearchDataResponse(var message: String="", var couponCode:String=""): Success.ViewState()

@Keep
data class RemoveDataResponse(var message: String="",var couponCode:String=""): Success.ViewState()



@Keep
data class ApplyCouponRequest(
    val couponCode: String = "",
    val deviceType: String,
    val domain: String,
    val appVersion: String
)

@Keep
data class RemoveCouponRequest(val deviceType: String, val domain: String, val appVersion: String)