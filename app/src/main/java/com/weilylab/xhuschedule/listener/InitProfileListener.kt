/*
 * Created by Mystery0 on 17-12-26 下午8:59.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-26 下午8:59
 */

package com.weilylab.xhuschedule.listener

import android.app.Dialog

interface InitProfileListener {
    fun done(position: Int, year: String)
    fun error(dialog: Dialog)
}