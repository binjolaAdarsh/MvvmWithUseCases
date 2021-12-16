package com.master.coupon.presentation.coupons


import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.master.coupon.R
import com.master.coupon.common.*
import com.master.coupon.common.CouponsRequestData.Companion.asApplyCouponRequest
import com.master.coupon.common.CouponsRequestData.Companion.asRemoveCouponRequest
import com.master.coupon.data.ConnectionManager
import com.master.coupon.databinding.LayoutCouponActivityBinding
import com.master.coupon.domain.model.*
import com.master.coupon.domain.model.Coupon.Companion.asCouponDetailData
import com.master.coupon.domain.use_case.get_coupons.*
import com.master.coupon.presentation.coupon_details.ViewCouponDetailsSheet
import com.master.coupon.presentation.util.hideKeyboard
import com.master.coupon.presentation.util.onDone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class ViewCouponActivity : AppCompatActivity() {


    private var _binding: LayoutCouponActivityBinding? = null
    private val binding get() = _binding!!


    private var firstTimeScreen = true
    private var searchClearVisible = false
    private var lastAppliedMessage = ""

    //data required to use in request for coupons api
    private val couponRequestData: CouponsRequestData by lazy {
        intent.extras?.getParcelable(CouponConstants.KEY_COUPON_EXTRA_DATA) ?: CouponsRequestData()
    }

    // for sending tracking to client using coupon module
    private val couponTrackingListener: CouponEventTrackListener? by lazy {
        intent.extras?.getParcelable(CouponConstants.KEY_COUPON_TRACK_EVENT)
    }
    // for sending tracking to client using coupon module
    private val trackingSender: TrackingSender by lazy {
        TrackingSenderImpl(couponTrackingListener)
    }

    // to track which screen opened the coupon module (either from VIEW COUPON section or MY COUPON section)
    private val couponPage: CouponPage by lazy {
        intent.extras?.getParcelable(CouponConstants.KEY_COUPON_FROM_WHICH_SCREEN)
            ?: CouponPage.VIEW_COUPON
    }

    // adapter for coupon listing
    private val couponAdapter: ViewCouponAdapter by lazy { ViewCouponAdapter(couponClickEventHandler) }

    // manage ui state screen with this class
    private var couponUiState = CouponUiState(showScreen = true)

    // viewModel for coupon list and coupon details
     val couponViewModel: ViewCouponViewModel by viewModels()

    /**
     * @return it returns the default factory req for the viewModel
     */



    /**
     * this lambda handles all the events(click events mostly)  of coupon listing screen
     */
    private val couponClickEventHandler: (CouponClickEvent) -> Unit = { clickEvent ->
        when (clickEvent) {
            is CouponClickEvent.ViewDetailEvent -> {
                couponDetailHandler(clickEvent.coupon)
            }
            is CouponClickEvent.DeltaEvent -> {
                handleDeltaClickedState(clickEvent.coupon)
            }
            is CouponClickEvent.ApplyEvent -> {
                if (clickEvent.coupon.removeAndApplyCase()) { // if other coupon is already applied
                    trackingSender.removeAndApplyListPageTrackingInitiated(clickEvent.coupon)
                    openRemoveAndApplySheet { // on confirming apply event
                        trackingSender.removeAndApplyListPageTrackingCompleted(clickEvent.coupon)
                        triggerApplyEvent(clickEvent.coupon)
                    }
                } else {// simple trigger apply event
                   trackingSender.onApplyListPageTracking(clickEvent.coupon)
                    triggerApplyEvent(clickEvent.coupon)
                }
            }
            is CouponClickEvent.RemoveEvent -> {
                trackingSender.removeListPageTrackingInitiated(clickEvent.coupon)
                openConfirmRemoveSheet { // on confirming remove event
                    trackingSender.removeListPageTrackingCompleted(clickEvent.coupon)
                    couponViewModel.handleEvent(CouponEvent.RemoveCouponEvent(clickEvent.coupon.couponId,
                        couponRequestData.asRemoveCouponRequest()))
                }
            }
            else -> Unit
        }
    }


    private fun triggerApplyEvent(coupon: Coupon) {
        couponViewModel.handleEvent(CouponEvent.ApplyCouponEvent(couponRequestData.asApplyCouponRequest(
            coupon.couponCode)))
    }

    private fun couponDetailHandler(coupon: Coupon) {
        // create view detail  sheet
       trackingSender.onViewDetailTracking(coupon)
        openViewDetailSheet(coupon,
            onApplyClicked = {
                if (coupon.isLocked)   // if locked coupon then its delta case handle it separately
                    handleDeltaClickedState(coupon)
                else {
                    if (coupon.removeAndApplyCase()) {
                        trackingSender.removeAndApplyDetailPageTrackingInitiated(coupon)
                        openRemoveAndApplySheet{ // on confirming apply
                            trackingSender.removeAndApplyDetailPageTrackingCompleted(coupon)
                            triggerApplyEvent(coupon)
                        }

                    } else { // direct apply
                        trackingSender.onApplyDetailPageTracking(coupon)
                        triggerApplyEvent(coupon)
                    }
                }
            },
            onRemoveClicked = {
                trackingSender.onRemoveDetailPageTrackingInitiated(coupon)
                openConfirmRemoveSheet { // on confirming remove
                      trackingSender.onRemoveDetailPageTrackingCompleted(coupon)
                    couponViewModel.handleEvent(CouponEvent.RemoveCouponEvent(coupon.couponId,
                        couponRequestData.asRemoveCouponRequest()))
                }

            }
        )


    }

    private fun handleDeltaClickedState(coupon: Coupon) {
        trackingSender.onLockedCouponDeltaTracking(coupon)
        if (isAlreadyAppliedCoupon())
            sendResultBack()
        finish()
    }

    private fun isAlreadyAppliedCoupon(): Boolean =
        couponViewModel.isAlreadyAppliedCoupon(couponAdapter.currentList)

    private fun openViewDetailSheet(
        coupon: Coupon,
        onApplyClicked: () -> Unit,
        onRemoveClicked: () -> Unit,
    ) {
        ViewCouponDetailsSheet.newInstance(coupon.asCouponDetailData(), couponRequestData.domain)
            .apply {
                setOnCancelListener { dismiss() }
                setOnRemoveListener {
                    onRemoveClicked()
                    dismiss()
                }
                setOnApplyListener {
                    onApplyClicked()
                    dismiss()
                }
                setOnTncClickListener { trackingSender.onTncClicked(coupon) }
                show(supportFragmentManager, "")
            }
    }


    /**
     *  checks whether particular coupon is not already applied but there is some other coupon already applied in list
     */
    private fun Coupon.removeAndApplyCase() =
        !applied && isAlreadyAppliedCoupon()


    /**
     * this func opens the remove and apply sheet
     * this sheet have 2 actions (apply , cancel)
     * clicking cancel will close the sheet
     * clicking the apply action close sheet and triggers the lambda and respective action is performed
     */
    private fun openRemoveAndApplySheet(onApplyClicked: () -> Unit) {
        RemoveAndAppliedSheet.newInstance().apply {
            setOnApplyListener {
                dismiss()
                onApplyClicked()
            }
            // in cancel action simply close the sheet
            setOnCancelListener { dismiss() }
            show(supportFragmentManager, "")
        }
    }

    /**
     * this func opens the confirm remove sheet that gives 2 options to user to remove or to cancel
     * clicking cancel will close the sheet
     * clicking the remove action close sheet and triggers the lambda and respective action is performed
     */
    private fun openConfirmRemoveSheet(onActionClicked: () -> Unit) {
        ConfirmRemoveSheet.newInstance().apply {
            setOnRemoveListener {
                dismiss()
                onActionClicked()
            }
            setOnCancelListener { dismiss() }
            show(supportFragmentManager, "")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        initNetwork(interceptors())
        _binding = DataBindingUtil.setContentView(this, R.layout.layout_coupon_activity)

        binding.apply {
            lifecycleOwner = this@ViewCouponActivity
            adapter = couponAdapter
        }

        observeInternetConnectivity()


        binding.rvViewCoupon.addItemDecoration(createItemDivider())
        setupToolbar()

        binding.etCouponCode.doAfterTextChanged {
            showApplyButton()
            hideSearchCouponError()
            searchClearVisible = false
            binding.btApply.isEnabled = it?.toString()?.trim()?.isEmpty() == true
            if (it?.toString()?.trim()?.isNotEmpty() == true) {
                binding.btApply.isEnabled = true
                binding.btApply.setTextColor(ContextCompat.getColor(this,
                    R.color.coupon_primary_color))
            } else {
                binding.btApply.isEnabled = false
                binding.btApply.setTextColor(ContextCompat.getColor(this,
                    R.color.coupon_apply_border))

            }
        }
        binding.etCouponCode.onDone {
            binding.btApply.performClick()
        }

        binding.btApply.setOnClickListener {
            if (searchClearVisible) {
                binding.etCouponCode.setText("")
                return@setOnClickListener
            }
            trackingSender.onApplySearchBlock(Coupon(couponCode = binding.etCouponCode.text.toString()))
            couponViewModel.handleEvent(CouponEvent.ApplySearchCouponEvent(couponRequestData.asApplyCouponRequest(
                binding.etCouponCode.text.toString())))
        }

        binding.layoutError.btOk.setOnClickListener {
            onBackPressed()
        }
        binding.layoutEmpty.btOk.setOnClickListener {
            onBackPressed()
        }

        couponViewModel.observe(this, ::renderSuccessState, ::renderErrorState)
    }


    private fun observeInternetConnectivity() {
        val connectionLiveData = ConnectionManager(this)

        connectionLiveData.observe(this) { isConnected ->
            if (firstTimeScreen && isConnected)
                return@observe

            firstTimeScreen = false

            if (isConnected) {
                Snackbar.make(this,
                    binding.root,
                    getString(R.string.coupon_online_back),
                    Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(this,
                        R.color.coupon_internet_available)).show()
            } else {
                Snackbar.make(this,
                    binding.root,
                    getString(R.string.coupon_offline_msg),
                    Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(this, R.color.coupon_error_color))
                    .show()
            }
        }
    }

    private fun renderSuccessState(it: Success) {
        when (it) {
            is Success.ViewEffect -> {
                reactToEffects(it)
            }
            is Success.ViewState -> {
                reactToState(it)
            }
        }

    }

    private fun reactToState(state: Success.ViewState) {
        when (state) {
            is Success.Loading -> {
                updateUi(couponUiState.copy(loading = state.isLoading))
            }
            is CouponState -> {
                updateUi(couponUiState.copy(couponLoaded = true))
                couponAdapter.submitList(state.coupons)
                binding.executePendingBindings()
                sendPageLoadEvent(state.coupons)

            }
            is ApplyDataResponse -> {
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.CREATED) {
                        couponViewModel.getAppliedStateCoupons(couponCode = state.couponCode,
                            couponAdapter.currentList)
                            .collect {
                                couponAdapter.submitList(it.second)
                                lastAppliedMessage = state.message
                                openCongratsSheet(it.first)
                            }
                    }
                }
            }
            is ApplySearchDataResponse -> {
                hideKeyboard(this@ViewCouponActivity, binding.etCouponCode)
                Toast.makeText(this, "Coupon Applied Successfully", Toast.LENGTH_SHORT).show()
                sendResultBack(state.message)
                finish()

            }
            is RemoveDataResponse -> {
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.CREATED) {
                        couponViewModel.getRemovedStateCoupons(couponAdapter.currentList)
                            .collect {
                                couponAdapter.submitList(it)
                            }
                    }
                }
            }
        }
    }

    private fun reactToEffects(effect: Success.ViewEffect) {
        when (effect) {
            CouponScreenEffect.NoCouponsAvailable ->
                updateUi(couponUiState.copy(emptyList = true))
            CouponScreenEffect.CouponApplied -> Unit
            is CouponScreenEffect.CouponRemoved -> {
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.CREATED) {
                        couponViewModel.getRemovedCoupon(effect.couponId, couponAdapter.currentList)
                            .collect {
                                if (it != null && isActive) {
                                    Snackbar.make(this@ViewCouponActivity,
                                        binding.root,
                                        effect.removedMessage,
                                        Snackbar.LENGTH_SHORT).show()
                                    lastAppliedMessage = ""
                                }
                            }
                    }

                }
            }
        }
    }

    private fun renderErrorState(failure: Failure) {
        when (failure) {
            is CouponError.CouponFetchError -> {
                updateUi(couponUiState.copy(errorOccurred = true, showScreen = true))
            }
            is CouponError.CouponApplyFailError -> {
                Snackbar.make(this@ViewCouponActivity,
                    binding.root,
                    failure.message,
                    Snackbar.LENGTH_SHORT).show()
            }
            is CouponError.CouponSearchApplyFailError -> {
                showClearButton()
                searchClearVisible = true
                binding.tvCouponError.isVisible = true
                if (failure.message.isEmpty()) {
                    val appliedOrRemoved = getString(R.string.coupon_applied)
                    binding.tvCouponError.text =
                        String.format(getString(R.string.coupon_error), appliedOrRemoved)
                } else {
                    binding.tvCouponError.text = failure.message
                }
            }

            is CouponError.CouponRemoveFailError -> {
                Snackbar.make(this@ViewCouponActivity,
                    binding.root,
                    failure.message,
                    Snackbar.LENGTH_SHORT).show()
            }

            Failure.NetworkConnection -> {
                Snackbar.make(this@ViewCouponActivity,
                    binding.root,
                    "Please check your network connection",
                    Snackbar.LENGTH_SHORT).show()
            }
            else -> Unit
        }

    }

    private fun updateUi(uiState: CouponUiState) {
        couponUiState = uiState
        uiState.apply {
            binding.root.isVisible = showScreen
            binding.progressBar2.isVisible = loading
            binding.group3.isVisible = couponLoaded
            binding.layoutEmpty.root.isVisible = emptyList
            binding.layoutError.root.isVisible = errorOccurred
        }
    }

    private fun createItemDivider(): DividerItemDecoration {
        val itemDivider = DividerItemDecoration(this,
            DividerItemDecoration.VERTICAL)

        itemDivider.setDrawable(ShapeDrawable().apply {
            intrinsicHeight = resources.getDimensionPixelOffset(R.dimen.coupon_item_divider_height)
            paint.color = ContextCompat.getColor(this@ViewCouponActivity,
                R.color.coupon_background) // note: currently (support version 28.0.0), we can not use transparent color here, if we use transparent, we still see a small divider line. So if we want to display transparent space, we can set color = background color or we can create a custom ItemDecoration instead of DividerItemDecoration.
        })
        return itemDivider
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = if (couponRequestData.title.isNotEmpty()) {
                couponRequestData.title
            } else {
                if (couponPage == CouponPage.VIEW_COUPON) getString(R.string.title_view_coupon) else getString(
                    R.string.title_view_coupon)
            }
        }
    }


    private fun sendPageLoadEvent(data: List<DataItem>) {
        lifecycleScope.launch(Dispatchers.Default) {
            val coupons = data.filterIsInstance<DataItem.DataModel>()
            if (!coupons.isNullOrEmpty() && isActive) {
                trackingSender.onPageLoadTracking(coupons,couponPage)
            }
        }
    }



    private fun openCongratsSheet(appliedCoupon: Coupon?) {
        if (appliedCoupon != null) {
            CouponAppliedSheet.newInstance(appliedCoupon.discount?.toString(),
                appliedCoupon.headingText,
                appliedCoupon.subText).apply {
                setOnApplyConfirmListener {
                    this.dismiss()
                    trackingSender.onApplyCongratsClickTracking(appliedCoupon,couponPage)
                    sendResultBack(lastAppliedMessage)
                    finish()
                }
                show(supportFragmentManager, "")
            }
        }
    }


    private fun hideSearchCouponError() {
        binding.tvCouponError.visibility = View.INVISIBLE
    }

    private fun showClearButton() {
        binding.btApply.text = getString(R.string.clear)
        binding.btApply.setTextColor(ContextCompat.getColor(this, R.color.coupon_error_color))
        binding.tilCouponCode.background =
            ContextCompat.getDrawable(this, R.drawable.coupon_search_error_bg)
    }

    private fun showApplyButton() {
        binding.btApply.text = getString(R.string.apply)
        binding.btApply.setTextColor(ContextCompat.getColor(this,
            R.color.coupon_apply_text_color_disables))
        binding.tilCouponCode.background =
            ContextCompat.getDrawable(this, R.drawable.coupon_search_bg)
    }

    private fun sendResultBack(msg: String = "") {
        if (couponPage == CouponPage.VIEW_COUPON)
            setResult(Activity.RESULT_OK, Intent().putExtra(Intent.EXTRA_TEXT, msg))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard(this@ViewCouponActivity, binding.etCouponCode)
                if (isAlreadyAppliedCoupon())
                    sendResultBack(lastAppliedMessage)
                finish()
                return true
            }

        }
        return super.onContextItemSelected(item)
    }


    override fun onBackPressed() {
        if (isAlreadyAppliedCoupon())
            sendResultBack(lastAppliedMessage)
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
