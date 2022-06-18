package com.example.vpn.util.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.example.vpn.R
import com.example.vpn.databinding.ItemSubscriptionBinding

class Subscription : LinearLayout {

    private val binding = ItemSubscriptionBinding.inflate(LayoutInflater.from(context), this)

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        context.withStyledAttributes(attrs, R.styleable.Subscription) {
            val title = getString(R.styleable.Subscription_name)
            val value = getString(R.styleable.Subscription_description)
            val isChecked = getBoolean(R.styleable.Subscription_isChecked, false)

            initView(title, value, isChecked)
        }
    }

    private fun initView(
        name: String? = null,
        description: String? = null,
        isChecked: Boolean = false,
    ) {
        setText(name, description)
        setCheckedStyle(isChecked)
    }

    private fun setText(name: String?, description: String?) {
        binding.tvSubscriptionName.text = name
        binding.tvSubscriptionDescription.text = description
    }

    fun setCheckedStyle(isChecked: Boolean) {
        if (isChecked) {
            binding.ivChecked.visible()
            binding.clSubscription.background = ResourcesCompat.getDrawable(
                resources, R.drawable.bg_checked_subscription, null
            )
        } else {
            binding.ivChecked.gone()
            binding.clSubscription.background = null
        }
    }
}
