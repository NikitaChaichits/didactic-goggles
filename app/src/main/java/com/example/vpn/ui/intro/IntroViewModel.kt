package com.example.vpn.ui.intro

import com.example.vpn.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor() : BaseViewModel() {

    private var isFirstClick = true

    val goToNextScreen = Event<Unit>()
    val showSecondIntroScreen = Event<Unit>()

    fun onBtnContinueClick() {
        if (isFirstClick) {
            showSecondIntroScreen.call()
            isFirstClick = false
        } else {
            goToNextScreen.call()
        }
    }

}
