package com.cyberself.vpn.ui.settings.speedtest

import com.cyberself.vpn.common.base.BaseViewModel
import com.cyberself.vpn.ui.settings.speedtest.State.INITIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SpeedTestViewModel @Inject constructor() : BaseViewModel() {

    private val _state = MutableStateFlow(INITIAL.name)
    val state : StateFlow<String> = _state


    fun setState(state: String) {
        _state.value = state
    }
}
