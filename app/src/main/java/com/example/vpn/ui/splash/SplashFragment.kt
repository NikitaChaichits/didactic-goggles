package com.example.vpn.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.data.source.local.SharedPreferencesDataSource
import com.example.vpn.util.time.handleWithDelay
import javax.inject.Inject

class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    override val viewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var prefs: SharedPreferencesDataSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = SharedPreferencesDataSource(view.context)
        handleWithDelay(3000L) { checkFirstLaunch() }
    }

    private fun checkFirstLaunch() {
        if (prefs.getFirstLaunch()) {
            navigate(R.id.action_splash_fragment_to_privacy_fragment)
            prefs.setFirstLaunch(false)
        } else {
            navigate(R.id.action_splash_fragment_to_main_fragment)
        }
    }

}
