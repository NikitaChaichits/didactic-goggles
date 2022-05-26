package com.example.vpn.util.view

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.vpn.R
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory as DCFF

val glideFactory: DCFF = DCFF.Builder().setCrossFadeEnabled(true).build()
val glideTransitionOption = DrawableTransitionOptions.withCrossFade(glideFactory)
val glideCircleTransformOption = RequestOptions().apply(RequestOptions.circleCropTransform())


fun ImageView.setCircleImage(
    uri: String?,
    @DrawableRes placeholderRes: Int,
    @DrawableRes fallbackRes: Int,
    requestListener: RequestListener<Drawable>? = null
) {
    Glide.with(this)
        .load(uri)
        .apply(glideCircleTransformOption)
        .error(fallbackRes)
        .apply { if (requestListener != null) addListener(requestListener) }
        .placeholder(placeholderRes)
        .transition(glideTransitionOption)
        .into(this)
}

fun ImageView.setImageWithRadius(
    uri: String?,
    @DimenRes radiusDimen: Int = R.dimen.common_radius
) {
    val radius: Int = context.resources.getDimension(radiusDimen).toInt()
    Glide.with(context)
        .load(uri)
        .transform(CenterCrop(), RoundedCorners(radius))
        .transition(glideTransitionOption)
        .into(this)
}

open class RequestListenerAdapter(
    private val onSuccess: () -> Unit = {},
    private val onFail: () -> Unit = {}
) : RequestListener<Drawable> {

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        onFail()
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        onSuccess()
        return false
    }
}
