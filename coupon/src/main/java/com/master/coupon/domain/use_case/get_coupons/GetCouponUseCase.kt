package com.master.coupon.domain.use_case.get_coupons


import com.master.coupon.common.CouponConstants
import com.master.coupon.common.CouponConstants.APPLICABLE_COUPONS
import com.master.coupon.common.CouponConstants.UNLOCK_COUPONS
import com.master.coupon.common.Either
import com.master.coupon.common.Failure
import com.master.coupon.common.Success
import com.master.coupon.data.data_source.dto.AllCouponsDto
import com.master.coupon.data.data_source.dto.ViewCouponResponse
import com.master.coupon.data.data_source.dto.toCoupon
import com.master.coupon.di.FakeRepo
import com.master.coupon.domain.model.CouponError
import com.master.coupon.domain.model.CouponScreenEffect
import com.master.coupon.domain.model.CouponState
import com.master.coupon.domain.model.DataItem
import com.master.coupon.domain.repository.CouponRepository
import com.master.coupon.domain.use_case.get_coupon_details.GetCouponDetailUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * this class is the group of all use cases
 * this class is passed to the required viewModel
 */
@ViewModelScoped
data class CouponUseCases @Inject constructor(
    val getCouponUseCase: GetCouponUseCase,
    val applyCouponUseCase: ApplyCouponUseCase,
    val appliedStateCoupons: GetAppliedStateCoupons,
    val removeStateCoupons: GetRemovedCoupons ,
    val applySearchCouponUseCase: ApplySearchCouponUseCase,
    val removeCouponUseCase: RemoveCouponUseCase,
)
@ViewModelScoped
data class CouponDetailUseCase @Inject constructor(val getCouponDetailUseCase: GetCouponDetailUseCase )

@ViewModelScoped
class GetCouponUseCase @Inject constructor(
    @FakeRepo private val repository: CouponRepository,
) {

    operator fun invoke(): Flow<Either<Failure, Success>> = flow {
        try {
            emit(Either.Right(Success.Loading()))
            val couponsResponse: ViewCouponResponse<AllCouponsDto> =
                repository.getCoupons()
            if (couponsResponse.success) {
                val allCoupons = couponsResponse.data ?: AllCouponsDto()
                createListAsync(allCoupons).collect {
//                    emit(Resource.Success(it))
                    if (it.isNotEmpty())
                        emit(Either.Right(CouponState(coupons = it)))
                    else
                        emit(Either.Right(CouponScreenEffect.NoCouponsAvailable))
                }
            } else {
                // if we get success false in response the throw error with respected message
                emit(Either.Left(CouponError.CouponFetchError(couponsResponse.message)))
            }
            emit(Either.Right(Success.Loading(false)))
        } catch (e: HttpException) {  // if it was not successful
            emit(Either.Right(Success.Loading(false)))
//            emit(Either.Left(Failure.GenericError(Exception(CouponConstants.SERVER_ERROR_MESSAGE))))
            emit(Either.Left(CouponError.CouponFetchError(
                CouponConstants.SERVER_ERROR_MESSAGE)))
        } catch (e: IOException) { // if our  api cant talk to remote api(usually internet error )
            emit(Either.Right(Success.Loading(false)))
            emit(Either.Left(Failure.NetworkConnection))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun createListAsync(data: AllCouponsDto) = flow<List<DataItem>> {
        val finalList = mutableListOf<DataItem>()
        if (!data.applicableCoupons.isNullOrEmpty()) {
            // adding applicable header
            finalList.add(DataItem.Header(1, APPLICABLE_COUPONS))
            // if applied coupon is already present then add it first in list
            if (!data.appliedCoupon.isNullOrEmpty())
                finalList.add(DataItem.DataModel(data.appliedCoupon[0].toCoupon(applied = true)))

            //  add the applicable coupons and change it to the ui consumable model
            finalList.addAll(data.applicableCoupons.map {
                DataItem.DataModel(it.toCoupon())
            })
        }

        if (!data.nonApplicableCoupons.isNullOrEmpty()) {
            // add unlock header
            finalList.add(DataItem.Header(2, UNLOCK_COUPONS))
            // add all no applicable coupons and changing it to the ui consumable model
            finalList.addAll(data.nonApplicableCoupons.map {
                DataItem.DataModel(it.toCoupon().copy(isLocked = true))
            })
        }
        emit(finalList)
    }.flowOn(Dispatchers.Default) // running in another thread
}