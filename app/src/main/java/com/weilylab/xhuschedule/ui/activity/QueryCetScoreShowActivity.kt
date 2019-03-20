package com.weilylab.xhuschedule.ui.activity

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.ActivityQueryCetScoreShowBinding
import com.weilylab.xhuschedule.model.CetScore
import com.weilylab.xhuschedule.viewmodel.QueryCetScoreViewModelHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_query_cet_score_show.*
import vip.mystery0.logs.Logs
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.Status

class QueryCetScoreShowActivity : XhuBaseActivity(R.layout.activity_query_cet_score_show) {
	private lateinit var activityQueryCetScoreShowBinding: ActivityQueryCetScoreShowBinding

	private val cetScoreObserver = Observer<PackageData<CetScore>> {
		when (it.status) {
			Status.Content -> doShow(it.data!!)
			else -> {
			}
		}
	}

	override fun inflateView(layoutId: Int) {
		activityQueryCetScoreShowBinding = DataBindingUtil.setContentView(this, layoutId)
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	override fun initData() {
		super.initData()
		initViewModel()
	}

	private fun initViewModel() {
		QueryCetScoreViewModelHelper.cetScoreLiveData.observe(this, cetScoreObserver)
	}

	private fun removeObserver() {
		QueryCetScoreViewModelHelper.cetScoreLiveData.removeObserver(cetScoreObserver)
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		removeObserver()
	}

	private fun doShow(cetScore: CetScore) {
		Observable.create<Boolean> {
			while (!::activityQueryCetScoreShowBinding.isInitialized) {
				Thread.sleep(200)
			}
			it.onComplete()
		}
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Boolean>() {
					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}

					override fun onFinish(data: Boolean?) {
						if (::activityQueryCetScoreShowBinding.isInitialized) {
							activityQueryCetScoreShowBinding.cetScore = cetScore
							val nameText = "姓名：${cetScore.name}"
							val schoolText = "学校：${cetScore.school}"
							activityQueryCetScoreShowBinding.textViewName.text = nameText
							activityQueryCetScoreShowBinding.textViewSchool.text = schoolText
						}
					}
				})
	}
}
