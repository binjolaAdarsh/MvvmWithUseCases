package com.master.coupon.data.repository

import com.master.coupon.data.data_source.CouponClientApi
import com.master.coupon.data.data_source.dto.AllCouponsDto
import com.master.coupon.data.data_source.dto.CouponDetailDto
import com.master.coupon.data.data_source.dto.ViewCouponResponse
import com.master.coupon.domain.model.ApplyCouponRequest
import com.master.coupon.domain.model.ApplyDataResponse
import com.master.coupon.domain.model.RemoveCouponRequest
import com.master.coupon.domain.model.RemoveDataResponse
import com.master.coupon.domain.repository.CouponRepository

class CouponRepositoryImpl(private val couponClientApi: CouponClientApi) : CouponRepository {
    override suspend fun getCoupons(): ViewCouponResponse<AllCouponsDto> {
        return couponClientApi.getCouponListData()
    }

    override suspend fun applyCoupon(applyCoupon: ApplyCouponRequest): ViewCouponResponse<ApplyDataResponse> {
        return couponClientApi.onCouponApply(applyCoupon)
    }

    override suspend fun removeCoupon(removeCoupon: RemoveCouponRequest): ViewCouponResponse<ApplyDataResponse> {
        return couponClientApi.onCouponRemove(removeCoupon)
    }

    override suspend fun removeCoupon2(removeCoupon: RemoveCouponRequest): ViewCouponResponse<RemoveDataResponse> {
        return couponClientApi.onCouponRemove2(removeCoupon)
    }

    override suspend fun getCouponDetails(couponId: String): ViewCouponResponse<CouponDetailDto> {
        return couponClientApi.getViewCouponDetail(couponId)
    }
}
