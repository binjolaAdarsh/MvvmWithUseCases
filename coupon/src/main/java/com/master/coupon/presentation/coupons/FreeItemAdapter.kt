package com.master.coupon.presentation.coupons

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.master.coupon.R
import com.master.coupon.databinding.FreeItemsLayoutBinding
import com.master.coupon.domain.model.FreeBies

class FreeItemAdapter(private val clickListener: (FreeBies) -> Unit) : ListAdapter<FreeBies, CouponDetailViewHolder>(
    FreeItemDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponDetailViewHolder =
        CouponDetailViewHolder.from(parent)

    override fun onBindViewHolder(holder: CouponDetailViewHolder, position: Int) {
        val freeItem = getItem(position)
        holder.bind(freeItem, clickListener)
    }
}


class CouponDetailViewHolder(val binding: FreeItemsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(freeItemDto: FreeBies, clickListener: (FreeBies) -> Unit) {
        binding.root.setOnClickListener {
            clickListener.invoke(freeItemDto)
        }

        binding.tvItemName.text = freeItemDto.name

        if (freeItemDto.rating == null)
            binding.tvRating.visibility = View.GONE
        else
        binding.tvRating.text = SpannableStringBuilder()
                .append(freeItemDto.rating.toString() )
                .color(ContextCompat.getColor(binding.root.context, R.color.coupon_apply_text_color_disables)) {
                    append("/5")
                }


        binding.tvPrice.text = SpannableStringBuilder()
                .color(ContextCompat.getColor(binding.root.context, R.color.coupon_apply_green)) {
                    append(" FREE")
                }

        // todo need to change the glide way
        fun requestOptionsWithPlaceHolder(
            placeholderDrawable: Int,
            errorDrawable: Int,
        ): RequestOptions {
            var requestOptions: RequestOptions = RequestOptions().timeout(10000)
            requestOptions = requestOptions.placeholder(placeholderDrawable).error(errorDrawable)
            return requestOptions
        }
        Glide.with(binding.imageView.context)
            .setDefaultRequestOptions(requestOptionsWithPlaceHolder(R.drawable.ic_coupon_default, R.drawable.ic_coupon_default))
            .load(freeItemDto.imageUrl)
            .into(binding.imageView)

    }

    companion object {
        fun from(parent: ViewGroup): CouponDetailViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = FreeItemsLayoutBinding.inflate(layoutInflater, parent, false)
            return CouponDetailViewHolder(binding)
        }
    }
}


private class FreeItemDiffUtil : DiffUtil.ItemCallback<FreeBies>() {
    override fun areItemsTheSame(oldItemDto: FreeBies, newItemDto: FreeBies) =
            oldItemDto.name == newItemDto.name

    override fun areContentsTheSame(oldItemDto: FreeBies, newItemDto: FreeBies) =
            oldItemDto == newItemDto

}
