package com.example.davaleba18

import android.content.Context

object SessionManager {

    private const val PREF_NAME = "session_pref"
    private const val KEY_EMAIL = "email"
    private const val KEY_REMEMBER = "remember_me"

    fun saveSession(context: Context, email: String, rememberMe: Boolean){
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_EMAIL,email)
            putBoolean(KEY_REMEMBER, rememberMe)
            apply()
        }
    }

    fun getSession(context: Context):String?{
        val prefs = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
        return if (prefs.getBoolean(KEY_REMEMBER,false)){
            prefs.getString(KEY_EMAIL,null)
        }else  null
    }

    fun clearSession(context: Context){
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }

}