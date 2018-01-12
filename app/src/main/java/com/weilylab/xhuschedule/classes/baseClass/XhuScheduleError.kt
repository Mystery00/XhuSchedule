/*
 * Created by Mystery0 on 18-1-12 下午8:52.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                  =====================================================
 *                  =                                                   =
 *                  =                       _oo0oo_                     =
 *                  =                      o8888888o                    =
 *                  =                      88" . "88                    =
 *                  =                      (| -_- |)                    =
 *                  =                      0\  =  /0                    =
 *                  =                    ___/`---'\___                  =
 *                  =                  .' \\|     |# '.                 =
 *                  =                 / \\|||  :  |||# \                =
 *                  =                / _||||| -:- |||||- \              =
 *                  =               |   | \\\  -  #/ |   |              =
 *                  =               | \_|  ''\---/''  |_/ |             =
 *                  =               \  .-\__  '-'  ___/-. /             =
 *                  =             ___'. .'  /--.--\  `. .'___           =
 *                  =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                  =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                  =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                  =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                  =                       `=---='                     =
 *                  =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                  =                                                   =
 *                  =               佛祖保佑         永无BUG              =
 *                  =                                                   =
 *                  =====================================================
 *
 * Last modified 18-1-12 下午8:51
 */

package com.weilylab.xhuschedule.classes.baseClass

import android.content.Context
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.response.UploadLogResponse
import com.weilylab.xhuschedule.interfaces.PhpService
import com.weilylab.xhuschedule.listener.UploadLogListener
import com.weilylab.xhuschedule.util.ScheduleHelper
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.InputStreamReader
import java.io.Serializable

/**
 * Created by myste.
 */
data class XhuScheduleError(val time: String, val appVersionName: String,
                            val appVersionCode: Int, val AndroidVersion: String,
                            val sdk: Int, val vendor: String, val model: String,
                            val ex: Throwable) : Serializable {

    fun uploadLog(context: Context, logFile: File, listener: UploadLogListener) {
        val map = HashMap<String, String>()
        map.put("date", time)
        map.put("appName", context.getString(R.string.app_name))
        map.put("appVersionName", appVersionName)
        map.put("appVersionCode", appVersionCode.toString())
        map.put("androidVersion", AndroidVersion)
        map.put("sdk", sdk.toString())
        map.put("vendor", vendor)
        map.put("model", model)
        ScheduleHelper.phpRetrofit
                .create(PhpService::class.java)
                .uploadLog(createPartFromMap(map), prepareFilePart("logFile", logFile))
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), UploadLogResponse::class.java) }
                .subscribe(object : Observer<UploadLogResponse> {
                    private lateinit var response: UploadLogResponse
                    override fun onError(e: Throwable) {
                        listener.error(-1, e)
                    }

                    override fun onNext(t: UploadLogResponse) {
                        response = t
                    }

                    override fun onComplete() {
                        listener.done(response.code, response.message)
                    }

                    override fun onSubscribe(d: Disposable) {
                        listener.ready()
                    }
                })
    }

    /**
     * 从Map中构建请求体
     */
    private fun createPartFromMap(map: Map<String, String>): MutableMap<String, RequestBody> {
        val requestMap = HashMap<String, RequestBody>()
        for (key in map.keys)
            requestMap.put(key, RequestBody.create(MediaType.parse("text/plain"),map[key]!!))
        return requestMap
    }

    /**
     * 根据文件构建部分请求体，在Retrofit中会根据类型决定是否将part加入到请求体中
     */
    private fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestFile = RequestBody.create(MultipartBody.FORM, file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}