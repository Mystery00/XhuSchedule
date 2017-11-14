package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.interfaces.UpdateResponse
import com.weilylab.xhuschedule.util.ScheduleHelper
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs

class UpdateService : Service()
{
	companion object
	{
		private val TAG = "UpdateService"
	}

	private val retrofit = ScheduleHelper.getInstance().getUpdateRetrofit()

	override fun onCreate()
	{
		super.onCreate()
		val observer = object : Observer<Int>
		{
			override fun onSubscribe(d: Disposable)
			{
				Logs.i(TAG, "onSubscribe: ")
			}

			override fun onError(e: Throwable)
			{
				e.printStackTrace()
			}

			override fun onComplete()
			{
			}

			override fun onNext(result: Int)
			{
				Logs.i(TAG, "onNext: ")
			}
		}

		val observable = Observable.create<Int> { subscriber ->
			val service = retrofit.create(UpdateResponse::class.java)
			val call = service.checkUpdateCall(getString(R.string.app_version_code).toInt())
			val response=call.execute()
			if (!response.isSuccessful)
			{

			}
		}
		observable.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}

	override fun onBind(intent: Intent): IBinder?
	{
		return null
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
	{
		return super.onStartCommand(intent, flags, startId)
	}
}
