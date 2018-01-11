/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午3:50
 */

package com.weilylab.xhuschedule.classes.baseClass

/**
 * Created by myste.
 */
class Version {
    var versionCode = -1
    lateinit var versionName: String
    lateinit var updateLog: String
    lateinit var versionAPK: String//1.3.4之后更改为下载的CDN链接
    var apkSize = -1L
    var lastVersion = -1
    lateinit var lastVersionPatch: String//1.3.4之后更改为下载的CDN链接
    var patchSize = -1L
}