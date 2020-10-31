/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository.ds

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.ExpScore
import com.weilylab.xhuschedule.model.Student
import vip.mystery0.rx.PackageData

interface ScoreDataSource {
    fun queryClassScoreByUsername(scoreLiveData: MutableLiveData<PackageData<List<ClassScore>>>, student: Student, year: String, term: String)

    fun queryExpScoreByUsername(scoreLiveData: MutableLiveData<PackageData<List<ExpScore>>>, student: Student, year: String, term: String)
}