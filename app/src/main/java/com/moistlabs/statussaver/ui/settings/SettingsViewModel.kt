package com.moistlabs.statussaver.ui.settings

import androidx.lifecycle.ViewModel
import com.moistlabs.statussaver.data.AppPref
import com.moistlabs.statussaver.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPref: AppPref,
): ViewModel() {

    var mode: Int
    var selectedLanguage: Int

    init {
        mode = Utils.getAppThemeText(appPref.getMode())
        selectedLanguage = Utils.languageMap[appPref.getLanguage()]!!.second
    }

    fun setTheme(pos: Int) {
        mode = Utils.getAppThemeText(pos)
        appPref.setMode(pos)
    }

    fun setLanguage(pos: Int) {
        selectedLanguage = Utils.languageMap[pos]!!.second
        appPref.setLanguage(pos)
    }
}
