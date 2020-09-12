/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.jrsc.PoetySentence
import com.weilylab.xhuschedule.model.jrsc.PoetyToken
import retrofit2.http.GET

interface JinrishiciAPI {
	@GET("/token")
	suspend fun getToken(): PoetyToken

	@GET("/one.json")
	suspend fun getSentence(): PoetySentence
}