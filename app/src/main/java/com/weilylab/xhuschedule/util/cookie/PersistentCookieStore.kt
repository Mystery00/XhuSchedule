package com.weilylab.xhuschedule.util.cookie

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.ArrayList
import java.util.HashMap
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

import okhttp3.Cookie
import okhttp3.HttpUrl
import vip.mystery0.tools.logs.Logs
import kotlin.experimental.and

open class PersistentCookieStore(context: Context) {
    private val cookies = HashMap<String, ConcurrentHashMap<String, Cookie>>()
    private val cookiePreferences: SharedPreferences

    init {
        cookiePreferences = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE)

        //将持久化的cookies缓存到内存中 即map cookies
        val map = cookiePreferences.all
        for ((key, value) in map) {
            val cookieNames = TextUtils.split(value as String, ",")
            for (name in cookieNames) {
                val encodedCookie = cookiePreferences.getString(name, null)
                if (encodedCookie != null) {
                    val decodedCookie = decodeCookie(encodedCookie)
                    if (decodedCookie != null) {
                        if (!cookies.containsKey(key))
                            cookies.put(key, ConcurrentHashMap())
                        cookies[key]?.put(name, decodedCookie)
                    }
                }
            }
        }
    }

    private fun getCookieToken(cookie: Cookie): String = cookie.name() + "@" + cookie.domain()

    fun add(url: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)

        //将cookies缓存到内存中 如果缓存过期 就重置此cookie
        if (!cookie.persistent()) {
            if (!cookies.containsKey(url.host()))
                cookies.put(url.host(), ConcurrentHashMap())
            cookies[url.host()]?.put(name, cookie)
        } else {
            if (cookies.containsKey(url.host()))
                cookies[url.host()]?.remove(name)
        }

        //讲cookies持久化到本地
        val prefsWriter = cookiePreferences.edit()
        prefsWriter.putString(url.host(), TextUtils.join(",", cookies[url.host()]?.keys))
        prefsWriter.putString(name, encodeCookie(OkHttpCookies(cookie)))
        prefsWriter.apply()
    }

    operator fun get(url: HttpUrl): List<Cookie> {
        val arrayList = ArrayList<Cookie>()
        if (cookies.containsKey(url.host()))
            arrayList.addAll(cookies[url.host()]!!.values)
        return arrayList
    }

    fun removeAll(): Boolean {
        val prefsWriter = cookiePreferences.edit()
        prefsWriter.clear()
        prefsWriter.apply()
        cookies.clear()
        return true
    }

    fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        val name = getCookieToken(cookie)
        if (cookies.containsKey(url.host()) && cookies[url.host()]!!.containsKey(name)) {
            cookies[url.host()]!!.remove(name)
            val prefsWriter = cookiePreferences.edit()
            if (cookiePreferences.contains(name))
                prefsWriter.remove(name)
            prefsWriter.putString(url.host(), TextUtils.join(",", cookies[url.host()]!!.keys))
            prefsWriter.apply()

            return true
        }
        return false
    }

    fun getCookies(): List<Cookie> {
        val arrayList = ArrayList<Cookie>()
        for (key in cookies.keys)
            arrayList.addAll(cookies[key]!!.values)
        return arrayList
    }

    /**
     * cookies 序列化成 string
     *
     * @param cookie 要序列化的cookie
     * @return 序列化之后的string
     */
    private fun encodeCookie(cookie: OkHttpCookies?): String? {
        if (cookie == null)
            return null
        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            val outputStream = ObjectOutputStream(byteArrayOutputStream)
            outputStream.writeObject(cookie)
        } catch (e: IOException) {
            Logs.wtf(TAG, "IOException in encodeCookie", e)
            return null
        }

        return byteArrayToHexString(byteArrayOutputStream.toByteArray())
    }

    /**
     * 将字符串反序列化成cookies
     *
     * @param cookieString cookies string
     * @return cookie object
     */
    private fun decodeCookie(cookieString: String): Cookie? {
        val bytes = hexStringToByteArray(cookieString)
        val byteArrayInputStream = ByteArrayInputStream(bytes)
        var cookie: Cookie? = null
        try {
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            cookie = (objectInputStream.readObject() as OkHttpCookies).getCookies()
        } catch (e: IOException) {
            Logs.wtf(TAG, "IOException in decodeCookie", e)
        } catch (e: ClassNotFoundException) {
            Logs.wtf(TAG, "ClassNotFoundException in decodeCookie", e)
        }

        return cookie
    }

    /**
     * 二进制数组转十六进制字符串
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    private fun byteArrayToHexString(bytes: ByteArray): String {
        val stringBuilder = StringBuilder(bytes.size * 2)
        for (element in bytes) {
            val v = (element.toInt() and 0xff)
            if (v < 16) {
                stringBuilder.append('0')
            }
            stringBuilder.append(Integer.toHexString(v))
        }
        return stringBuilder.toString().toUpperCase(Locale.US)
    }

    /**
     * 十六进制字符串转二进制数组
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    private fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    companion object {
        private val TAG = "PersistentCookieStore"
        private val COOKIE_PREFS = "Cookies_Prefs"
    }
}