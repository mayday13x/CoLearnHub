package com.example.colearnhub.ui.utils

import android.content.Context
import androidx.core.content.edit

class SharedPreferenceHelper(private val context: Context) {

    companion object{
        private const val MY_PREF_KEY = "MY_PREF"
        const val IS_USER_LOGGED_IN = "IS_USER_LOGGED_IN"
        const val USER_EMAIL = "USER_EMAIL"
        const val ACCESS_TOKEN = "ACCESS_TOKEN"
        const val HAS_SEEN_ONBOARDING = "HAS_SEEN_ONBOARDING" // para os intro sliders
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

    // Métodos específicos para onboarding
    fun hasSeenOnboarding(): Boolean {
        return getBooleanData(HAS_SEEN_ONBOARDING, false)
    }

    fun setOnboardingSeen() {
        saveBooleanData(HAS_SEEN_ONBOARDING, true)
    }

    // Métodos específicos para login (para facilitar o uso)
    fun isUserLoggedIn(): Boolean {
        return getBooleanData(IS_USER_LOGGED_IN, false)
    }

    fun setUserLoggedIn(isLoggedIn: Boolean) {
        saveBooleanData(IS_USER_LOGGED_IN, isLoggedIn)
    }
}