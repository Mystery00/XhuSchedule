package com.weilylab.xhuschedule

import android.app.Application
import vip.mystery0.tools.crashHandler.CrashHandler
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
class APP:Application()
{
	override fun onCreate()
	{
		super.onCreate()
		Logs.setLevel(Logs.LogLevel.Debug)
		CrashHandler.getInstance(this)
				.init()
	}
}