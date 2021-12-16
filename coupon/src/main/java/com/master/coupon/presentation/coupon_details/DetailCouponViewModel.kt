package com.master.coupon.presentation.coupon_details

import androidx.lifecycle.*
import com.master.coupon.common.Either
import com.master.coupon.common.Failure
import com.master.coupon.common.Success
import com.master.coupon.domain.model.CouponDetails
import com.master.coupon.domain.use_case.get_coupons.CouponDetailEvent
import com.master.coupon.domain.use_case.get_coupons.CouponDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
internal class DetailCouponViewModel @Inject constructor(val couponDetailUseCase: CouponDetailUseCase) : ViewModel() {
    private val errorLiveData = MutableLiveData<Failure>()
    private val successLiveData = MutableLiveData<Success>(Success.Idle)
    internal var couponDetailFetchedData = CouponDetails()

    // pass event type from view and  handle it in viewModel
    fun handleEvent(event: CouponDetailEvent) {
        when(event){
            is CouponDetailEvent.LoadCouponDetailEvent -> viewCouponDetail(event.couponId)
        }
    }
    // wrapper func for multiple observer types
    fun observe(owner: LifecycleOwner,
                successObserver:(Success)->Unit,
                failureObserver:(Failure)->Unit){
        successLiveData.observe(owner,Observer{ successObserver(it) })
        errorLiveData.observe(owner,Observer{ failureObserver(it) })
    }

    // func to call couponDetail useCase
    private fun viewCouponDetail(couponId: String) {
        couponDetailUseCase.getCouponDetailUseCase(couponId).onEach {
//            delay(3000)
            handleResult(it)
        }.launchIn(viewModelScope)
    }

    private fun handleResult(result: Either<Failure, Success>){
        when (result) {
            is Either.Left -> errorLiveData.value = result.a!!
            is Either.Right -> successLiveData.value = result.b!!
        }
    }
}