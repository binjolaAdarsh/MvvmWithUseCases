package com.master.coupon.common

import androidx.annotation.NonNull

/**
 *  represent 2 path ,either success(right) or failure(left)
 */
sealed class Either<out L,out R>{
    // represents the left side of either class
    // which by convention is a "failure"
    data class  Left<out L>(@NonNull val a:L): Either<L, Nothing>()

    // represents the right side of either class
    // which by convention is a "success"
    data class  Right<out R>(@NonNull val b:R): Either<Nothing, R>()
}
