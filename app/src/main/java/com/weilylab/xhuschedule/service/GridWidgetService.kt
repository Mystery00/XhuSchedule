/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-18 下午4:25
 */

package com.weilylab.xhuschedule.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.util.widget.CourseGridRemotesViewsFactory

class GridWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return CourseGridRemotesViewsFactory(this)
    }
}