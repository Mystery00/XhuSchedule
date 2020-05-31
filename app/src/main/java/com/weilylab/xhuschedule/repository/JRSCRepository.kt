/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository

import android.content.Context
import com.jinrishici.sdk.android.JinrishiciClient
import com.jinrishici.sdk.android.factory.JinrishiciFactory
import com.jinrishici.sdk.android.listener.JinrishiciCallback
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException
import com.jinrishici.sdk.android.model.PoetySentence
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import vip.mystery0.logs.Logs

object JRSCRepository {
	private val client = JinrishiciClient.getInstance()
	fun load(context: Context, listener: PoetySentence.() -> Unit) {
		if (!JinrishiciFactory.isInit())
			client.init(context)
		if (ConfigurationUtil.disableJRSC)
			return
		client.getOneSentenceBackground(object : JinrishiciCallback {
			override fun done(poetySentence: PoetySentence?) {
				if (poetySentence != null)
					listener.invoke(poetySentence)
			}

			override fun error(e: JinrishiciRuntimeException?) {
				Logs.wtf("error: ", e)
			}
		})
	}
}