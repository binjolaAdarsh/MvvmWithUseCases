package com.master.coupon.domain.use_case.get_coupons

import com.master.coupon.common.Either
import com.master.coupon.common.Failure
import com.master.coupon.common.Success
import com.master.coupon.data.data_source.dto.ViewCouponResponse
import com.master.coupon.di.FakeRepo
import com.master.coupon.domain.model.ApplyCouponRequest
import com.master.coupon.domain.model.ApplyDataResponse
import com.master.coupon.domain.model.CouponError
import com.master.coupon.domain.model.CouponScreenEffect
import com.master.coupon.domain.repository.CouponRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 *
 * @param repository to call the api
 */
@ViewModelScoped
class ApplyCouponUseCase @Inject constructor(
    @FakeRepo private val repository: CouponRepository,
) {
    companion object{
        private const val SERVER_GENERIC_APPLY_ERROR= "Coupon could not be applied due to a technical issue. Please try again later."
    }
    /**
     * @param   req  is the coupon apply request for api
     * @return Flow<Either<Failure, Success>> -> this will return either the failure or success
     */
    operator fun invoke(
        req: ApplyCouponRequest,
    ): Flow<Either<Failure, Success>> = flow {
        try {
            emit(Either.Right(Success.Loading()))
            val couponsResponse: ViewCouponResponse<ApplyDataResponse> = repository.applyCoupon(req)
            emit(Either.Right(Success.Loading(false)))
            when {
                couponsResponse.success -> {
                    val appliedCouponState = couponsResponse.data?.also {
                        it.message = couponsResponse.message
                        it.couponCode = req.couponCode
                    } ?: ApplyDataResponse("")
                    emit(Either.Right(appliedCouponState)) // emit state
                    emit(Either.Right(CouponScreenEffect.CouponApplied)) // emit the applied effect
                }
                else -> // emit error state
                    emit(Either.Left(CouponError.CouponApplyFailError(couponsResponse.message)))
            }
        } catch (e: HttpException) {
            emit(Either.Right(Success.Loading(false)))
            emit(Either.Left(CouponError.CouponApplyFailError(SERVER_GENERIC_APPLY_ERROR)))
        } catch (e: IOException) {
            emit(Either.Right(Success.Loading(false)))
            emit(Either.Left(Failure.NetworkConnection))
        }
    }.flowOn(Dispatchers.IO)





}