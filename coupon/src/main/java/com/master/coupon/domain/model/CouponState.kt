package com.master.coupon.domain.model

import com.master.coupon.common.Failure
import com.master.coupon.common.Success


data class CouponState(
    val message: String = "",
    val coupons: List<DataItem> = emptyList(),
) : Success.ViewState()

sealed class CouponScreenEffect : Success.ViewEffect() {
    object NoCouponsAvailable : CouponScreenEffect()
    object CouponApplied : CouponScreenEffect()
    class CouponRemoved(val couponId:Long,val removedMessage:String) : CouponScreenEffect()
}
sealed class CouponError: Failure.FeatureFailure(){
    class CouponFetchError(val message: String = "") : Failure.FeatureFailure()
    class CouponApplyFailError(val message: String = ""): Failure.FeatureFailure()
    class CouponSearchApplyFailError(val message: String = ""): Failure.FeatureFailure()
    class CouponRemoveFailError(val message: String = ""): Failure.FeatureFailure()
    class CouponDetailError(val message: String = ""): Failure.FeatureFailure()
}


data class CouponUiState(
    val loading: Boolean = false,
    val couponLoaded: Boolean = false,
    val errorOccurred: Boolean = false,
    val emptyList: Boolean = false,
    val showScreen: Boolean = true,
)