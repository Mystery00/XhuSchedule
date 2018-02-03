/*
 * Created by Mystery0 on 18-2-3 下午12:11.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-2-3 下午12:11
 */

package com.weilylab.xhuschedule.util

import android.content.Context

object TestUtil {
    fun getPlayServiceVersion(context: Context): String {
        val packages = context.packageManager.getInstalledPackages(0)
        var playServiceVersion = "null"
        packages.forEach {
            if (it.applicationInfo.packageName == "com.google.android.gms")
                playServiceVersion = "${it.versionName}-${it.versionCode}"
        }
        return playServiceVersion
    }
}