package com.moistlabs.statussaver.util

import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.moistlabs.statussaver.R


object Utils {
    const val CROSS_FADE_DURATION = 120
    const val WHATSAPP_PATH = "Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
    const val REQUEST_CODE = 121
    const val APP_LINK = "Status saver app link:\nhttps://play.google.com/store/apps/details?id=com.moistlabs.statussaver"
    const val APP_ID = "com.moistlabs.statussaver"
    val languageMap = mapOf(
        Pair(0, Pair("en", R.string.english)),
        Pair(1, Pair("fr", R.string.french)),
        Pair(2, Pair("de", R.string.german)),
        Pair(3, Pair("ja", R.string.japanese)),
        Pair(4, Pair("ko", R.string.korean)),
        Pair(5, Pair("pt", R.string.portuguese)),
        Pair(6, Pair("ru", R.string.russian)),
        Pair(7, Pair("es", R.string.spanish))
    )


    fun getStatusPath() = "${Environment.getExternalStorageDirectory()}/$WHATSAPP_PATH"

    fun getSavedStatusPath() = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/Status Saver"

    fun getAppThemeMode(pos: Int): Int {
        return when(pos) {
            1 -> AppCompatDelegate.MODE_NIGHT_NO
            2 -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    fun getAppThemeText(pos: Int): Int {
        return when(pos) {
            1 -> R.string.light
            2 -> R.string.dark
            else -> R.string.system_default
        }
    }

    fun View.toggleVisibility(show: Boolean) {
        visibility = if(show)
            View.VISIBLE
        else
            View.GONE
    }
}