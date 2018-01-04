/*
 * Created by Mystery0 on 18-1-4 下午5:14.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 17-12-21 上午3:41
 */

package com.weilylab.xhuschedule.classes.baseClass

import com.weilylab.xhuschedule.classes.rt.InfoRT
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

    fun map(studentInfoRT: InfoRT): Profile {
        profession = studentInfoRT.profession
        no = studentInfoRT.no
        classname = studentInfoRT.classname
        sex = studentInfoRT.sex
        grade = studentInfoRT.grade
        name = studentInfoRT.name
        institute = studentInfoRT.institute
        direction = studentInfoRT.direction
        return this
    }
}