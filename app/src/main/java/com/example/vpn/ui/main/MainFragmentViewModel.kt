package com.example.vpn.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vpn.common.base.BaseViewModel
import com.example.vpn.data.list.CountryList
import com.example.vpn.data.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor() : BaseViewModel() {

    private val _countryList = MutableLiveData<List<Country>>().apply {
        value = CountryList.list
    }

    val countryList: LiveData<List<Country>> = _countryList

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
}
