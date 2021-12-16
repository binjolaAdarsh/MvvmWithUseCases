package com.master.coupon.presentation.util

import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.master.coupon.R

const val APPLICABLE_COUPONS = "APPLICABLE COUPONS"
const val UNLOCK_COUPONS = "UNLOCK COUPONS"


@BindingAdapter(value = ["setImageUrl"])
fun AppCompatImageView.bindImageUrl(url: String?) {
// todo need to change the glide way
     fun requestOptionsWithPlaceHolder(
        placeholderDrawable: Int,
        errorDrawable: Int,
    ): RequestOptions {
        var requestOptions: RequestOptions = RequestOptions().timeout(10000)
        requestOptions = requestOptions.placeholder(placeholderDrawable).error(errorDrawable)
        return requestOptions
    }
    Glide.with(this)
        .setDefaultRequestOptions(requestOptionsWithPlaceHolder(R.drawable.ic_coupon_default, R.drawable.ic_coupon_default))
        .load(url)
        .into(this)
}


@BindingAdapter(value = ["addTermsConditionBullets"])
fun LinearLayout.addTermsConditionBullets(listOfTerms: List<String>?) {
    removeAllViews()
    listOfTerms?.forEachIndexed { index, it ->
        val termsLayout = LayoutInflater.from(this.context).inflate(R.layout.item_terms, null)
        termsLayout.findViewById<TextView>(R.id.tvTerm).text = it
        this.addView(termsLayout, index)
    }

}

@BindingAdapter(value = ["setAdapter"])
fun RecyclerView.bindRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>) {
    this.run {
        this.setHasFixedSize(true)
        this.adapter = adapter
    }
}

fun setCopiedToClipboard(context: Context?, textToBeCopied: String?, toastMessage: String?) {
    if (context != null && !TextUtils.isEmpty(textToBeCopied)) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", textToBeCopied)
        clipboard.setPrimaryClip(clip)
        if (!TextUtils.isEmpty(toastMessage)) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }
}

fun getLoadingDialog(context: Context, message: String?): ProgressDialog {
    val pd = ProgressDialog(ContextThemeWrapper(context, android.R.style.Theme_Holo_Light_Dialog))
//    pd.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progress_loader_without_bg))
    pd.setMessage(message)
    pd.setCancelable(false)
    return pd
}

internal fun EditText.onDone(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            return@setOnEditorActionListener true
        }
        false
    }
}

inline fun <reified T : Enum<T>> Bundle.getEnum(key: String, default: T) =
        getInt(key).let { if (it >= 0) enumValues<T>()[it] else default }
@Suppress("unused")
fun <T : Enum<T>> Bundle.putEnum(key: String, value: T?) =
        putInt(key, value?.ordinal ?: -1)

fun hideKeyboard(context: Context, view: View?) {
    try {
        if (view != null) {
            val info = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            info.hideSoftInputFromWindow(view.windowToken, 0)
        }
    } catch (e: Exception) {
        //silent catch
    }
}