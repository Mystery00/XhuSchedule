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
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by shoewann on 16-9-23.
 */
object AESUtils {
	private const val KEY_ALGORITHM = "AES"
	private const val ALGORITHM = "AES/CBC/PKCS5PADDING"

	private fun decryptBASE64(key: String): ByteArray = Base64.decode(key, Base64.DEFAULT)

	private fun encryptBASE64(key: ByteArray): String = Base64.encodeToString(key, Base64.DEFAULT)

	fun aesEncrypt(seed: String, cleartext: String): String {
		val rawKey = deriveKeyInsecurely(seed, 32).encoded
		val result = encrypt(rawKey, cleartext.toByteArray())
		return encryptBASE64(result)
	}

	fun aesDecrypt(seed: String, encrypted: String): String {
		val rawKey = deriveKeyInsecurely(seed, 32).encoded
		val enc = decryptBASE64(encrypted)
		val result = decrypt(rawKey, enc)
		return String(result)
	}

	private fun deriveKeyInsecurely(password: String, keySizeInBytes: Int): SecretKey {
		val passwordBytes = password.toByteArray(StandardCharsets.US_ASCII)
		return SecretKeySpec(
				InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(
						passwordBytes, keySizeInBytes),
				KEY_ALGORITHM)
	}

	private fun encrypt(raw: ByteArray, clear: ByteArray): ByteArray {
		val skeySpec = SecretKeySpec(raw, KEY_ALGORITHM)
		val cipher = Cipher.getInstance(ALGORITHM)
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, IvParameterSpec(ByteArray(cipher.blockSize)))
		return cipher.doFinal(clear)
	}

	private fun decrypt(raw: ByteArray, encrypted: ByteArray): ByteArray {
		val skeySpec = SecretKeySpec(raw, KEY_ALGORITHM)
		val cipher = Cipher.getInstance(ALGORITHM)
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, IvParameterSpec(ByteArray(cipher.blockSize)))
		return cipher.doFinal(encrypted)
	}
}