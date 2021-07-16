package com.SynchronossTask.utils

import android.content.Context
import android.content.SharedPreferences

class MyPreference(private val context: Context) {
    private val PREFS_FILE = context.packageName
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    fun clear() {
        prefs.edit().clear().apply()
    }

    operator fun contains(key: String?): Boolean {
        return prefs.contains(key)
    }

    fun remove(key: String?) {
        prefs!!.edit().remove(key).apply()
    }

    fun putInt(key: String?, value: Int) {
        if (prefs != null) {
            prefs!!.edit().putInt(key, value).apply()
        }
    }

    fun getInt(key: String?): Int? {
        return if (prefs != null && prefs!!.contains(key)) {
            prefs!!.getInt(key, 0)
        } else null
    }

    fun putFloat(key: String?, value: Float) {
        if (prefs != null) {
            prefs!!.edit().putFloat(key, value).apply()
        }
    }

    fun getFloat(key: String?): Float? {
        return if (prefs != null && prefs!!.contains(key)) {
            prefs!!.getFloat(key, 0f)
        } else null
    }

    fun putLong(key: String?, value: Long) {
        if (prefs != null) {
            prefs!!.edit().putLong(key, value).apply()
        }
    }

    fun getLong(key: String?, vararg defValue: Long): Long? {
        return if (prefs != null && prefs!!.contains(key)) {
            prefs!!.getLong(key, 0)
        } else null
    }

    fun putString(key: String?, value: String?) {
        if (prefs != null) {
            prefs!!.edit().putString(key, value).apply()
        }
    }

    fun getString(key: String?): String? {
        return if (prefs != null && prefs!!.contains(key)) {
            prefs!!.getString(key, null)
        } else null
    }

    fun putBoolean(key: String?, value: Boolean) {
        if (prefs != null) {
            prefs!!.edit().putBoolean(key, value).apply()
        }
    }

    fun getBoolean(key: String?): Boolean? {
        return if (prefs != null && prefs!!.contains(key)) {
            prefs!!.getBoolean(key, false)
        } else null
    }
}