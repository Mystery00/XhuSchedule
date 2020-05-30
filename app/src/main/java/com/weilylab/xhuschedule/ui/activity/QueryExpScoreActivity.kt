package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.ExpScore
import com.weilylab.xhuschedule.ui.adapter.QueryExpScoreRecyclerViewAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.viewmodel.QueryExpScoreViewModel
import kotlinx.android.synthetic.main.activity_query_exp_score.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageDataObserver
import vip.mystery0.tools.toastLong
import vip.mystery0.tools.utils.screenWidth

class QueryExpScoreActivity : XhuBaseActivity(R.layout.activity_query_exp_score) {
	private val queryExpScoreViewModel: QueryExpScoreViewModel by viewModel()
	private val queryExpScoreRecyclerViewAdapter: QueryExpScoreRecyclerViewAdapter by lazy { QueryExpScoreRecyclerViewAdapter(this) }
	private var hasData = false
	private val dialog: Dialog by lazy { buildDialog(R.string.hint_dialog_init) }

	private val scoreListObserver = object : PackageDataObserver<List<ExpScore>> {
		override fun loading() {
			showLoading()
		}

		override fun empty(data: List<ExpScore>?) {
			showEmpty()
		}

		override fun content(data: List<ExpScore>?) {
			hasData = true
			updateScoreList(data!!)
			showContent()
		}

		override fun error(data: List<ExpScore>?, e: Throwable?) {
			Logs.wtfm("scoreListObserver: ", e)
			dismissLoading()
			e.toastLong(this@QueryExpScoreActivity)
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
		layoutParams.width = screenWidth
		scoreListRecyclerView.layoutParams = layoutParams
	}

	override fun initData() {
		super.initData()
		initViewModel()
		dialog.show()
		queryExpScoreViewModel.init()
	}

	private fun initViewModel() {
		queryExpScoreViewModel.student.observe(this, Observer {
			dialog.dismiss()
			if (it == null) {
				toastLong(R.string.hint_action_not_login)
				finish()
			}
			val text = "${it.studentName}(${it.username})"
			textViewStudent.text = text
		})
		queryExpScoreViewModel.year.observe(this, Observer {
			textViewYear.text = it
		})
		queryExpScoreViewModel.term.observe(this, Observer {
			textViewTerm.text = it
		})
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
			val studentList = queryExpScoreViewModel.studentList.value
			if (studentList.isNullOrEmpty()) {
				toastLong(R.string.hint_action_not_login)
				return@setOnClickListener
			}
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
						queryExpScoreViewModel.student.postValue(studentList[selectIndex])
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewYear.setOnClickListener {
			val studentInfoList = queryExpScoreViewModel.studentInfoList.value
			if (studentInfoList.isNullOrEmpty()) {
				toastLong(R.string.hint_action_not_login)
				return@setOnClickListener
			}
			val studentInfo = studentInfoList[queryExpScoreViewModel.student.value]
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
						queryExpScoreViewModel.year.postValue(yearTextArray[selectIndex])
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
						queryExpScoreViewModel.term.postValue(termTextArray[selectIndex])
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		queryButton.setOnClickListener {
			val student = queryExpScoreViewModel.student.value
			if (student == null) {
				toastLong(R.string.hint_action_not_login)
				return@setOnClickListener
			}
			queryExpScoreViewModel.query(student, queryExpScoreViewModel.year.value!!, queryExpScoreViewModel.term.value!!)
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
		toastLong(R.string.hint_data_null)
	}

	private fun showContent() {
		dismissLoading()
		drawerLayout.openDrawer(GravityCompat.END)
	}
}
