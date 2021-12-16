package com.master.coupon.domain.use_case.get_coupons


import com.master.coupon.common.Either
import com.master.coupon.common.Failure
import com.master.coupon.common.Success
import com.master.coupon.data.data_source.dto.ViewCouponResponse
import com.master.coupon.di.FakeRepo
import com.master.coupon.domain.model.CouponError
import com.master.coupon.domain.model.CouponScreenEffect
import com.master.coupon.domain.model.RemoveCouponRequest
import com.master.coupon.domain.model.RemoveDataResponse
import com.master.coupon.domain.repository.CouponRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ViewModelScoped
class RemoveCouponUseCase @Inject constructor(
    @FakeRepo private val repository: CouponRepository,
) {

    companion object{
        private const val SERVER_GENERIC_REMOVE_ERROR = "Coupon could not be removed due to a technical issue. Please try again later."
    }

    operator fun invoke(couponId:Long,
                        req: RemoveCouponRequest,
    ): Flow<Either<Failure, Success>> = flow {
        try {
            emit(Either.Right(Success.Loading()))
            val couponsResponse: ViewCouponResponse<RemoveDataResponse> = repository.removeCoupon2(req)
            emit(Either.Right(Success.Loading(false)))
            if (couponsResponse.success) {
                val removedCouponState =
                    couponsResponse.data?.also {
                        it.message = couponsResponse.message
                    } ?: RemoveDataResponse("")
                emit(Either.Right(removedCouponState))
                emit(Either.Right(CouponScreenEffect.CouponRemoved(couponId = couponId,couponsResponse.message)))
            } else {
                emit(Either.Left(CouponError.CouponRemoveFailError(couponsResponse.message)))
            }
        } catch (e: HttpException) {
            emit(Either.Right(Success.Loading(false)))
            emit(Either.Left(CouponError.CouponRemoveFailError(SERVER_GENERIC_REMOVE_ERROR)))
        } catch (e: IOException) {
            emit(Either.Right(Success.Loading(false)))
            emit(Either.Left(Failure.NetworkConnection))
        }
    }.flowOn(Dispatchers.IO)
}