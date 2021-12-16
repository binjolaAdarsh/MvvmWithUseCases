package com.master.coupon.domain.use_case.get_coupon_details


import com.master.coupon.common.CouponConstants
import com.master.coupon.common.Either
import com.master.coupon.common.Failure
import com.master.coupon.common.Success
import com.master.coupon.data.data_source.dto.CouponDetailDto
import com.master.coupon.data.data_source.dto.ViewCouponResponse
import com.master.coupon.data.data_source.dto.toCouponDetails
import com.master.coupon.di.FakeRepo
import com.master.coupon.domain.model.CouponError
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
class GetCouponDetailUseCase @Inject constructor(@FakeRepo private val repository: CouponRepository) {

    operator fun invoke(couponId:String): Flow<Either<Failure, Success>> = flow {
        try {
            emit(Either.Right(Success.Loading()))
            val couponResponse: ViewCouponResponse<CouponDetailDto> =
                repository.getCouponDetails(couponId= couponId)
            if (couponResponse.success) {
                val coupon = couponResponse.data ?: CouponDetailDto()
                emit(Either.Right(coupon.toCouponDetails()))
            } else {
                // if we get success false in response the throw error with respected message
                emit(Either.Left(CouponError.CouponDetailError(couponResponse.message)))
            }
            emit(Either.Right(Success.Loading(false)))
        } catch (e: HttpException) {  // if it was not successful
            emit(Either.Right(Success.Loading(false)))
//            emit(Either.Left(Failure.GenericError(Exception(CouponConstants.SERVER_ERROR_MESSAGE))))
            emit(Either.Left(CouponError.CouponDetailError(CouponConstants.SERVER_ERROR_MESSAGE)))
        } catch (e: IOException) { // if our  api cant talk to remote api(usually internet error )
            emit(Either.Right(Success.Loading(false)))
            emit(Either.Left(Failure.NetworkConnection))
        }
    }.flowOn(Dispatchers.IO)

}