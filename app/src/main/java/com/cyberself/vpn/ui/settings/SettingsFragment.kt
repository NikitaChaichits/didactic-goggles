package com.cyberself.vpn.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cyberself.vpn.R
import com.cyberself.vpn.common.base.BaseFragment
import com.cyberself.vpn.data.source.local.SharedPreferencesDataSource
import com.cyberself.vpn.databinding.FragmentSettingsBinding
import com.cyberself.vpn.ui.connection.AdActivity
import com.cyberself.vpn.util.view.invisible
import com.cyberself.vpn.util.view.visible
import javax.inject.Inject

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    @Inject
    lateinit var prefs: SharedPreferencesDataSource

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    override val viewModel: SettingsFragmentViewModel by viewModels()

    private var isWebViewVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = SharedPreferencesDataSource(view.context)
        if (prefs.getIsPremium()){
            binding.ivBanner.visibility = View.GONE
        }
        initListeners()
        onBackPressed()
    }

    private fun onBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isWebViewVisible) {
                        binding.webView.invisible()
                        isWebViewVisible = false
                    }
                }
            })
    }

    private fun initListeners() {
        with(binding) {
            btnBack.setOnClickListener { navigateBack() }
            ivBanner.setOnClickListener {
                navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToSubscriptionFragment(
                        true
                    )
                )
            }
            btnCheckConnection.setOnClickListener {
                navigate(R.id.action_settings_fragment_to_speed_test_fragment)
            }
            btnContactSupport.setOnClickListener {
                navigate(R.id.action_settings_fragment_to_support_fragment)
            }
            btnRateApp.setOnClickListener {
                val intent = Intent(requireContext(), AdActivity::class.java)
                activity?.startActivity(intent)
            }
            btnPrivacyPolicy.setOnClickListener {
                openWebView("https://cyberself-vpn.com/policy.html")
            }
            btnTerms.setOnClickListener {
                openWebView("https://cyberself-vpn.com/terms.html")
            }
        }
    }

    private fun openWebView(url: String) {
        binding.webView.run {
            webViewClient = WebViewClient()
            loadUrl(url)
            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
            visible()
            isWebViewVisible = true
        }
    }
}
