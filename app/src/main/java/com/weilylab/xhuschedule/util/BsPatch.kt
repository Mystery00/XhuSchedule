/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午3:50
 */

package com.weilylab.xhuschedule.util

/**
 * Created by zhy on 16/10/7.
 * 合并增量更新包的方法
 */
object BsPatch {
    init {
        System.loadLibrary("bspatch")
    }

    external fun patch(oldApk: String, newApk: String, patch: String): Int
}
