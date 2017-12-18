/*
 * Created by Mystery0 on 17-12-18 下午2:20.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-18 下午2:20
 */

package com.weilylab.xhuschedule.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.util.widget.ListRemotesViewsFactory

/**
 * Created by mystery0.
 */
class TodayWidgetService:RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ListRemotesViewsFactory(this)
    }
}