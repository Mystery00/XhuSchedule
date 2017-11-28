/*
 * Created by Mystery0 on 17-11-28 下午8:04.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-28 下午8:04
 */

package com.weilylab.xhuschedule.classes

import java.io.Serializable

class Profile : Serializable {
    lateinit var number: String
    lateinit var name: String
    lateinit var college: String
    lateinit var professional: String
    lateinit var `class`: String
    lateinit var grade: String
    lateinit var direction: String
}