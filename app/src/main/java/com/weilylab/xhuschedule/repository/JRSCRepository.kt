package com.weilylab.xhuschedule.repository

import com.jinrishici.sdk.android.JinrishiciClient
import com.jinrishici.sdk.android.listener.JinrishiciCallback
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException
import com.jinrishici.sdk.android.model.PoetySentence
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import vip.mystery0.logs.Logs

object JRSCRepository {
	private val client = JinrishiciClient()
	fun load(listener: (PoetySentence) -> Unit) {
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