/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-3 上午1:34
 */

package com.weilylab.xhuschedule.classes

import com.weilylab.xhuschedule.classes.rt.StudentInfoRT
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

    fun map(studentInfoRT: StudentInfoRT): Profile {
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