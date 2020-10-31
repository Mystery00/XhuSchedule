/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.interceptor

import android.content.Context
import com.weilylab.xhuschedule.config.APP

object CookieManger {
    private const val COOKIE_PREFS = "cookies"
    private val cookiePreferences by lazy { APP.context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE) }

    fun putCookie(username: String, host: String, cookie: String?) {
        val name = getCookieToken(username, host)
        cookiePreferences.edit()
                .putString(name, cookie)
                .apply()
    }

    fun getCookie(username: String, host: String): String? {
        val name = getCookieToken(username, host)

        return cookiePreferences.getString(name, null)
    }

    private fun getCookieToken(username: String, host: String): String = "$username@$host"
}