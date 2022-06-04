package com.example.vpn.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vpn.common.base.BaseViewModel
import com.example.vpn.domain.model.Country
import com.example.vpn.domain.result.onError
import com.example.vpn.domain.result.onSuccess
import com.example.vpn.domain.usecase.VpnUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val useCase: VpnUseCase
) : BaseViewModel() {

    private val _countryList = MutableLiveData<List<Country>>().apply {
        value = listOf()
    }

    val countryList: LiveData<List<Country>> = _countryList

    private val _serverConfig = MutableLiveData<String>().apply {
        value = ""
    }

    val serverConfig: LiveData<String> = _serverConfig


    fun setNewCountryList(selectedItem: Country) {
        val newList = mutableListOf<Country>()
            _countryList.value?.forEach{
                if (it.shortName == selectedItem.shortName){
                    newList.add(it.copy(isChosen = true))
                    getServerConfig(it.ip)
                } else {
                    newList.add(it.copy(isChosen = false))
                }
            }

        _countryList.value = newList
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
                                countryList.add(chosenCountry)
                                getServerConfig(it.ip)
                            }else{
                                countryList.add(it.mapToCountry())
                            }
                        }
                        _countryList.postValue(countryList)
                    }catch (e: Exception){
                        Log.e("MainFragmentViewModel", "exception = $e")
                    }
                }
                .onError {
                    error.emit(it)
                    Log.e("MainFragmentViewModel", it.toString())
                    getServerConfig("165.227.117.4")
                }
        }
    }

    private fun getServerConfig(serverIp: String) {
        viewModelScope.launch(Dispatchers.IO)  {
            useCase.getServerConfig(serverIp)
                .onSuccess { serverConfig ->
                    Log.d("MainFragmentViewModel", "ServerConfig = ${serverConfig.execute().message()}")
                    try {
                        _serverConfig.postValue(serverConfig.execute().message())
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
}
