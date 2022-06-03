package com.example.vpn.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vpn.common.base.BaseViewModel
import com.example.vpn.data.list.CountryList
import com.example.vpn.domain.model.Country
import com.example.vpn.data.source.remote.api.VpnServerApi
import com.example.vpn.domain.model.ApiServer
import com.example.vpn.domain.result.onError
import com.example.vpn.domain.result.onSuccess
import com.example.vpn.domain.usecase.VpnUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.nio.file.attribute.UserPrincipal
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val useCase: VpnUseCase
) : BaseViewModel() {

    private val _countryList = MutableLiveData<List<Country>>().apply {
        value = CountryList.list
    }

    val countryList: LiveData<List<Country>> = _countryList


    private val _countryListApi = MutableStateFlow<List<ApiServer>>(listOf())

    val countryListApi: StateFlow<List<ApiServer>> = _countryListApi

    private fun setCountryListApi(list: List<ApiServer>) {
        _countryListApi.value = list
    }


    fun setNewCountryList(selectedItem: Country) {
        val newList = mutableListOf<Country>()
            _countryList.value?.forEach{
                if (it.name == selectedItem.name)
                    newList.add(it.copy(isChosen = true))
                else
                    newList.add(it.copy(isChosen = false))
            }

        _countryList.value = newList
    }


    fun getListFromApi() {
        viewModelScope.launch(Dispatchers.IO)  {
            useCase.getServersList()
                .onSuccess { list ->
                    Log.i("MainFragmentViewModel", "Response = $list")
                    try {
                        setCountryListApi(list.articles)
                    }catch (e: Exception){
                        Log.e("MainFragmentViewModel", "exception = $e")
                    }
                }
                .onError { error.emit(it) }
        }
    }
}
