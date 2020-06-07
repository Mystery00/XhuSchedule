/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.utils

import android.util.Base64
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

private const val KEY_ALGORITHM = "AES"
private const val ALGORITHM = "AES/CBC/PKCS5PADDING"

private fun decryptBASE64(key: String): ByteArray = Base64.decode(key, Base64.DEFAULT)

private fun encryptBASE64(key: ByteArray): String = Base64.encodeToString(key, Base64.DEFAULT)

private fun toKey(key: ByteArray): Key = SecretKeySpec(key, KEY_ALGORITHM)

fun generateKey(): String {
	val secureRandom = SecureRandom()
	val keygen = KeyGenerator.getInstance(KEY_ALGORITHM)
	keygen.init(secureRandom)
	return encryptBASE64(keygen.generateKey().encoded)
}

fun aesDecrypt(data: String, key: String): String {
	val k: Key = toKey(decryptBASE64(key))
	val cipher = Cipher.getInstance(ALGORITHM)
	cipher.init(Cipher.DECRYPT_MODE, k)
	return String(cipher.doFinal(decryptBASE64(data)))
}

fun aesEncrypt(data: String, key: String): String {
	val k: Key = toKey(decryptBASE64(key))
	val cipher = Cipher.getInstance(ALGORITHM)
	cipher.init(Cipher.ENCRYPT_MODE, k)
	return encryptBASE64(cipher.doFinal(data.toByteArray()))
}