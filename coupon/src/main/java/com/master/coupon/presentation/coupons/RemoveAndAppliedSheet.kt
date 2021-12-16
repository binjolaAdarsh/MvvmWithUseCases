package com.master.coupon.presentation.coupons

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.master.coupon.R
import com.master.coupon.databinding.FragmentRemoveAndAppliedSheetBinding
import com.master.coupon.presentation.util.CurvedBottomSheet

internal class RemoveAndAppliedSheet : CurvedBottomSheet<FragmentRemoveAndAppliedSheetBinding>(
    FragmentRemoveAndAppliedSheetBinding::inflate) {
    private var cancelListener: () -> Unit = {}
    private var applyListener: () -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.btApply)
                .setOnClickListener { applyListener.invoke() }

        view.findViewById<TextView>(R.id.btCancel)
                .setOnClickListener { cancelListener.invoke() }
    }

    fun setOnApplyListener(function: () -> Unit) {
        applyListener = function
    }

    fun setOnCancelListener(function: () -> Unit) {
        cancelListener = function
    }

    companion object {
        @JvmStatic
        fun newInstance() = RemoveAndAppliedSheet()
    }
}