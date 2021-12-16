package com.master.coupon.domain.repository

import com.master.coupon.data.data_source.dto.AllCouponsDto
import com.master.coupon.data.data_source.dto.CouponDetailDto
import com.master.coupon.data.data_source.dto.ViewCouponResponse
import com.master.coupon.domain.model.ApplyCouponRequest
import com.master.coupon.domain.model.ApplyDataResponse
import com.master.coupon.domain.model.RemoveCouponRequest
import com.master.coupon.domain.model.RemoveDataResponse


interface CouponRepository {
    suspend fun getCoupons(): ViewCouponResponse<AllCouponsDto>
    suspend fun applyCoupon(applyCoupon: ApplyCouponRequest): ViewCouponResponse<ApplyDataResponse>
    suspend fun removeCoupon(removeCoupon: RemoveCouponRequest): ViewCouponResponse<ApplyDataResponse>
    suspend fun removeCoupon2(removeCoupon: RemoveCouponRequest): ViewCouponResponse<RemoveDataResponse>
    suspend fun getCouponDetails( couponId:String): ViewCouponResponse<CouponDetailDto>
}