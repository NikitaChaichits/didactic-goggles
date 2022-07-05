package com.cyberself.vpn.ui.connection

import com.cyberself.vpn.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.cyberself.vpn.ui.connection.State.*
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class AdActivityViewModel @Inject constructor() : BaseViewModel() {

    private val _state = MutableStateFlow(INITIAL.name)
    val state : StateFlow<String> = _state

    fun setState(state: String) {
        _state.value = state
    }
}
