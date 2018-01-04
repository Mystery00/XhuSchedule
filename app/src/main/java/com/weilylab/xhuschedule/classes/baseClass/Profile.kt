/*
 * Created by Mystery0 on 18-1-4 下午5:14.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 17-12-21 上午3:41
 */

package com.weilylab.xhuschedule.classes.baseClass

import com.weilylab.xhuschedule.classes.rt.GetInfoRT
import java.io.Serializable

class Profile : Serializable {
    var profession = ""
    var no = ""
    var classname = ""
    var sex = ""
    var grade = ""
    var name = ""
    var institute = ""
    var direction = ""

    fun map(studentGetInfoRT: GetInfoRT): Profile {
        profession = studentGetInfoRT.profession
        no = studentGetInfoRT.no
        classname = studentGetInfoRT.classname
        sex = studentGetInfoRT.sex
        grade = studentGetInfoRT.grade
        name = studentGetInfoRT.name
        institute = studentGetInfoRT.institute
        direction = studentGetInfoRT.direction
        return this
    }
}