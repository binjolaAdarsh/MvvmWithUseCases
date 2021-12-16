package com.master.coupon.presentation.coupons

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.color
import com.master.coupon.R
import com.master.coupon.databinding.FragmentCouponAppliedSheetBinding
import com.master.coupon.presentation.util.CurvedBottomSheet

internal class CouponAppliedSheet : CurvedBottomSheet<FragmentCouponAppliedSheetBinding>(FragmentCouponAppliedSheetBinding::inflate) {

    private var confirmListener: () -> Unit = {}

    private val saveAmount: String by lazy {
        arguments?.getString(KEY_SAVE_AMOUNT) ?: ""
    }
    private val savedText: String by lazy {
        arguments?.getString(KEY_SAVED_TEXT) ?: ""
    }
    private val saveSubText: String by lazy {
        arguments?.getString(KEY_SAVED_SUB_TEXT) ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tvSubText).text = saveSubText
        val tvSaved = view.findViewById<TextView>(R.id.tvSaved)
        if (savedText.isNotEmpty()) {
            if (savedText.indexOf("<value>", ignoreCase = true) > -1) {
                val data = savedText.split("<value>")
                tvSaved.text = SpannableStringBuilder().append(data[0]).append(" ")
                        .color(ContextCompat.getColor(requireContext(), R.color.coupon_apply_green)) { append("₹$saveAmount") }
            } else {
                tvSaved.text = savedText
            }
        }
        view.findViewById<TextView>(R.id.btViewCart)
                .setOnClickListener { confirmListener() }
    }

    fun setOnApplyConfirmListener(function: () -> Unit) {
        confirmListener = function
    }



    companion object {
        private const val KEY_SAVE_AMOUNT = "save_amount"
        private const val KEY_SAVED_TEXT = "saved_text"
        private const val KEY_SAVED_SUB_TEXT = "saved_sub_text"

        @JvmStatic
        fun newInstance(saveAmount: String?, savedText: String?, savedSubText: String?) = CouponAppliedSheet().apply {
            arguments = Bundle().apply {
                putString(KEY_SAVE_AMOUNT, saveAmount)
                putString(KEY_SAVED_TEXT, savedText)
                putString(KEY_SAVED_SUB_TEXT, savedSubText)
            }
        }
    }
}