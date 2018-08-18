package com.weilylab.xhuschedule.ui.activity

import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.ScoreRepository
import com.weilylab.xhuschedule.ui.adapter.QueryClassScoreRecyclerViewAdapter
import com.weilylab.xhuschedule.ui.helper.ScoreAnimationHelper
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.viewModel.QueryClassScoreViewModel
import kotlinx.android.synthetic.main.activity_query_class_score.*
import vip.mystery0.tools.utils.DensityTools
import java.util.*

class QueryClassScoreActivity : XhuBaseActivity(R.layout.activity_query_class_score) {
	private lateinit var queryClassScoreViewModel: QueryClassScoreViewModel
	private var scoreListLayoutShowY = 0//显示时的位置
	private var scoreListLayoutHideButHasData = 0//隐藏时有数据的位置
	private var scoreListLayoutHideNoData = 0//隐藏时无数据的位置
	private lateinit var queryClassScoreRecyclerViewAdapter: QueryClassScoreRecyclerViewAdapter

	private val studentInfoListObserver = Observer<PackageData<Map<Student, StudentInfo?>>> { data ->
		when (data.status) {
			Content -> {
				val map = data.data!!
				if (map.keys.isNotEmpty()) {
					queryClassScoreViewModel.student.value = map.keys.first { it.isMain }
					queryClassScoreViewModel.year.value = CalendarUtil.getSelectArray(null).last()
					val month = Calendar.getInstance().get(Calendar.MONTH)
					queryClassScoreViewModel.term.value = if (month in Calendar.MARCH until Calendar.SEPTEMBER) "2" else "1"
				}
			}
		}
	}

	private val studentObserver = Observer<Student> {
		val text = "${it.studentName}(${it.username})"
		textViewStudent.text = text
	}

	private val yearObserver = Observer<String> {
		textViewYear.text = it
	}

	private val termObserver = Observer<String> {
		textViewTerm.text = it
	}

	private val scoreListObserver = Observer<PackageData<List<ClassScore>>> {
		when (it.status) {
			Loading -> showLoading()
			Empty -> showEmpty()
			Content -> {
				ScoreAnimationHelper.hasData = true
				queryClassScoreRecyclerViewAdapter.items.clear()
				queryClassScoreRecyclerViewAdapter.items.addAll(it.data!!)
				queryClassScoreRecyclerViewAdapter.notifyDataSetChanged()
				showContent(true)
			}
			Error -> {
				dismissLoading()
				Toast.makeText(this, it.error?.message, Toast.LENGTH_SHORT)
						.show()
			}
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		getScoreLayoutY()
		recyclerView.layoutManager = LinearLayoutManager(this)
		val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
		recyclerView.addItemDecoration(dividerItemDecoration)
		queryClassScoreRecyclerViewAdapter = QueryClassScoreRecyclerViewAdapter(this)
		recyclerView.adapter = queryClassScoreRecyclerViewAdapter
		hideContent(false)
	}

	override fun initData() {
		super.initData()
		initViewModel()
		ScoreRepository.queryAllStudentInfo(queryClassScoreViewModel)
	}

	private fun initViewModel() {
		queryClassScoreViewModel = ViewModelProviders.of(this).get(QueryClassScoreViewModel::class.java)
		queryClassScoreViewModel.studentInfoList.observe(this, studentInfoListObserver)
		queryClassScoreViewModel.student.observe(this, studentObserver)
		queryClassScoreViewModel.year.observe(this, yearObserver)
		queryClassScoreViewModel.term.observe(this, termObserver)
		queryClassScoreViewModel.scoreList.observe(this, scoreListObserver)
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
		textViewStudent.setOnClickListener {
			val map = queryClassScoreViewModel.studentInfoList.value!!.data!!
			val studentList = map.keys.toList()
			val studentTextArray = Array(studentList.size) { i -> "${studentList[i].studentName}(${studentList[i].username})" }
			var nowIndex = studentList.indexOf(queryClassScoreViewModel.student.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle("选择学生")
					.setSingleChoiceItems(studentTextArray, nowIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						queryClassScoreViewModel.student.value = studentList[selectIndex]
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewYear.setOnClickListener {
			val map = queryClassScoreViewModel.studentInfoList.value!!.data!!
			val studentInfo = map[queryClassScoreViewModel.student.value]
			val yearTextArray = CalendarUtil.getSelectArray(studentInfo?.grade)
			var nowIndex = yearTextArray.indexOf(queryClassScoreViewModel.year.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle("选择学年")
					.setSingleChoiceItems(yearTextArray, nowIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						queryClassScoreViewModel.year.value = yearTextArray[selectIndex]
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewTerm.setOnClickListener {
			val termTextArray = Array(3) { i -> (i + 1).toString() }
			var nowIndex = termTextArray.indexOf(queryClassScoreViewModel.term.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle("选择学期")
					.setSingleChoiceItems(termTextArray, nowIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						queryClassScoreViewModel.term.value = termTextArray[selectIndex]
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		queryButton.setOnClickListener {
			ScoreRepository.queryClassScore(queryClassScoreViewModel)
		}
		scoreListLayout.doOnTouchListener = {
			doOnTouch(it)
		}
	}

	private fun doOnTouch(motionEvent: MotionEvent?) {
		when (motionEvent?.action) {
			MotionEvent.ACTION_MOVE -> moveContent((motionEvent.rawY - ScoreAnimationHelper.startTouchY + scoreListLayoutShowY).toInt())
			MotionEvent.ACTION_UP -> {
				val offset = motionEvent.rawY - ScoreAnimationHelper.startTouchY
				val current = when {
					ScoreAnimationHelper.isShowScoreLayout -> scoreListLayoutShowY
					!ScoreAnimationHelper.isShowScoreLayout && ScoreAnimationHelper.hasData -> scoreListLayoutHideButHasData
					else -> scoreListLayoutHideNoData
				}
				val isReverse = Math.abs(offset) * 3 > DensityTools.getScreenHeight(this)
				if (isReverse) ScoreAnimationHelper.isShowScoreLayout = !ScoreAnimationHelper.isShowScoreLayout
				moveContentFromHere((offset + current).toInt())
			}
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
		val statusBarHeight = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
		val height = DensityTools.getScreenHeight(this)
		scoreListLayoutShowY = 0
		scoreListLayoutHideNoData = height - statusBarHeight - DensityTools.dp2px(this, 45F)
		scoreListLayoutHideButHasData = scoreListLayoutHideNoData - DensityTools.dp2px(this, 45F)
	}

	private fun showContent(isShowAnimation: Boolean) {
		dismissLoading()
		if (isShowAnimation)
			ScoreAnimationHelper.translationY(scoreListLayout, scoreListLayoutHideNoData, scoreListLayoutShowY, 300L)
		else
			ScoreAnimationHelper.move(scoreListLayout, scoreListLayoutShowY, 0)
		ScoreAnimationHelper.isShowScoreLayout = true
	}

	private fun moveContent(current: Int) {
		val start = when {
			ScoreAnimationHelper.isShowScoreLayout -> scoreListLayoutShowY
			!ScoreAnimationHelper.isShowScoreLayout && ScoreAnimationHelper.hasData -> scoreListLayoutHideButHasData
			else -> scoreListLayoutHideNoData
		}
		ScoreAnimationHelper.move(scoreListLayout, start, current)
	}

	private fun hideContent(isShowAnimation: Boolean) {
		dismissLoading()
		if (isShowAnimation)
			ScoreAnimationHelper.translationY(scoreListLayout, scoreListLayoutShowY, scoreListLayoutHideNoData, 300L)
		else
			ScoreAnimationHelper.move(scoreListLayout, scoreListLayoutHideNoData, 0)
		ScoreAnimationHelper.isShowScoreLayout = false
	}

	private fun moveContentFromHere(current: Int) {
		val end = when {
			ScoreAnimationHelper.isShowScoreLayout -> scoreListLayoutShowY
			!ScoreAnimationHelper.isShowScoreLayout && ScoreAnimationHelper.hasData -> scoreListLayoutHideButHasData
			else -> scoreListLayoutHideNoData
		}
		ScoreAnimationHelper.translationY(scoreListLayout, current, end, 300L)
	}
}
