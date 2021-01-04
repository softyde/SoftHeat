package de.annee.softheat.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedModel : ViewModel() {

    val currentTemperature = MutableLiveData<String>()
    val currentHumidity = MutableLiveData<String>()
    val currentBattery = MutableLiveData<String>()
    val currentMode = MutableLiveData<String>()
    val currentTarget = MutableLiveData<Int>()
    val newTarget = MutableLiveData<Int>()

    fun setCurrentTemperature(t: String) {
        currentTemperature.value = t
    }

    fun setCurrentHumidity(t: String) {
        currentHumidity.value = t
    }

    fun setCurrentBattery(t: String) {
        currentBattery.value = t
    }

    fun setCurrentMode(t: String) {
        currentMode.value = t
    }

    fun setCurrentTarget(t: Int) {
        currentTarget.value = t
    }

    fun setNewTarget(t: Int) {
        newTarget.value = t
    }

}