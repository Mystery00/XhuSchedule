/*
 * Created by Mystery0 on 17-12-7 下午9:22.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-7 下午9:22
 */

package com.weilylab.xhuschedule.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.util.widget.GridRemotesViewsFactory

class GridWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return GridRemotesViewsFactory(this)
    }
}