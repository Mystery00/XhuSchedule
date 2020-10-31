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
import java.security.SecureRandom
import javax.crypto.KeyGenerator

private const val KEY_ALGORITHM = "AES"

private fun encryptBASE64(key: ByteArray): String = Base64.encodeToString(key, Base64.DEFAULT)

fun generateSeed(): String {
    val secureRandom = SecureRandom()
    val keygen = KeyGenerator.getInstance(KEY_ALGORITHM)
    keygen.init(secureRandom)
    return encryptBASE64(keygen.generateKey().encoded)
}