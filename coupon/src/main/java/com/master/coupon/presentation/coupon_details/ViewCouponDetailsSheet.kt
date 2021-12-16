package com.master.coupon.presentation.coupon_details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.master.coupon.R
import com.master.coupon.common.CouponConstants
import com.master.coupon.common.Failure
import com.master.coupon.common.Success
import com.master.coupon.databinding.FragmentCouponDetailsSheetBinding
import com.master.coupon.domain.model.CouponDetailData
import com.master.coupon.domain.model.CouponDetails
import com.master.coupon.domain.model.CouponError
import com.master.coupon.domain.model.FreeBies
import com.master.coupon.domain.use_case.get_coupons.CouponDetailEvent
import com.master.coupon.presentation.coupons.FreeItemAdapter
import com.master.coupon.presentation.util.CurvedBottomSheet
import com.master.coupon.presentation.util.setCopiedToClipboard
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * this class is for showing the details of coupon in bottom sheet way
 */
@AndroidEntryPoint
internal class ViewCouponDetailsSheet : CurvedBottomSheet<FragmentCouponDetailsSheetBinding>(
    FragmentCouponDetailsSheetBinding::inflate) {
    private var applyListener: () -> Unit = {}
    private var removeListener: () -> Unit = {}
    private var cancelListener: () -> Unit = {}
    private var tncClickListener: () -> Unit = {}

    override fun onStart() {
        super.onStart()
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private val couponDetailData: CouponDetailData by lazy {
        arguments?.getParcelable(CouponConstants.KEY_COUPON_DETAIL)?: CouponDetailData()
    }


    private val freeItemAdapter: FreeItemAdapter by lazy {
        FreeItemAdapter(itemClickListener)
    }
    private val itemClickListener: (FreeBies) -> Unit = {

    }

    private val detailCouponViewModel: DetailCouponViewModel by viewModels()

    override fun initBinding(root: ConstraintLayout) {
        binding.apply {
            lifecycleOwner = this@ViewCouponDetailsSheet
            detail = CouponDetails()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (couponDetailData.isApplied) {
            binding.groupRemoveAndCancel.isVisible = true
            binding.btApply.isVisible = false
        } else {
            binding.groupRemoveAndCancel.isVisible = false
            binding.btApply.isVisible = true
        }
        if (couponDetailData.isLocked) {
            binding.btApply.isVisible = false
            binding.groupRemoveAndCancel.isVisible = false

//            binding.divider.visibility =View.INVISIBLE
            binding.tvDelta.apply {
                text = couponDetailData.deltaText
                setOnClickListener { applyListener.invoke() }
            }
        }

        binding.groupRemoveAndCancel.isEnabled = !couponDetailData.isLocked
        binding.btApply.setOnClickListener { applyListener.invoke() }
        binding.btCancel.setOnClickListener { cancelListener.invoke() }
        binding.btRemove.setOnClickListener { removeListener.invoke() }
        binding.tvCopyCoupon.setOnClickListener { setCopiedToClipboard(requireContext(), binding.tvCopyCoupon.text.toString(), "Coupon code copied") }
        binding.rvFreeItems.adapter = freeItemAdapter

        binding.tvViewAllTerms.setOnClickListener {
//            TermsAndConditionActivity.showScreen(requireContext(), detailCouponViewModel.couponDetailFetchedData.couponTncUrl)
//            tncClickListener.invoke()
        }

        detailCouponViewModel.handleEvent(CouponDetailEvent.LoadCouponDetailEvent(couponDetailData.couponId.toString()))
        detailCouponViewModel.observe(viewLifecycleOwner, ::renderSuccessState, ::renderErrorState)

        binding.layoutError.btOk.setOnClickListener {
            dialog?.dismiss()
        }
        binding.layoutError.tvErrorDetail.text = getString(R.string.error_occurred_view_detail)

        binding.executePendingBindings()
    }

    private fun renderSuccessState(it: Success) {
        when (it) {
            is Success.ViewEffect -> {

            }
            is Success.ViewState -> {
                reactToState(it)
            }
        }

    }

    private fun reactToState(state: Success.ViewState) {

        when(state){
            is Success.Loading -> {
                binding.progressBar2.isVisible = state.isLoading
            }

            is CouponDetails ->{
                binding.llCouponDetail.isVisible = true

                binding.detail = state.copy(isLocked = couponDetailData.isLocked)
                if (state.freebies.isNullOrEmpty())
                    binding.freeItemGroup.visibility = View.GONE
                else {
                    binding.freeGiftTitle.text = "Free Gifts Available - ${state.freebies!!.size}"
                    binding.freeItemGroup.visibility = View.VISIBLE
                }
                freeItemAdapter.submitList(state.freebies)
                detailCouponViewModel.couponDetailFetchedData = state
                binding.executePendingBindings()
            }
        }
    }

    private fun renderErrorState(failure: Failure) {
        when (failure) {
            is CouponError.CouponDetailError-> {
                binding.layoutError.root.isVisible = true
                binding.llCouponDetail.isVisible = false
            }

            /*is Failure.FeatureFailure -> { }*/
            is Failure.GenericError -> {
                Toast.makeText(requireContext(),
                    failure.exception.message ?: "generic error occured",
                    Toast.LENGTH_SHORT).show()
            }
            Failure.NetworkConnection -> {
                Toast.makeText(requireContext(), "please check your network connection", Toast.LENGTH_SHORT)
                    .show()
            }
            is Failure.ServerError -> {
                Toast.makeText(requireContext(), failure.error, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }

    }

    fun setOnApplyListener(function: () -> Unit) {
        applyListener = function
    }

    fun setOnRemoveListener(function: () -> Unit) {
        removeListener = function
    }

    fun setOnCancelListener(function: () -> Unit) {
        cancelListener = function
    }

    fun setOnTncClickListener(function: () -> Unit) {
        tncClickListener = function
    }

    companion object {


        @JvmStatic
       internal fun newInstance(couponDetailData: CouponDetailData, domain: String) = ViewCouponDetailsSheet().apply {
            arguments = Bundle().apply {
                putParcelable(CouponConstants.KEY_COUPON_DETAIL, couponDetailData)
                putString(CouponConstants.KEY_DOMAIN, domain)
            }
        }



    }
}