package com.moistlabs.statussaver.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import javax.inject.Inject

class AppPref @Inject constructor(
    context: Context
) {

    private val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
    }

    fun getPath(): Uri {
        return Uri.parse(preferences.getString("path",""))
    }

    fun getMode(): Int {
        return preferences.getInt("mode", 0)
    }

    fun getLanguage(): Int {
        return preferences.getInt("lang", 0)
    }

    fun setPath(path: String) {
        preferences.edit().apply {
            putString("path", path)
            apply()
        }
    }

    fun setMode(mode: Int) {
        preferences.edit().apply {
            putInt("mode", mode)
            apply()
        }
    }

    fun setLanguage(lang: Int) {
        preferences.edit().apply {
            putInt("lang", lang)
            apply()
        }
    }
}