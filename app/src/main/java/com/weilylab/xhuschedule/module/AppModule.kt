/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.module

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
	single { androidContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

	single { LocalBroadcastManager.getInstance(androidContext()) }

	single { androidContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }

	single { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

	single { EventBus.getDefault() }
}