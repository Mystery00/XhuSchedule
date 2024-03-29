/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.model.response

import org.koin.core.KoinComponent

abstract class CloudResponse : KoinComponent {
    var code: Int = -1
    lateinit var message: String

    val isSuccessful: Boolean
        get() = code == 0
}