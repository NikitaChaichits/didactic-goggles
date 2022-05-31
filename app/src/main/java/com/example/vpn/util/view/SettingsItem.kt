package com.example.vpn.util.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.example.vpn.R
import com.example.vpn.databinding.SettingsItemBinding

class SettingsItem : LinearLayout {

    private val binding = SettingsItemBinding.inflate(LayoutInflater.from(context), this)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attrs?.let { initAttrs(it) }
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingsItem)
        try {
            context.withStyledAttributes(attrs, R.styleable.SettingsItem) {
                binding.tvSettingName.text = getString(R.styleable.SettingsItem_text)
                binding.ivIcon.setImageDrawable(
                    typedArray.getDrawable(R.styleable.SettingsItem_icon)
                )
            }
        } finally {
            typedArray.recycle()
        }
    }
}
