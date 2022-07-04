package com.example.vpn.ui.settings.support

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.BuildConfig
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.data.source.local.SharedPreferencesDataSource
import com.example.vpn.databinding.FragmentSupportBinding
import com.example.vpn.ui.settings.SettingsFragmentDirections
import javax.inject.Inject


class SupportFragment : BaseFragment(R.layout.fragment_support) {

    @Inject
    lateinit var prefs: SharedPreferencesDataSource

    private val binding by viewBinding(FragmentSupportBinding::bind)
    override val viewModel: SupportFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = SharedPreferencesDataSource(view.context)
        if (prefs.getIsPremium()){
            binding.ivBanner.visibility = View.GONE
        }
        initListeners()
    }

    private fun initListeners() {
        with(binding) {
            ivBanner.setOnClickListener {
                navigate(
                    SupportFragmentDirections.actionSupportFragmentToSubscriptionFragment(
                        true
                    )
                )
            }
            btnBack.setOnClickListener { navigateBack() }
            btnConnectIssues.setOnClickListener { openEmailClient("Connection Issues") }
            btnSlowBrowsing.setOnClickListener { openEmailClient("Slow Browsing") }
            btnElse.setOnClickListener { openEmailClient("Other Issues") }
        }
    }

    private fun openEmailClient(subject: String) {
        val appConfig = "\n\nbuild type : ${BuildConfig.BUILD_TYPE}" +
                "\nversion name: ${BuildConfig.VERSION_NAME}" +
                "\nmanufacturer: ${Build.MANUFACTURER}" +
                "\nbrand: ${Build.BRAND}" +
                "\nmodel: ${Build.MODEL}"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "text/html"
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@cyberself-vpn.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, appConfig)
        }
        startActivity(intent)
    }
}
