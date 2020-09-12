/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.weilylab.xhuschedule.model.jrsc

import com.google.gson.annotations.SerializedName

class PoetyToken {
	@SerializedName("data")
	lateinit var token: String
}