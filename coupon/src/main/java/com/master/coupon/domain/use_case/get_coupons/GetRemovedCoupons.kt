package com.master.coupon.domain.use_case.get_coupons

import com.master.coupon.domain.model.DataItem
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
@ViewModelScoped
class GetRemovedCoupons @Inject constructor(){
    operator fun invoke(
       coupons: List<DataItem>
    ): Flow<List<DataItem>> = flow {
        val appliedList = coupons.map {  dataItem ->
            if (dataItem is DataItem.DataModel)
                DataItem.DataModel(dataItem.data.copy(applied = false))
            else
                dataItem
        }
        emit(appliedList)
    }.flowOn(Dispatchers.Default)
}