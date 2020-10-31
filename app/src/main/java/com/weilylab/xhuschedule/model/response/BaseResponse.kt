/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.model.response

import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import org.koin.core.KoinComponent

abstract class BaseResponse : KoinComponent {
    lateinit var msg: String
    lateinit var rt: String

    val isSuccessful: Boolean get() = rt == ResponseCodeConstants.DONE
}