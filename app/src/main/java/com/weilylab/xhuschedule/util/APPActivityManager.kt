/*
 * Created by Mystery0 on 18-1-11 下午4:19.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-1-11 下午4:19
 */

package com.weilylab.xhuschedule.util

import android.app.Activity
import java.util.*


/**
 * Created by kun on 2016/7/12.
 * Activity管理类
 */
class APPActivityManager private constructor() {
    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity {
        return activityStack.lastElement()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        activityStack.lastElement()?.finish()
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity) {
        activityStack.remove(activity)
        activity.finish()
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        activityStack
                .filter { it.javaClass == cls }
                .forEach { finishActivity(it) }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        var i = 0
        val size = activityStack.size
        while (i < size) {
            if (null != activityStack[i]) {
                activityStack[i].finish()
            }
            i++
        }
        activityStack.clear()
    }

    companion object {
        private var activityStack: Stack<Activity> = Stack()
        private var instance: APPActivityManager? = null
        /**
         * 单一实例
         */
        val appManager: APPActivityManager
            get() {
                if (instance == null) {
                    instance = APPActivityManager()
                }
                return instance!!
            }
    }
}