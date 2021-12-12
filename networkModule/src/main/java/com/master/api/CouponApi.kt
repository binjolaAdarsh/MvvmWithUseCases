package com.master.api

import retrofit2.http.GET

interface CouponApi {
    @GET("fact")
    suspend fun getCoupons():String
}