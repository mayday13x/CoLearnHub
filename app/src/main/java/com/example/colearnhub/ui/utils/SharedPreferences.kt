package com.example.colearnhub.ui.utils

import android.content.Context
import androidx.core.content.edit

class SharedPreferenceHelper(private val context: Context) {

    companion object{
        private const val MY_PREF_KEY = "MY_PREF"
        const val IS_USER_LOGGED_IN = "IS_USER_LOGGED_IN"
        const val USER_EMAIL = "USER_EMAIL"
        const val ACCESS_TOKEN = "ACCESS_TOKEN"
    }

    fun saveStringData(key: String, data: String?) {
        val sharedPreferences = context.getSharedPreferences(MY_PREF_KEY, Context.MODE_PRIVATE)
        sharedPreferences.edit { putString(key, data) }
    }

    fun getStringData(key: String): String? {
        val sharedPreferences = context.getSharedPreferences(MY_PREF_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    fun saveBooleanData(key: String, data: Boolean) {
        val sharedPreferences = context.getSharedPreferences(MY_PREF_KEY, Context.MODE_PRIVATE)
        sharedPreferences.edit { putBoolean(key, data) }
    }

    fun getBooleanData(key: String, defaultValue: Boolean = false): Boolean {
        val sharedPreferences = context.getSharedPreferences(MY_PREF_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun saveLongData(key: String, data: Long) {
        val sharedPreferences = context.getSharedPreferences(MY_PREF_KEY, Context.MODE_PRIVATE)
        sharedPreferences.edit { putLong(key, data) }
    }

    fun getLongData(key: String, defaultValue: Long = 0L): Long {
        val sharedPreferences = context.getSharedPreferences(MY_PREF_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun clearPreferences() {
        val sharedPreferences = context.getSharedPreferences(MY_PREF_KEY, Context.MODE_PRIVATE)
        sharedPreferences.edit { clear() }
    }
}