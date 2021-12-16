package com.master.coupon.presentation.coupons

import androidx.lifecycle.*
import com.master.coupon.common.Either
import com.master.coupon.common.Failure
import com.master.coupon.common.Success
import com.master.coupon.domain.model.*
import com.master.coupon.domain.use_case.get_coupons.CouponEvent
import com.master.coupon.domain.use_case.get_coupons.CouponUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ViewCouponViewModel @Inject constructor(
     private val couponUseCases: CouponUseCases
) : ViewModel() {
    private val errorLiveData = MutableLiveData<Failure>()


    private val successLiveData = MutableLiveData<Success>(Success.Idle)

    init { handleEvent(CouponEvent.LoadCouponEvent) }

    fun handleEvent(event: CouponEvent) {
        when (event) {
            is CouponEvent.ApplyCouponEvent -> applyCoupon(event.applyRequest)
            is CouponEvent.LoadCouponEvent -> getCoupons()
            is CouponEvent.RemoveCouponEvent -> removeCoupon(couponId =event.couponId ,event.removeRequest)
            is CouponEvent.ApplySearchCouponEvent -> applySearchCoupon(event.applyRequest)
        }
    }

    fun observe(owner: LifecycleOwner,
                successObserver:(Success)->Unit,
                failureObserver:(Failure)->Unit){
        successLiveData.observe(owner,Observer{ successObserver(it) })
        errorLiveData.observe(owner,Observer{ failureObserver(it) })
    }

    private fun getCoupons() {
        couponUseCases.getCouponUseCase().onEach {
            handleResult(it)
        }.launchIn(viewModelScope)
    }

   private fun applyCoupon(req: ApplyCouponRequest) {
        couponUseCases.applyCouponUseCase(req).onEach { result ->
            handleResult(result)
        }.launchIn(viewModelScope)
    }

    private fun applySearchCoupon(req: ApplyCouponRequest) {
        couponUseCases.applySearchCouponUseCase(req).onEach { result ->
            handleResult(result)
        }.launchIn(viewModelScope)
    }
    private fun handleResult(result:Either<Failure,Success>){
        when (result) {
            is Either.Left -> errorLiveData.value = result.a!!
            is Either.Right -> successLiveData.value = result.b!!
        }
    }

     fun getAppliedStateCoupons(couponCode: String, coupons: List<DataItem>):Flow<Pair<Coupon?,List<DataItem>>> =
         couponUseCases.appliedStateCoupons(couponCode,coupons)

    fun getRemovedStateCoupons(coupons: List<DataItem>):Flow<List<DataItem>> =
         couponUseCases.removeStateCoupons(coupons)

   fun getRemovedCoupon(id:Long,coupons: List<DataItem>) = flow {
        val removedCoupon = coupons.asCoupon(id)
        emit(removedCoupon)
    }.flowOn(Dispatchers.Main)



    fun isAlreadyAppliedCoupon(coupons:List<DataItem>): Boolean {
        return coupons.filterIsInstance<DataItem.DataModel>().any { it.data.applied }
    }

    fun removeCoupon(couponId:Long,removeReq: RemoveCouponRequest) {
        couponUseCases.removeCouponUseCase(couponId = couponId,removeReq).onEach { result ->
            handleResult(result)
        }.launchIn(viewModelScope)
    }


}
