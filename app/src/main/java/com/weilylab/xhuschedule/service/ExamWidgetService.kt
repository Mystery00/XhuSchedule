/*
 * Created by Mystery0 on 17-12-31 下午7:08.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-31 下午5:33
 */

package com.weilylab.xhuschedule.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.util.widget.ExamListRemotesViewsFactory

/**
 * Created by mystery0.
 */
class ExamWidgetService :RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ExamListRemotesViewsFactory(this)
    }
}