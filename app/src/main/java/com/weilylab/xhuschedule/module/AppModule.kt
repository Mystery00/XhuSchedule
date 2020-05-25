package com.weilylab.xhuschedule.module

import android.content.ClipboardManager
import android.content.Context
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
	single { androidContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

	single { EventBus.getDefault() }
}