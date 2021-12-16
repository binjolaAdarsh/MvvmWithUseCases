package com.master.coupon.presentation.coupons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.master.coupon.databinding.ItemViewCouponHeaderBinding
import com.master.coupon.databinding.ViewCouponItemBinding
import com.master.coupon.domain.model.Coupon
import com.master.coupon.domain.model.CouponClickEvent
import com.master.coupon.domain.model.DataItem

class ViewCouponAdapter(
    private val clickEvent: (CouponClickEvent) -> Unit,
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(COUPON_DIFF_UTIL) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder(ItemViewCouponHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ITEM_VIEW_TYPE_ITEM -> ViewCouponViewHolder(ViewCouponItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewCouponViewHolder -> {
                val item = getItem(position) as DataItem.DataModel
                holder.bind(item.data)
            }
            is HeaderViewHolder -> {
                val nightItem = getItem(position) as DataItem.Header
                holder.bind(nightItem)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.DataModel -> ITEM_VIEW_TYPE_ITEM
        }
    }


    inner class ViewCouponViewHolder(val binding: ViewCouponItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btApply.setOnClickListener {
                safeCall {
                    if(it.applied)
                    clickEvent.invoke(CouponClickEvent.RemoveEvent(it))
                    else
                        clickEvent.invoke(CouponClickEvent.ApplyEvent(it))
                }
            }
            binding.tvViewDetails.setOnClickListener {
                safeCall {
                    clickEvent.invoke(CouponClickEvent.ViewDetailEvent(it))
                }

            }
            binding.tvDelta.setOnClickListener {
                safeCall {
                    clickEvent.invoke(CouponClickEvent.DeltaEvent(it))
                }
            }
        }

        private fun safeCall(data: (Coupon) -> Unit) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = getItem(position)
                if (item != null && item is DataItem.DataModel) {
                    data(item.data)
                }
            }
        }

        fun bind(coupon: Coupon) {
            binding.coupon = coupon
            binding.executePendingBindings()
        }
    }

    // Header view holder
    class HeaderViewHolder(val binding: ItemViewCouponHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(nightItem: DataItem.Header) {
            binding.name = nightItem.name
            binding.executePendingBindings()
        }
    }

    companion object {
        private const val ITEM_VIEW_TYPE_HEADER = 0
        private const val ITEM_VIEW_TYPE_ITEM = 1

        private val COUPON_DIFF_UTIL = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem) =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem) =
                    oldItem == newItem
        }
    }
}