package com.weilylab.xhuschedule.ui.activity

import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
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
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.viewModel.QueryClassScoreViewModel
import kotlinx.android.synthetic.main.activity_query_class_score.*
import vip.mystery0.tools.utils.DensityTools
import java.util.*

class QueryClassScoreActivity : XhuBaseActivity(R.layout.activity_query_class_score) {
	private lateinit var queryClassScoreViewModel: QueryClassScoreViewModel
	private lateinit var queryClassScoreRecyclerViewAdapter: QueryClassScoreRecyclerViewAdapter
	private var hasData = false

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
				hasData = true
				updateScoreList(it.data!!)
				showContent()
			}
			Error -> {
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
		queryClassScoreRecyclerViewAdapter = QueryClassScoreRecyclerViewAdapter(this)
		scoreListRecyclerView.adapter = queryClassScoreRecyclerViewAdapter
		val layoutParams = scoreListRecyclerView.layoutParams
		layoutParams.width = DensityTools.getScreenWidth(this)
		scoreListRecyclerView.layoutParams = layoutParams
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
					drawerLayout.closeDrawer(Gravity.END)
			}
		})
		textViewStudent.setOnClickListener {
			if (queryClassScoreViewModel.studentList.value == null || queryClassScoreViewModel.studentList.value!!.data == null || queryClassScoreViewModel.studentList.value!!.data!!.isEmpty()) {
				toastMessage(R.string.hint_action_not_login, true)
				return@setOnClickListener
			}
			val map = queryClassScoreViewModel.studentInfoList.value!!.data!!
			val studentList = map.keys.toList()
			val studentTextArray = Array(studentList.size) { i -> "${studentList[i].studentName}(${studentList[i].username})" }
			var nowIndex = studentList.indexOf(queryClassScoreViewModel.student.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_choose_student)
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
			if (queryClassScoreViewModel.studentList.value == null || queryClassScoreViewModel.studentList.value!!.data == null || queryClassScoreViewModel.studentList.value!!.data!!.isEmpty()) {
				toastMessage(R.string.hint_action_not_login, true)
				return@setOnClickListener
			}
			val map = queryClassScoreViewModel.studentInfoList.value!!.data!!
			val studentInfo = map[queryClassScoreViewModel.student.value]
			val yearTextArray = CalendarUtil.getSelectArray(studentInfo?.grade)
			var nowIndex = yearTextArray.indexOf(queryClassScoreViewModel.year.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_choose_year)
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
					.setTitle(R.string.hint_dialog_choose_term)
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
			if (queryClassScoreViewModel.studentList.value == null || queryClassScoreViewModel.studentList.value!!.data == null || queryClassScoreViewModel.studentList.value!!.data!!.isEmpty())
				toastMessage(R.string.hint_action_not_login, true)
			else
				ScoreRepository.queryClassScore(queryClassScoreViewModel)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_query_score, menu)
		menu.findItem(R.id.action_show_gpa).isChecked = ConfigurationUtil.isShowGpa
		menu.findItem(R.id.action_show_failed).isChecked = ConfigurationUtil.isShowFailed
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		return when (item?.itemId) {
			R.id.action_show_gpa -> {
				item.isChecked = !item.isChecked
				ConfigurationUtil.isShowGpa = item.isChecked
				queryClassScoreRecyclerViewAdapter.notifyDataSetChanged()
				true
			}
			R.id.action_show_failed -> {
				item.isChecked = !item.isChecked
				ConfigurationUtil.isShowFailed = item.isChecked
				if (queryClassScoreViewModel.scoreList.value != null && queryClassScoreViewModel.scoreList.value!!.data != null)
					updateScoreList(queryClassScoreViewModel.scoreList.value!!.data!!)
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	override fun onBackPressed() {
		if (drawerLayout.isDrawerOpen(Gravity.END))
			drawerLayout.closeDrawer(Gravity.END)
		else
			super.onBackPressed()
	}

	private fun updateScoreList(list: List<ClassScore>) {
		queryClassScoreRecyclerViewAdapter.items.clear()
		if (!ConfigurationUtil.isShowFailed)
			queryClassScoreRecyclerViewAdapter.items.addAll(list.filter { !it.failed })
		else
			queryClassScoreRecyclerViewAdapter.items.addAll(list)
		queryClassScoreRecyclerViewAdapter.notifyDataSetChanged()
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
		drawerLayout.openDrawer(Gravity.END)
	}
}
