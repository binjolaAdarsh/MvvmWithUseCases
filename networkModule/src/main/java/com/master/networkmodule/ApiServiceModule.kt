package com.master.networkmodule

import com.master.api.CouponApi
import com.master.fakeDummyData.DummyCouponApiImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton


//@Module
//@InstallIn(ActivityComponent::class)
//abstract  class CouponDataModule{
//    @Binds
//    @FakeCoupon
//    abstract fun bindCouponData(
//        analyticsServiceImpl: DummyCouponApiImpl
//    ): CouponApi
//}



@Module
@InstallIn(ActivityComponent::class)
object ApiServiceModule {


    @Provides
    @ActivityScoped
    @FakeCoupon
    fun provideDummyCoupon(): CouponApi {
        return  DummyCouponApiImpl()
    }

    @Provides
    @ActivityScoped
    fun  provideCouponApiService(retrofit: Retrofit, couponApi: CouponApi):CouponApi{
        return retrofit.create(couponApi::class.java)
    }
    /*
    @Provides
    @Singleton
    fun <T> provideRealCouponApiService(retrofit: Retrofit, couponApi: Class<T>):T{
        return retrofit.create(couponApi)
    }

    @Provides
    @RealCouponApi
    @Singleton
    fun provideRealCouponApiService(retrofit: Retrofit):CouponApi{
        return retrofit.create(CouponApi::class.java)
    }*/


}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FakeCoupon

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RealCoupon

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FakeCouponApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RealCouponApi


