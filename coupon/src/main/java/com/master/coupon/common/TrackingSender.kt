package com.master.coupon.common


import com.master.coupon.domain.model.Coupon
import com.master.coupon.domain.model.CouponEventTrackListener
import com.master.coupon.domain.model.CouponPage
import com.master.coupon.domain.model.DataItem

interface TrackingSender {
    fun onViewDetailTracking(coupon: Coupon)
    fun onRemoveDetailPageTrackingInitiated(coupon: Coupon)
    fun onRemoveDetailPageTrackingCompleted(coupon: Coupon)

    fun removeListPageTrackingInitiated(coupon: Coupon)
    fun removeListPageTrackingCompleted(coupon: Coupon)

    fun removeAndApplyDetailPageTrackingInitiated(coupon: Coupon)
    fun removeAndApplyDetailPageTrackingCompleted(coupon: Coupon)

    fun removeAndApplyListPageTrackingInitiated(coupon: Coupon)
    fun removeAndApplyListPageTrackingCompleted(coupon: Coupon)

    fun onApplyDetailPageTracking(coupon: Coupon)
    fun onApplyCongratsClickTracking(coupon: Coupon,couponPage: CouponPage)
    fun onTncClicked(coupon: Coupon)
    fun onApplySearchBlock(coupon: Coupon)
    fun onApplyListPageTracking(coupon: Coupon)
    fun onPageLoadTracking(coupons: List<DataItem.DataModel>, couponPage: CouponPage)
    fun onLockedCouponDeltaTracking(coupon: Coupon)
}

class TrackingSenderImpl(val couponTrackingListener: CouponEventTrackListener?): TrackingSender {
    override fun onViewDetailTracking(coupon: Coupon) {
        couponTrackingListener?.onViewDetail(coupon)

    }

    override fun onRemoveDetailPageTrackingInitiated(coupon: Coupon) {
        couponTrackingListener?.onRemove(coupon,
            CouponEventTrackListener.ActionPage.detailPage,
            CouponEventTrackListener.ActionState.INITIATED)
    }

    override fun onRemoveDetailPageTrackingCompleted(coupon: Coupon) {
        couponTrackingListener?.onRemove(coupon,
            CouponEventTrackListener.ActionPage.detailPage,
            CouponEventTrackListener.ActionState.COMPLETED)
    }

    override fun removeAndApplyDetailPageTrackingInitiated(coupon: Coupon) {
        couponTrackingListener?.onRemoveAndApply(coupon,
            CouponEventTrackListener.ActionPage.detailPage,
            CouponEventTrackListener.ActionState.INITIATED)
    }

    override fun removeAndApplyDetailPageTrackingCompleted(coupon: Coupon) {
        couponTrackingListener?.onRemoveAndApply(coupon,
            CouponEventTrackListener.ActionPage.detailPage,
            CouponEventTrackListener.ActionState.COMPLETED)
    }

    override fun removeAndApplyListPageTrackingInitiated(coupon: Coupon) {
        couponTrackingListener?.onRemoveAndApply(coupon,
            CouponEventTrackListener.ActionPage.listPage,
            CouponEventTrackListener.ActionState.INITIATED)
    }

    override fun removeListPageTrackingInitiated(coupon: Coupon) {
        couponTrackingListener?.onRemove(coupon,
            CouponEventTrackListener.ActionPage.listPage,
            CouponEventTrackListener.ActionState.INITIATED)
    }

    override fun removeListPageTrackingCompleted(coupon: Coupon) {
        couponTrackingListener?.onRemove(coupon,
            CouponEventTrackListener.ActionPage.listPage,
            CouponEventTrackListener.ActionState.COMPLETED)
    }

    override fun removeAndApplyListPageTrackingCompleted(coupon: Coupon) {
        couponTrackingListener?.onRemoveAndApply(coupon,
            CouponEventTrackListener.ActionPage.listPage,
            CouponEventTrackListener.ActionState.COMPLETED)
    }

    override fun onApplyDetailPageTracking(coupon: Coupon) {
        couponTrackingListener?.onApply(coupon,
            CouponEventTrackListener.ApplyActionPage.detailPage)
    }

    override fun onApplyCongratsClickTracking(coupon: Coupon,couponPage: CouponPage) {
        couponTrackingListener?.onApplyCongratsClick(coupon, couponPage)
    }

    override fun onTncClicked(coupon: Coupon) {
        couponTrackingListener?.onTncClicked(coupon)
    }

    override fun onApplySearchBlock(coupon: Coupon) {
        couponTrackingListener?.onApply(coupon,
            CouponEventTrackListener.ApplyActionPage.searchBlock)
    }

    override fun onApplyListPageTracking(coupon: Coupon) {
        couponTrackingListener?.onApply(coupon,
            CouponEventTrackListener.ApplyActionPage.listPage)
    }

    override fun onPageLoadTracking(coupons: List<DataItem.DataModel>,couponPage: CouponPage) {
        couponTrackingListener?.onPageLoad(couponPage,
            coupons.size,
            coupons.count { !it.data.isLocked },
            coupons.count { it.data.isLocked })
    }

    override fun onLockedCouponDeltaTracking(coupon: Coupon) {
        couponTrackingListener?.onLockedCouponDelta(coupon)
    }

}