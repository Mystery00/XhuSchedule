/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.activity

import androidx.databinding.DataBindingUtil
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.ActivityQueryCetScoreShowBinding
import com.weilylab.xhuschedule.model.CetScore
import com.weilylab.xhuschedule.viewmodel.QueryCetScoreViewModelHelper
import kotlinx.android.synthetic.main.activity_query_cet_score_show.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vip.mystery0.rx.DataObserver

class QueryCetScoreShowActivity : XhuBaseActivity(R.layout.activity_query_cet_score_show) {
	private lateinit var activityQueryCetScoreShowBinding: ActivityQueryCetScoreShowBinding

	private val cetScoreObserver = object : DataObserver<CetScore> {
		override fun contentNoEmpty(data: CetScore) {
			doShow(data)
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
		launch(Dispatchers.Default) {
			while (!::activityQueryCetScoreShowBinding.isInitialized) {
				Thread.sleep(200)
			}
			withContext(Dispatchers.Main) {
				activityQueryCetScoreShowBinding.cetScore = cetScore
				val nameText = "姓名：${cetScore.name}"
				val schoolText = "学校：${cetScore.school}"
				activityQueryCetScoreShowBinding.textViewName.text = nameText
				activityQueryCetScoreShowBinding.textViewSchool.text = schoolText
			}
		}
	}
}
