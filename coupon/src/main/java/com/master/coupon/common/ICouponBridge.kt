package com.master.coupon.common

import android.os.Parcelable
import com.master.coupon.domain.model.ApplyCouponRequest
import com.master.coupon.domain.model.CouponEventTrackListener
import com.master.coupon.domain.model.CouponPage
import com.master.coupon.domain.model.RemoveCouponRequest
import kotlinx.parcelize.Parcelize


interface ICouponBridge {
    /**
     * @param dependency  is dependency provider interface that provides data required for the coupons module
     */
    fun openCouponScreen(dependency: CouponDependenciesProvidable)
}
class CouponRequestBuilder{
    val couponRequest  = CouponsRequestData()
    fun setDeviceType(deviceType: String): CouponRequestBuilder {
        couponRequest.deviceType = deviceType
        return this
    }
    fun setDomain(domain: String): CouponRequestBuilder {
        couponRequest.domain = domain
        return this
    }
    fun setAppVersion(appVersion: String): CouponRequestBuilder {
        couponRequest.appVersion = appVersion
        return this
    }
    fun setBaseUrl(baseUrl: String): CouponRequestBuilder {
        couponRequest.baseUrl = baseUrl
        return this
    }
    fun setAuthToken(authToken: String): CouponRequestBuilder {
        couponRequest.authToken = authToken
        return this
    }
    fun setGuestToken(guestToken: String): CouponRequestBuilder {
        couponRequest.guestToken = guestToken
        return this
    }

    fun setTitle(title: String): CouponRequestBuilder {
        couponRequest.title = title
        return this
    }

    fun build(): CouponsRequestData {
        return couponRequest
    }
}

@Parcelize
data class CouponsRequestData(
    var deviceType: String="",
    var domain: String="",
    var appVersion: String="",
    var baseUrl: String="",
    var authToken: String="",
    var guestToken: String="",
    var title: String="",
):Parcelable{
    companion object{
        internal fun CouponsRequestData.asApplyCouponRequest(couponCode: String): ApplyCouponRequest {
            return ApplyCouponRequest(couponCode = couponCode,
                deviceType = deviceType,
                domain = domain,
                appVersion = appVersion,)
        }

        internal fun CouponsRequestData.asRemoveCouponRequest(): RemoveCouponRequest {
            return RemoveCouponRequest(deviceType = deviceType,
                domain = domain,
                appVersion = appVersion)
        }
    }
}


interface CouponDependenciesProvidable {
    // to get any type of request
    fun<T> getCouponRequest(): T

    // returns the  events listener to send tracking based on the events triggered
    fun getCouponEvent(): CouponEventTrackListener?

    // returns the Page type from where coupon module was triggered
    fun from(): CouponPage
}


