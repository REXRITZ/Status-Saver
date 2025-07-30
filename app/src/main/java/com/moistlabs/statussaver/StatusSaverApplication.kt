package com.moistlabs.statussaver

import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.MobileAds
import com.moistlabs.statussaver.data.AppPref
import com.moistlabs.statussaver.util.Utils
import com.zeugmasolutions.localehelper.LocaleAwareApplication
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
class StatusSaverApplication : LocaleAwareApplication() {

    @Inject
    lateinit var appPref: AppPref

    override fun onCreate() {
        super.onCreate()
        setAppTheme(appPref.getMode())
        MobileAds.initialize(applicationContext) {}
        val savedStatusDir = File(Utils.getSavedStatusPath())
        if (!savedStatusDir.exists())
            savedStatusDir.mkdir()
    }

    private fun setAppTheme(pos: Int) {
        AppCompatDelegate.setDefaultNightMode(
            Utils.getAppThemeMode(pos)
        )
    }
}