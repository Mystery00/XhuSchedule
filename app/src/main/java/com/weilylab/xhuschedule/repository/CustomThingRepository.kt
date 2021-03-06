/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.api.UserAPI
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.model.SyncCustomThing
import com.weilylab.xhuschedule.module.redoAfterLogin
import com.weilylab.xhuschedule.repository.local.dao.CustomThingDao
import com.weilylab.xhuschedule.utils.CalendarUtil
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.tools.ResourceException
import vip.mystery0.tools.factory.fromJson
import vip.mystery0.tools.factory.toJson
import vip.mystery0.tools.utils.isConnectInternet

class CustomThingRepository : KoinComponent {
    private val customThingDao: CustomThingDao by inject()

    private val userAPI: UserAPI by inject()

    private val studentRepository: StudentRepository by inject()

    suspend fun getToday() = customThingDao.queryAllThings().filter { c -> CalendarUtil.isThingOnDay(c) }

    suspend fun getAll(): List<CustomThing> = customThingDao.queryAllThings()

    suspend fun save(thing: CustomThing) = customThingDao.addThing(thing)

    suspend fun update(thing: CustomThing) = customThingDao.updateThing(thing)

    suspend fun delete(thing: CustomThing) = customThingDao.deleteThing(thing)

    suspend fun syncCustomThingForLocal() {
        val student = studentRepository.queryMainStudent()
                ?: throw ResourceException(R.string.hint_action_not_login)
        if (isConnectInternet()) {
            val key = "customThing"
            val response = userAPI.getUserData(student.username, key).redoAfterLogin(student) {
                userAPI.getUserData(student.username, key)
            }
            if (response.isSuccessful) {
                if (response.value.isBlank()) {
                    return
                }
                val courseList = response.value.fromJson<SyncCustomThing>().list
                val savedList = customThingDao.queryAllThings()
                savedList.forEach { thing -> delete(thing) }
                courseList.forEach { thing -> save(thing) }
                return
            } else {
                throw Exception(response.msg)
            }
        } else {
            throw ResourceException(R.string.hint_network_error)
        }
    }

    suspend fun syncCustomThingForServer() {
        val student = studentRepository.queryMainStudent()
                ?: throw ResourceException(R.string.hint_action_not_login)
        if (isConnectInternet()) {
            val key = "customThing"
            val localList = customThingDao.queryAllThings()
            val value = SyncCustomThing(localList).toJson()
            val response = userAPI.setUserData(student.username, key, value).redoAfterLogin(student) {
                userAPI.setUserData(student.username, key, value)
            }
            if (response.isSuccessful) {
                return
            } else {
                throw Exception(response.msg)
            }
        } else {
            throw ResourceException(R.string.hint_network_error)
        }
    }
}