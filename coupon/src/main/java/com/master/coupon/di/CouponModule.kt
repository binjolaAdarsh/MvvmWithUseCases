package com.master.coupon.di

import com.master.coupon.data.data_source.CouponClientApi
import com.master.coupon.data.data_source.FakeCouponClientApi
import com.master.coupon.data.repository.CouponRepositoryImpl
import com.master.coupon.data.repository.EmptyRepoImpl
import com.master.coupon.domain.repository.CouponRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FakeCouponClient
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RealCouponClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EmptyRepo
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FakeRepo
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RealRepo

@Module
@InstallIn(ViewModelComponent::class)
object CouponModule {

    @Provides
    @ViewModelScoped
    @RealCouponClient
    fun provodeCouponClient(retrofit: Retrofit): CouponClientApi{
       return retrofit.create(CouponClientApi::class.java)
    }
    @Provides
    @ViewModelScoped
    @FakeCouponClient
    fun provodeFakeCouponClient(): CouponClientApi{
       return FakeCouponClientApi()
    }

    @Provides
    @ViewModelScoped
    @EmptyRepo
    fun provideEmptyCouponRepository():CouponRepository{
        return EmptyRepoImpl()
    }

    @Provides
    @ViewModelScoped
    @FakeRepo
    fun provideCouponRepository(@FakeCouponClient couponApi:CouponClientApi):CouponRepository{
        return CouponRepositoryImpl(couponApi)
    }

    @Provides
    @ViewModelScoped
    @RealRepo
    fun provideRealCouponRepository(@RealCouponClient couponApi:CouponClientApi):CouponRepository{
        return CouponRepositoryImpl(couponApi)
    }


}



