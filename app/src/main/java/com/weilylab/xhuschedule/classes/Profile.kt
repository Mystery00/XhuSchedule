/*
 * Created by Mystery0 on 17-11-28 下午8:04.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-28 下午8:04
 */

package com.weilylab.xhuschedule.classes

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

    fun map(studentInfoRT: StudentInfoRT) {
        profession = studentInfoRT.profession
        no = studentInfoRT.no
        classname = studentInfoRT.classname
        sex = studentInfoRT.sex
        grade = studentInfoRT.grade
        name = studentInfoRT.name
        institute = studentInfoRT.institute
        direction = studentInfoRT.direction
    }
}