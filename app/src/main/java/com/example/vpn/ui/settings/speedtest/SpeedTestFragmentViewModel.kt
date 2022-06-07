package com.example.vpn.ui.settings.speedtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vpn.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.vpn.ui.settings.speedtest.State.*

@HiltViewModel
class SpeedTestFragmentViewModel @Inject constructor() : BaseViewModel() {

    private val _state = MutableLiveData<String>().apply{
        value = INITIAL.value
    }

    val state : LiveData<String> = _state


    fun setState(state: String) {
        _state.value = state
    }
}
