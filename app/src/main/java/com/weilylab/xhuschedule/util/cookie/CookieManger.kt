package com.weilylab.xhuschedule.util.cookie

import android.content.Context

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CookieManger(context: Context) : CookieJar {
    init {
        if (cookieStore == null)
            cookieStore = PersistentCookieStore(context)
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isNotEmpty())
            for (item in cookies)
                cookieStore!!.add(url, item)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> = cookieStore!![url]

    companion object {
        private var cookieStore: PersistentCookieStore? = null
    }

}