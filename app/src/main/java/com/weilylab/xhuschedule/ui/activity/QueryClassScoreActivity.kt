package com.weilylab.xhuschedule.ui.activity

import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.ui.helper.AnimationHelper
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import com.weilylab.xhuschedule.viewModel.QueryClassScoreViewModel
import kotlinx.android.synthetic.main.activity_query_class_score.*
import vip.mystery0.tools.utils.DensityTools

class QueryClassScoreActivity : XhuBaseActivity(R.layout.activity_query_class_score) {
	private lateinit var queryClassScoreViewModel: QueryClassScoreViewModel
	private var startTouchY = 0F
	private var scoreListLayoutShowY = 0//显示时的位置
	private var scoreListLayoutHideButHasData = 0//隐藏时有数据的位置

	private val scoreListObserver = Observer<PackageData<List<String>>> {
		when (it.status) {
			Loading -> showLoading()
			Empty -> showEmpty()
			Content -> showContent(true)
			Error -> Toast.makeText(this, it.error?.message, Toast.LENGTH_SHORT)
					.show()
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		getScoreLayoutY()
		hideContent(false)
	}

	override fun initData() {
		super.initData()
		initViewModel()
	}

	private fun initViewModel() {
		queryClassScoreViewModel = ViewModelProviders.of(this).get(QueryClassScoreViewModel::class.java)
		queryClassScoreViewModel.scoreList.observe(this, scoreListObserver)
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
		queryButton.setOnClickListener {
			queryClassScoreViewModel.scoreList.value = PackageData.loading()
			RxObservable<Boolean>()
					.doThings {
						Thread.sleep(3000)
						it.onFinish(true)
					}.subscribe(object : RxObserver<Boolean>() {
						override fun onFinish(data: Boolean?) {
							queryClassScoreViewModel.scoreList.value = PackageData.content(arrayListOf(""))
						}

						override fun onError(e: Throwable) {
						}
					})
		}
		scoreListLayout.setOnTouchListener { _, motionEvent ->
			when (motionEvent.action) {
				MotionEvent.ACTION_DOWN -> startTouchY = motionEvent.rawY
				MotionEvent.ACTION_MOVE -> moveContent((motionEvent.rawY - startTouchY + scoreListLayoutShowY).toInt())
				MotionEvent.ACTION_UP -> hideContentFromHere((motionEvent.rawY - startTouchY + scoreListLayoutShowY).toInt())
			}
			true
		}
	}

	private fun showLoading() {
		queryButton.visibility = View.GONE
		loadingView.visibility = View.VISIBLE
	}

	private fun dismissLoading() {
		queryButton.visibility = View.VISIBLE
		loadingView.visibility = View.GONE
	}

	private fun showEmpty() {
		dismissLoading()

	}

	private fun getScoreLayoutY() {
		val array = intArrayOf(0, 0)
		scoreListLayout.getLocationOnScreen(array)
		scoreListLayoutShowY = array[1]
	}

	private fun showContent(isShowAnimation: Boolean) {
		dismissLoading()
		val height = DensityTools.getScreenHeight(this)
		if (isShowAnimation)
			AnimationHelper.translationY(scoreListLayout, scoreListLayoutShowY + height, scoreListLayoutShowY, 300L)
		else
			AnimationHelper.move(scoreListLayout, scoreListLayoutShowY, 0)
	}

	private fun moveContent(current: Int) {
		AnimationHelper.move(scoreListLayout, scoreListLayoutShowY, current)
	}

	private fun hideContent(isShowAnimation: Boolean) {
		dismissLoading()
		val height = DensityTools.getScreenHeight(this)
		if (isShowAnimation)
			AnimationHelper.translationY(scoreListLayout, scoreListLayoutShowY, scoreListLayoutShowY + height, 300L)
		else
			AnimationHelper.move(scoreListLayout, scoreListLayoutShowY, height)
	}

	private fun hideContentFromHere(current: Int) {
		val height = DensityTools.getScreenHeight(this)
		AnimationHelper.translationY(scoreListLayout, current, scoreListLayoutShowY + height, 300L)
	}
}
