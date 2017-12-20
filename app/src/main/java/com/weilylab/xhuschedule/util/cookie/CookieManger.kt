/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-30 上午11:04
 */

package com.weilylab.xhuschedule.util.cookie

import android.content.Context
import com.weilylab.xhuschedule.APP

object CookieManger {
    private val COOKIE_PREFS = "cookies"
    private val cookiePreferences = APP.getContext().getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE)

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

    private fun getCookieToken(username: String, host: String): String = username + '@' + host
}