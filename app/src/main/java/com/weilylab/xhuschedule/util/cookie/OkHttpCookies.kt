package com.weilylab.xhuschedule.util.cookie

import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

import okhttp3.Cookie


class OkHttpCookies(@field:Transient private val cookies: Cookie) : Serializable
{
	@Transient private var clientCookies: Cookie? = null

	fun getCookies(): Cookie
	{
		return clientCookies ?: cookies
	}

	@Throws(IOException::class)
	private fun writeObject(objectOutputStream: ObjectOutputStream)
	{
		objectOutputStream.writeObject(cookies.name())
		objectOutputStream.writeObject(cookies.value())
		objectOutputStream.writeLong(cookies.expiresAt())
		objectOutputStream.writeObject(cookies.domain())
		objectOutputStream.writeObject(cookies.path())
		objectOutputStream.writeBoolean(cookies.secure())
		objectOutputStream.writeBoolean(cookies.httpOnly())
		objectOutputStream.writeBoolean(cookies.hostOnly())
		objectOutputStream.writeBoolean(cookies.persistent())
	}

	@Throws(IOException::class, ClassNotFoundException::class)
	private fun readObject(objectInputStream: ObjectInputStream)
	{
		val name = objectInputStream.readObject() as String
		val value = objectInputStream.readObject() as String
		val expiresAt = objectInputStream.readLong()
		val domain = objectInputStream.readObject() as String
		val path = objectInputStream.readObject() as String
		val secure = objectInputStream.readBoolean()
		val httpOnly = objectInputStream.readBoolean()
		val hostOnly = objectInputStream.readBoolean()
		val persistent = objectInputStream.readBoolean()
		var builder = Cookie.Builder()
		builder = builder.name(name)
		builder = builder.value(value)
		builder = builder.expiresAt(expiresAt)
		builder = if (hostOnly) builder.hostOnlyDomain(domain) else builder.domain(domain)
		builder = builder.path(path)
		builder = if (secure) builder.secure() else builder
		builder = if (httpOnly) builder.httpOnly() else builder
		clientCookies = builder.build()
	}
}