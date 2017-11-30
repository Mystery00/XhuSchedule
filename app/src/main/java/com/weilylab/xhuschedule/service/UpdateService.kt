/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-22 上午2:26
 */

package com.weilylab.xhuschedule.service

import android.app.IntentService
import android.content.Intent
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Update
import com.weilylab.xhuschedule.interfaces.UpdateService
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.notification.UpdateNotification
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader

class UpdateService : IntentService("UpdateService") {
    override fun onHandleIntent(intent: Intent?) {
        ScheduleHelper.phpRetrofit
                .create(UpdateService::class.java)
                .checkUpdateCall(getString(R.string.app_version_code).toInt())
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), Update::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Update> {
                    private lateinit var update: Update

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                        if (update.code == 1)
                            UpdateNotification.notify(applicationContext, update.version)
                        stopSelf()
                    }

                    override fun onNext(update: Update) {
                        this.update = update
                    }
                })
    }
}
