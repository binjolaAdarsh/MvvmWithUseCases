package com.master.coupon.data.data_source

import com.master.coupon.data.data_source.dto.AllCouponsDto
import com.master.coupon.data.data_source.dto.CouponDetailDto
import com.master.coupon.data.data_source.dto.ViewCouponResponse
import com.master.coupon.domain.model.ApplyCouponRequest
import com.master.coupon.domain.model.ApplyDataResponse
import com.master.coupon.domain.model.RemoveCouponRequest
import com.master.coupon.domain.model.RemoveDataResponse
import retrofit2.http.*


interface CouponClientApi {
    @POST("/cartapi/v1/coupon/list")
    suspend fun getCouponListData(): ViewCouponResponse<AllCouponsDto>
    @POST("/cartapi/v1/coupon/apply")
    suspend fun onCouponApply(@Body applyCoupon: ApplyCouponRequest): ViewCouponResponse<ApplyDataResponse>

    @HTTP(method = "DELETE", path = "/cartapi/v1/coupon/remove", hasBody = true)
    suspend fun onCouponRemove(@Body removeCoupon: RemoveCouponRequest): ViewCouponResponse<ApplyDataResponse>
    @HTTP(method = "DELETE", path = "/cartapi/v1/coupon/remove", hasBody = true)
    suspend fun onCouponRemove2(@Body removeCoupon: RemoveCouponRequest): ViewCouponResponse<RemoveDataResponse>
    @GET("/coupon/api/v2/coupon/description")
    suspend fun getViewCouponDetail(@Query("couponId") couponId: String="/*8059145*/"): ViewCouponResponse<CouponDetailDto>
}
