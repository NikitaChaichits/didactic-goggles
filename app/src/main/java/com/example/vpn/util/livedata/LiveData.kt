package com.example.vpn.util.livedata

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<Event<T>>.postEventValue(value: T) {
    this.postValue(Event(value))
}

fun <T> MutableLiveData<Event<T>>.setEventValue(value: T) {
    this.value = Event(value)
}
