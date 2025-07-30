package com.moistlabs.statussaver.ui.direct_chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moistlabs.statussaver.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DirectChatViewModel @Inject constructor(

): ViewModel() {

    var country: Country = Country.countries[79]
    var number: String = ""
    var message: String = ""

    val uiToggle = MutableLiveData(false)

    fun getPhoneNumber(): String {
        return "${country.isoCode.replace("+","")}$number"
    }

    fun getCountryCodeWithName() = "${country.isoCode} ${country.countryName}"
}