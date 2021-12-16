package com.master.coupon.domain.use_case.get_coupons

import com.master.coupon.domain.model.ApplyCouponRequest
import com.master.coupon.domain.model.RemoveCouponRequest


sealed class CouponEvent {
    object LoadCouponEvent : CouponEvent()
    class ApplyCouponEvent(val applyRequest: ApplyCouponRequest) : CouponEvent()
    class ApplySearchCouponEvent(val applyRequest: ApplyCouponRequest) : CouponEvent()
    class RemoveCouponEvent(val couponId:Long,val removeRequest: RemoveCouponRequest) : CouponEvent()
}

sealed class CouponDetailEvent{
    class LoadCouponDetailEvent(val couponId:String) : CouponDetailEvent()
}
