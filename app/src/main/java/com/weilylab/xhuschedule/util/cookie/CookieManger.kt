package com.weilylab.xhuschedule.util.cookie

import android.content.Context

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import vip.mystery0.tools.logs.Logs

class CookieManger(context: Context) : CookieJar {
    init {
        if (cookieStore == null)
            cookieStore = PersistentCookieStore(context)
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val username = url.queryParameter("username")
        if (cookies.isNotEmpty() && username != null) {
            for (item in cookies)
                cookieStore!!.add(username, item)
        }

    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val username = url.queryParameter("username")
        return if (username != null)
            cookieStore!![username]
        else
            ArrayList()
    }

    companion object {
        private var cookieStore: PersistentCookieStore? = null
    }

}