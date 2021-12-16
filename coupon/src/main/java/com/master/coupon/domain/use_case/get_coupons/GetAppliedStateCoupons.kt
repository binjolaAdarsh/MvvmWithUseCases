package com.master.coupon.domain.use_case.get_coupons

import com.master.coupon.domain.model.Coupon
import com.master.coupon.domain.model.DataItem
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
@ViewModelScoped
class GetAppliedStateCoupons @Inject constructor(){
    operator fun invoke(
        couponCode: String, coupons: List<DataItem>
    ): Flow<Pair<Coupon?,List<DataItem>>> = flow {

        var appliedCoupon: Coupon?= null
        val appliedList = coupons.map {  dataItem ->
            if (dataItem is DataItem.DataModel) {
                if (dataItem.data.couponCode == couponCode && (appliedCoupon== null)) {
                    appliedCoupon = dataItem.data
                }

                DataItem.DataModel(dataItem.data.copy(applied = dataItem.data.couponCode == couponCode))
            }else
                dataItem
        }

        emit(Pair(appliedCoupon, appliedList))
    }.flowOn(Dispatchers.Default)
}