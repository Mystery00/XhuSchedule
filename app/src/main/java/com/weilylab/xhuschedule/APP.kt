package com.weilylab.xhuschedule

import android.app.Application
import android.content.Context
import vip.mystery0.tools.crashHandler.CrashHandler
import vip.mystery0.tools.logs.Logs
import java.io.File

/**
 * Created by myste.
 */
class APP : Application()
{
	companion object
	{
		private var app: APP? = null

		fun getContext(): Context = app!!
	}

	init
	{
		app = this
	}

	override fun onCreate()
	{
		super.onCreate()
		Logs.setLevel(Logs.LogLevel.Debug)
		CrashHandler.getInstance(this)
				.setDirectory(cacheDir.absolutePath + File.separator)
				.init()
	}
}