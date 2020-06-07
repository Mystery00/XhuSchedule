/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.utils

import vip.mystery0.tools.utils.base64String
import vip.mystery0.tools.utils.deBase64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

fun generateKey(): String {
	val keygen = KeyGenerator.getInstance("AES")
	keygen.init(256)
	return keygen.generateKey().encoded.base64String()
}

fun aesEncrypt(plaintext: String, secretKey: String): String {
	val secretKeySpec = SecretKeySpec(secretKey.deBase64().toByteArray(), "AES")
	val cipher = Cipher.getInstance("AES/ECB/NoPadding")
	cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
	return String(cipher.doFinal(plaintext.toByteArray()))
}

fun aesDecrypt(data: String, secretKey: String): String {
	val secretKeySpec = SecretKeySpec(secretKey.deBase64().toByteArray(), "AES")
	val cipher = Cipher.getInstance("AES/ECB/NoPadding")
	cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
	return String(cipher.doFinal(data.toByteArray()))
}