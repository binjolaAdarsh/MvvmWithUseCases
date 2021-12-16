package com.master.coupon.presentation.coupons

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.master.coupon.R
import com.master.coupon.databinding.FragmentConfirmRemoveSheetBinding
import com.master.coupon.presentation.util.CurvedBottomSheet


internal class ConfirmRemoveSheet : CurvedBottomSheet<FragmentConfirmRemoveSheetBinding>(FragmentConfirmRemoveSheetBinding::inflate) {
    private var cancelListener: () -> Unit = {}
    private var removeListener: () -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.btRemove).setOnClickListener { removeListener.invoke() }
        view.findViewById<TextView>(R.id.btCancel).setOnClickListener { cancelListener.invoke() }
    }

    fun setOnRemoveListener(function: () -> Unit) {
        removeListener = function
    }

    fun setOnCancelListener(function: () -> Unit) {
        cancelListener = function
    }

    companion object {
        @JvmStatic
        fun newInstance() = ConfirmRemoveSheet()
    }
}