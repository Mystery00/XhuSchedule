package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.ExpScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.ScoreRepository
import com.weilylab.xhuschedule.ui.adapter.QueryExpScoreRecyclerViewAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status
import com.weilylab.xhuschedule.viewmodel.QueryExpScoreViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_query_exp_score.*
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.DensityTools
import java.util.*

class QueryExpScoreActivity : XhuBaseActivity(R.layout.activity_query_exp_score) {
	private val queryExpScoreViewModel: QueryExpScoreViewModel by lazy {
		ViewModelProviders.of(this)[QueryExpScoreViewModel::class.java]
	}
	private val queryExpScoreRecyclerViewAdapter: QueryExpScoreRecyclerViewAdapter by lazy { QueryExpScoreRecyclerViewAdapter(this) }
	private var hasData = false
	private val dialog: Dialog by lazy {
		ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_init))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(this, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}

	private val studentInfoListObserver = Observer<PackageData<Map<Student, StudentInfo?>>> { data ->
		when (data.status) {
			Status.Loading -> dialog.show()
			Status.Content -> {
				val map = data.data!!
				if (map.keys.isNotEmpty()) {
					queryExpScoreViewModel.student.value = map.keys.first { it.isMain }
					queryExpScoreViewModel.year.value = CalendarUtil.getSelectArray(null).last()
					val month = Calendar.getInstance().get(Calendar.MONTH)
					queryExpScoreViewModel.term.value = if (month in Calendar.MARCH until Calendar.SEPTEMBER) "2" else "1"
				}
			}
			Status.Error -> {
				dialog.dismiss()
				Logs.wtf("studentInfoListObserver: ", data.error)
				toastMessage(R.string.error_init_failed)
				finish()
			}
			Status.Empty -> {
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

	private val scoreListObserver = Observer<PackageData<List<ExpScore>>> {
		when (it.status) {
			Status.Loading -> showLoading()
			Status.Empty -> showEmpty()
			Status.Content -> {
				hasData = true
				updateScoreList(it.data!!)
				showContent()
			}
			Status.Error -> {
				Logs.wtfm("scoreListObserver: ", it.error)
				dismissLoading()
				toastMessage(it.error?.message)
			}
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		scoreListRecyclerView.layoutManager = LinearLayoutManager(this)
		val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
		scoreListRecyclerView.addItemDecoration(dividerItemDecoration)
		scoreListRecyclerView.adapter = queryExpScoreRecyclerViewAdapter
		val layoutParams = scoreListRecyclerView.layoutParams
		layoutParams.width = DensityTools.getScreenWidth()
		scoreListRecyclerView.layoutParams = layoutParams
	}

	override fun initData() {
		super.initData()
		initViewModel()
		ScoreRepository.queryAllStudentInfo(queryExpScoreViewModel)
	}

	private fun initViewModel() {
		queryExpScoreViewModel.studentInfoList.observe(this, studentInfoListObserver)
		queryExpScoreViewModel.student.observe(this, studentObserver)
		queryExpScoreViewModel.year.observe(this, yearObserver)
		queryExpScoreViewModel.term.observe(this, termObserver)
		queryExpScoreViewModel.scoreList.observe(this, scoreListObserver)
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			onBackPressed()
		}
		drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
			override fun onDrawerStateChanged(newState: Int) {
			}

			override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
			}

			override fun onDrawerClosed(drawerView: View) {
			}

			override fun onDrawerOpened(drawerView: View) {
				if (!hasData)
					drawerLayout.closeDrawer(GravityCompat.END)
			}
		})
		textViewStudent.setOnClickListener {
			val map = queryExpScoreViewModel.studentInfoList.value!!.data!!
			val studentList = map.keys.toList()
			val studentTextArray = Array(studentList.size) { i -> "${studentList[i].studentName}(${studentList[i].username})" }
			var nowIndex = studentList.indexOf(queryExpScoreViewModel.student.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_choose_student)
					.setSingleChoiceItems(studentTextArray, nowIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						queryExpScoreViewModel.student.value = studentList[selectIndex]
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewYear.setOnClickListener {
			val map = queryExpScoreViewModel.studentInfoList.value!!.data!!
			val studentInfo = map[queryExpScoreViewModel.student.value]
			val yearTextArray = CalendarUtil.getSelectArray(studentInfo?.grade)
			var nowIndex = yearTextArray.indexOf(queryExpScoreViewModel.year.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_choose_year)
					.setSingleChoiceItems(yearTextArray, nowIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						queryExpScoreViewModel.year.value = yearTextArray[selectIndex]
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewTerm.setOnClickListener {
			val termTextArray = Array(3) { i -> (i + 1).toString() }
			var nowIndex = termTextArray.indexOf(queryExpScoreViewModel.term.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_choose_term)
					.setSingleChoiceItems(termTextArray, nowIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						queryExpScoreViewModel.term.value = termTextArray[selectIndex]
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		queryButton.setOnClickListener {
			ScoreRepository.queryExpScore(queryExpScoreViewModel)
		}
	}

	override fun onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.END))
			drawerLayout.closeDrawer(GravityCompat.END)
		else
			super.onBackPressed()
	}

	private fun updateScoreList(list: List<ExpScore>) {
		queryExpScoreRecyclerViewAdapter.items.clear()
		queryExpScoreRecyclerViewAdapter.items.addAll(list)
		queryExpScoreRecyclerViewAdapter.notifyDataSetChanged()
	}

	private fun showLoading() {
		queryButton.alpha = 0F
		queryButton.isClickable = false
		loadingView.visibility = View.VISIBLE
	}

	private fun dismissLoading() {
		queryButton.alpha = 1F
		queryButton.isClickable = true
		loadingView.visibility = View.GONE
	}

	private fun showEmpty() {
		dismissLoading()
		toastMessage(R.string.hint_data_null, true)
	}

	private fun showContent() {
		dismissLoading()
		drawerLayout.openDrawer(GravityCompat.END)
	}
}
