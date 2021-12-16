package com.master.coupon.presentation.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.master.coupon.R
import com.master.coupon.databinding.CommonCurvedSheetBinding

// simple name for lambda, this lambda will help in getting binding class of sub class
typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

/**
 * This bottom sheet if common for all the same type of sheets used in the coupon module
 * this class is of subtype of any viewBinding class and also takes func as param
 * this class will contain the common code for all binding operations(no more binding initialization code in sub class)
 */
abstract class CurvedBottomSheet<VB : ViewBinding>( val inflate: Inflate<VB>) : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun getTheme(): Int {
        return R.style.CouponCurvedTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // getting the view of this particular class
        val local = CommonCurvedSheetBinding.inflate(inflater, container, false)
        // getting the view of sub class through binding
        _binding = inflate.invoke(inflater, container, false)
        // add the sub class view to this parent class view
        local.flContainer.addView(binding.root)
        initBinding(local.root as ConstraintLayout)
        return local.root
    }

    /*
        this is the helper method for the sub classes if they want to pass data(viewModel)
         into the binding for data binding they can use this method
     */
    protected open fun initBinding(root: ConstraintLayout) {}
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // clearing up binding
    }
}