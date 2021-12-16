package com.master.coupon.data.repository

import com.master.coupon.data.data_source.dto.AllCouponsDto
import com.master.coupon.data.data_source.dto.CouponDetailDto
import com.master.coupon.data.data_source.dto.ViewCouponResponse
import com.master.coupon.di.EmptyRepo
import com.master.coupon.domain.model.ApplyCouponRequest
import com.master.coupon.domain.model.ApplyDataResponse
import com.master.coupon.domain.model.RemoveCouponRequest
import com.master.coupon.domain.model.RemoveDataResponse
import com.master.coupon.domain.repository.CouponRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
@EmptyRepo
class EmptyRepoImpl @Inject constructor(): CouponRepository {
    override suspend fun getCoupons(): ViewCouponResponse<AllCouponsDto> {
        return ViewCouponResponse()
    }

    override suspend fun applyCoupon(applyCoupon: ApplyCouponRequest): ViewCouponResponse<ApplyDataResponse> {
        return ViewCouponResponse()
    }

    override suspend fun removeCoupon(removeCoupon: RemoveCouponRequest): ViewCouponResponse<ApplyDataResponse> {
        return ViewCouponResponse()

    }

    override suspend fun removeCoupon2(removeCoupon: RemoveCouponRequest): ViewCouponResponse<RemoveDataResponse> {
        return ViewCouponResponse()

    }

    override suspend fun getCouponDetails(couponId: String): ViewCouponResponse<CouponDetailDto> {
        return ViewCouponResponse()

    }
}