/*
 * Created by Mystery0 on 17-12-28 下午4:42.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-28 下午4:42
 */

package com.weilylab.xhuschedule.util

import android.content.Context
import com.weilylab.xhuschedule.APP

object TempSharedPreferenceUtil {
    private val sharedPreference = APP.getContext().getSharedPreferences("temp", Context.MODE_PRIVATE)

}