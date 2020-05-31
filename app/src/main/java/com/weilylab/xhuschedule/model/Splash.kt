/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.model

class Splash {
	var id: Int = 0
	var splashTime: Long = 0
	lateinit var locationUrl: String
	lateinit var splashUrl: String
	var enable: Boolean = false
	lateinit var imageMD5: String
}