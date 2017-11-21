package com.weilylab.xhuschedule.service

import android.app.IntentService
import android.content.Intent
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Version
import com.weilylab.xhuschedule.interfaces.UpdateResponse
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.UpdateNotification
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs

class UpdateService : IntentService(TAG) {
    companion object {
        private val TAG = "UpdateService"
    }

    private val retrofit = ScheduleHelper.getUpdateRetrofit()
    private var version: Version? = null

    override fun onHandleIntent(intent: Intent?) {
        Logs.i(TAG, "onHandleIntent: ")
        Observable.create<Int> { subscriber ->
            val call = retrofit.create(UpdateResponse::class.java).checkUpdateCall(getString(R.string.app_version_code).toInt())
            val response = call.execute()
            if (!response.isSuccessful) {
                subscriber.onNext(-1)
                subscriber.onComplete()
                return@create
            }
            val update = response.body()
            if (update?.code == 1)
                version = update.version
            Logs.i(TAG, "onCreate: " + update?.message)
            subscriber.onNext(update?.code!!)
            subscriber.onComplete()
        }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    private var code = -233

                    override fun onSubscribe(d: Disposable) {
                        Logs.i(TAG, "onSubscribe: ")
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                        Logs.i(TAG, "onComplete: ")
                        if (code == 1)
                            UpdateNotification.notify(applicationContext, version!!)
                        stopSelf()
                    }

                    override fun onNext(result: Int) {
                        Logs.i(TAG, "onNext: ")
                        code = result
                    }
                })
    }
}
