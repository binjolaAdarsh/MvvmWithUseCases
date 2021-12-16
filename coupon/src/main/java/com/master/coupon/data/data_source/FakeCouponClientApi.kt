package com.master.coupon.data.data_source

import com.master.coupon.data.data_source.dto.*
import com.master.coupon.domain.model.ApplyCouponRequest
import com.master.coupon.domain.model.ApplyDataResponse
import com.master.coupon.domain.model.RemoveCouponRequest
import com.master.coupon.domain.model.RemoveDataResponse
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.delay
import javax.inject.Inject

// client for debugging offline
@ViewModelScoped
class FakeCouponClientApi @Inject constructor(): CouponClientApi {
    override suspend fun getCouponListData(): ViewCouponResponse<AllCouponsDto> {
        val couponProvider = CouponDummyData()
        val appliedCoupon = couponProvider.getAppliedCoupon()
        val unlockedCoupons = couponProvider.getUnLockedCouponList()
        val lockedCoupons = couponProvider.getLockedCouponList()
        delay(2000)
        return ViewCouponResponse(
            success = true,
            data = AllCouponsDto(
                appliedCoupon = appliedCoupon,
                applicableCoupons = unlockedCoupons,
                nonApplicableCoupons = lockedCoupons
            )
        )
    }


    override suspend fun onCouponApply(applyCoupon: ApplyCouponRequest): ViewCouponResponse<ApplyDataResponse> {
        delay(1000)
        return ViewCouponResponse(success = true, data = ApplyDataResponse("couponCode"))
    }

    override suspend fun onCouponRemove(removeCoupon: RemoveCouponRequest): ViewCouponResponse<ApplyDataResponse> {
        delay(1000)
        return ViewCouponResponse(success = true, data = ApplyDataResponse(""))
    }

    override suspend fun onCouponRemove2(removeCoupon: RemoveCouponRequest): ViewCouponResponse<RemoveDataResponse> {
        delay(1000)
        return ViewCouponResponse(success = true, data = RemoveDataResponse(""))
    }

    override suspend fun getViewCouponDetail(couponId: String): ViewCouponResponse<CouponDetailDto> {
        val freeGift = FreeItemDto(
            "name",
            rating = 4.3,
            imageUrl = "",
            description = "description",
            price = 200.0,
            productUrl = "https://image.flaticon.com"
        )
        val data = CouponDetailDto(
            couponCode = "GOAL",
            title = "Extra 10% Off on L’Oreal Paris",
            subtitle = "On purchase of ₹3000 or more",
            freebies = listOf(freeGift.copy()),
            tnc = listOf("first", "second")
        )
        return ViewCouponResponse(success = true, data = data)

    }

    private class CouponDummyData {
        fun getCoupon() = CouponDto(
            couponCode = "LORE10",
            couponId = 1,
            title = "Extra 10% Off on L’Oreal Paris",
            subTitle = "On purchase of ₹3000 or more",
            prive = false,
            infiniteUsage = true,
            offerId = 1,
            description = "this is coupon description",
            imageUrl = "https://image.flaticon.com/icons/png/512/1078/1078454.png",
            toDate = "28 Feb 2021"
        )

        fun getLockedCouponList() = listOf(
            getCoupon().copy(couponCode = "NCOS20", couponId = 2, offerId = 2),
            getCoupon().copy(couponCode = "KUDOS20", couponId = 3, offerId = 3),
            getCoupon().copy(couponCode = "WOOS20", couponId = 4, offerId = 4),
        )

        fun getUnLockedCouponList() = listOf(
            getCoupon().copy(couponCode = "REFS20", couponId = 5, offerId = 5),
            getCoupon().copy(couponCode = "ASOS20", couponId = 6, offerId = 6, prive = true),
            getCoupon().copy(couponCode = "WOES20", couponId = 7, offerId = 7),
        )

        fun getAppliedCoupon(): List<CouponDto> = listOf(getCoupon())

    }
}