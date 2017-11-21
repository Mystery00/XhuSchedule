package com.weilylab.xhuschedule.classes

/**
 * Created by myste.
 */
class Version {
    var versionCode = -1
    lateinit var versionName: String
    lateinit var updateLog: String
    lateinit var versionAPK: String
    var apkSize = -1L
    var lastVersion = -1
    lateinit var lastVersionPatch: String
    var patchSize = -1L
}