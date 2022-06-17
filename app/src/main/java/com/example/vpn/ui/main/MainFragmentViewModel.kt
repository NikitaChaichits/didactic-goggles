package com.example.vpn.ui.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.vpn.common.base.BaseViewModel
import com.example.vpn.domain.model.Country
import com.example.vpn.domain.result.onError
import com.example.vpn.domain.result.onSuccess
import com.example.vpn.domain.usecase.VpnUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val useCase: VpnUseCase
) : BaseViewModel() {

    private val _countryList = MutableStateFlow<List<Country>>(listOf())
    val countryList: StateFlow<List<Country>> = _countryList.asStateFlow()

    private val _serverConfig = MutableStateFlow("")
    val serverConfig: StateFlow<String> = _serverConfig.asStateFlow()

    private val _country = MutableStateFlow("")
    val country: StateFlow<String> = _country.asStateFlow()

    private val _vpnStart = MutableStateFlow(false)
    val vpnStart: StateFlow<Boolean> = _vpnStart.asStateFlow()

    private val _status = MutableStateFlow("")
    val status : StateFlow<String> = _status.asStateFlow()


    fun setNewCountryList(selectedItem: Country) {
        val newList = mutableListOf<Country>()
            _countryList.value.forEach{
                if (it.shortName == selectedItem.shortName){
                    newList.add(it.copy(isChosen = true))
                    getServerConfig(it.ip)
                } else {
                    newList.add(it.copy(isChosen = false))
                }
            }

        _countryList.value = newList
        _country.value = selectedItem.fullName
    }

    fun getListFromApi(chosenCountryName: String) {
        viewModelScope.launch(Dispatchers.IO)  {
            useCase.getServersList()
                .onSuccess { list ->
                    Log.d("MainFragmentViewModel", "Response = $list")
                    try {
                        val countryList = mutableListOf<Country>()
                        list.forEach{
                            if (chosenCountryName == it.info.country){
                                val chosenCountry = it.mapToCountry().copy(isChosen = true)
                                if (it.info.country == "US")
                                    countryList.add(0, chosenCountry)
                                else
                                    countryList.add(chosenCountry)
                                getServerConfig(it.ip)
                                _country.value = chosenCountry.fullName
                            }else{
                                if (it.info.country == "US")
                                    countryList.add(0, it.mapToCountry())
                                else
                                    countryList.add(it.mapToCountry())
                            }
                        }
                        _countryList.emit(countryList)
                    }catch (e: Exception){
                        Log.e("MainFragmentViewModel", "exception = $e")
                    }
                }
                .onError {
                    error.emit(it)
                    Log.e("MainFragmentViewModel", it.toString())
                }
        }
    }

    private fun getServerConfig(serverIp: String) {
         viewModelScope.launch(Dispatchers.IO)  {
            useCase.getServerConfig(serverIp)
                .onSuccess { configResponse ->
                    _serverConfig.value = configResponse.body() ?: ""
                }
                .onError {
                    error.emit(it)
                    Log.e("MainFragmentViewModel", it.toString())
                }
        }
    }

    fun setVpnStart(isStart: Boolean){
        _vpnStart.value = isStart
    }

    fun setStatus(status: String) {
        _status.value = status
    }
}
