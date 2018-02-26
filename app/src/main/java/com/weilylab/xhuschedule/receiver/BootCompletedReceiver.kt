/*
 * Created by Mystery0 on 18-2-27 上午2:26.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-27 上午2:26
 */

package com.weilylab.xhuschedule.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.weilylab.xhuschedule.service.BootInitService
import com.weilylab.xhuschedule.util.Constants
import vip.mystery0.tools.logs.Logs

class BootCompletedReceiver : BroadcastReceiver() {
    private val TAG = "BootCompletedReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Logs.i(TAG, "onReceive: ")
        if (intent.action == Constants.ACTION_BOOT_COMPLETED)
            context.startService(Intent(context, BootInitService::class.java))
    }
}
