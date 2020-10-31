/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.config

import android.content.Context
import androidx.core.content.edit

object JRSCConfig {
    private val sp by lazy { APP.context.getSharedPreferences("jinrishici", Context.MODE_PRIVATE) }

    var token: String?
        set(value) = sp.edit {
            putString("key_token", value)
        }
        get() = sp.getString("key_token", null)
}