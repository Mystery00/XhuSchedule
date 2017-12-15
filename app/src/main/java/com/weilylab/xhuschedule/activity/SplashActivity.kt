/*
 * Created by Mystery0 on 17-12-11 下午9:08.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-11 下午9:08
 */

package com.weilylab.xhuschedule.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.Settings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.io.File
import kotlin.math.max

/**
 * Created by mystery0.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Settings.autoCheckLog) {
            var latestLog: File? = null
            val sharedPreference = getSharedPreferences("updateData", Context.MODE_PRIVATE)
            Observable.create<Boolean> { subscriber ->
                //log文件前缀名
                val fileNamePrefix = "crash"
                //log文件的扩展名
                val fileNameSuffix = "txt"
                val dir = File(externalCacheDir.absolutePath + File.separator)
                var modified = 0L
                dir.listFiles()
                        .filter {
                            it.name.startsWith(fileNamePrefix, true) && it.name.endsWith(fileNameSuffix, true)
                        }
                        .forEach {
                            if (modified < it.lastModified()) {
                                modified = max(it.lastModified(), modified)
                                latestLog = it
                            }
                        }
                if (modified == 0L) {
                    subscriber.onNext(false)
                    subscriber.onComplete()
                    return@create
                }
                val saveFile = sharedPreference.getString("saveFile", "")
                subscriber.onNext(saveFile != latestLog!!.name)
                subscriber.onComplete()
            }
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : DisposableObserver<Boolean>() {
                        override fun onError(e: Throwable) {
                        }

                        override fun onComplete() {
                        }

                        override fun onNext(t: Boolean) {
                            if (t) {
                                sharedPreference.edit().putString("saveFile", latestLog!!.name).apply()
                                AlertDialog.Builder(this@SplashActivity)
                                        .setTitle(" ")
                                        .setMessage(getString(R.string.hint_check_log, latestLog!!.name, latestLog!!.absolutePath, CalendarUtil.showDate(latestLog!!.lastModified())))
                                        .setPositiveButton(R.string.action_feedback, { _, _ ->
                                            //上传日志
                                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                            finish()
                                        })
                                        .setNegativeButton(android.R.string.cancel, { _, _ ->
                                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                            finish()
                                        })
                                        .show()
                            } else {
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                finish()
                            }
                        }
                    })
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}