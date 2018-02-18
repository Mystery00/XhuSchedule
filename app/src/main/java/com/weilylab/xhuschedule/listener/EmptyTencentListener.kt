/*
 * Created by Mystery0 on 18-2-18 下午11:09.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-2-18 下午11:09
 */

package com.weilylab.xhuschedule.listener

import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError
import vip.mystery0.tools.logs.Logs

class EmptyTencentListener:IUiListener {
    private val TAG = "EmptyTencentListener"
    override fun onComplete(p0: Any?) {
        Logs.i(TAG, "onComplete: ")
    }

    override fun onCancel() {
        Logs.i(TAG, "onCancel: ")
    }

    override fun onError(p0: UiError?) {
        Logs.i(TAG, "onError: ")
    }
}