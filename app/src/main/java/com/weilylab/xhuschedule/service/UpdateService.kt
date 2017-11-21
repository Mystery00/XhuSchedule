package com.weilylab.xhuschedule.service

import android.app.IntentService
import android.content.Intent
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Update
import com.weilylab.xhuschedule.interfaces.UpdateResponse
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.UpdateNotification
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader

class UpdateService : IntentService("UpdateService") {
    override fun onHandleIntent(intent: Intent?) {
        ScheduleHelper.phpRetrofit
                .create(UpdateResponse::class.java)
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
