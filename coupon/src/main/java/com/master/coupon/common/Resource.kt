package com.master.coupon.common

import java.lang.Exception

sealed class Failure{
    data class ServerError(val error:String): Failure()
    object  NetworkConnection: Failure()
    data class GenericError(val exception: Exception): Failure()

    // extend this class for feature specific failure
    abstract  class FeatureFailure: Failure()
}

sealed class  Success{
    // extend this classes for feature specific success
    abstract  class ViewState: Success()
    abstract  class ViewEffect: Success()

    // app level states
    object Idle: ViewState()
    class Loading(val isLoading:Boolean=true): ViewState()
}