/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

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
import com.weilylab.xhuschedule.model.Classroom
import com.weilylab.xhuschedule.ui.adapter.QueryClassroomRecyclerViewAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.viewmodel.QueryClassroomViewModel
import kotlinx.android.synthetic.main.activity_query_class_room.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver
import vip.mystery0.tools.utils.screenWidth

class QueryClassroomActivity : XhuBaseActivity(R.layout.activity_query_class_room) {
	private val queryClassroomViewModel: QueryClassroomViewModel by viewModel()
	private val queryClassroomRecyclerViewAdapter: QueryClassroomRecyclerViewAdapter by lazy { QueryClassroomRecyclerViewAdapter(this) }
	private var hasData = false
	private val dialog: Dialog by lazy { buildDialog(R.string.hint_dialog_init) }

	private val classroomObserver = object : DataObserver<List<Classroom>> {
		override fun loading() {
			showLoading()
		}

		override fun empty() {
			showEmpty()
		}

		override fun contentNoEmpty(data: List<Classroom>) {
			hasData = true
			updateClassroomList(data)
			showContent()
		}

		override fun error(e: Throwable?) {
			Logs.w(e)
			dismissLoading()
			toastLong(e)
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		scoreListRecyclerView.layoutManager = LinearLayoutManager(this)
		val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
		scoreListRecyclerView.addItemDecoration(dividerItemDecoration)
		scoreListRecyclerView.adapter = queryClassroomRecyclerViewAdapter
		val layoutParams = scoreListRecyclerView.layoutParams
		layoutParams.width = screenWidth
		scoreListRecyclerView.layoutParams = layoutParams

		textViewLocation.setText(R.string.hint_dialog_choose_location)
		textViewWeek.setText(R.string.hint_dialog_choose_week)
		textViewDay.setText(R.string.hint_dialog_choose_day)
		textViewTime.setText(R.string.hint_dialog_choose_time)
	}

	override fun initData() {
		super.initData()
		initViewModel()
		queryClassroomViewModel.init()
	}

	private fun initViewModel() {
		queryClassroomViewModel.student.observe(this, Observer {
			dialog.dismiss()
			if (it == null) {
				toastLong(R.string.hint_action_not_login)
				finish()
			}
		})
		queryClassroomViewModel.location.observe(this, Observer { textViewLocation.text = it })
		queryClassroomViewModel.week.observe(this, Observer {
			val string = "第${it.replace(",", "，")}周"
			textViewWeek.text = string
		})
		queryClassroomViewModel.day.observe(this, Observer { s ->
			val list = s.split(",")
			val string = list.joinToString("，") { CalendarUtil.getWeekIndexInString(it.toInt()) }
			textViewDay.text = string
		})
		queryClassroomViewModel.time.observe(this, Observer {
			val string = "第${it.replace(",", "，")}节"
			textViewTime.text = string
		})
		queryClassroomViewModel.classroomList.observe(this, classroomObserver)
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
		textViewLocation.setOnClickListener {
			if (queryClassroomViewModel.student.value == null) {
				toastLong(R.string.hint_action_not_login)
				return@setOnClickListener
			}
			val array = resources.getStringArray(R.array.classroom_location)
			var nowIndex = array.indexOf(queryClassroomViewModel.location.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_choose_location)
					.setSingleChoiceItems(array, nowIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						queryClassroomViewModel.location.value = array[selectIndex]
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewWeek.setOnClickListener {
			if (queryClassroomViewModel.student.value == null) {
				toastLong(R.string.hint_action_not_login)
				return@setOnClickListener
			}
			val array = Array(20) { i -> "第${i + 1}周" }
			val text = queryClassroomViewModel.week.value
			val checked = if (text == null) {
				BooleanArray(20)
			} else {
				BooleanArray(20) { i -> text.contains((i + 1).toString()) }
			}
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_choose_week)
					.setMultiChoiceItems(array, checked) { _, index, b ->
						checked[index] = b
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						queryClassroomViewModel.week.value = checked.mapIndexed { index, b -> if (b) index + 1 else 0 }.filter { it != 0 }.joinToString(",")
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewDay.setOnClickListener {
			if (queryClassroomViewModel.student.value == null) {
				toastLong(R.string.hint_action_not_login)
				return@setOnClickListener
			}
			val array = Array(7) { i -> CalendarUtil.getWeekIndexInString(i + 1) }
			val text = queryClassroomViewModel.day.value
			val checked = if (text == null) {
				BooleanArray(20)
			} else {
				BooleanArray(20) { i -> text.contains((i + 1).toString()) }
			}
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_choose_day)
					.setMultiChoiceItems(array, checked) { _, index, b ->
						checked[index] = b
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						queryClassroomViewModel.day.value = checked.mapIndexed { index, b -> if (b) index + 1 else 0 }.filter { it != 0 }.joinToString(",")
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewTime.setOnClickListener {
			if (queryClassroomViewModel.student.value == null) {
				toastLong(R.string.hint_action_not_login)
				return@setOnClickListener
			}
			val array = Array(11) { i -> "第${i + 1}节" }
			val text = queryClassroomViewModel.time.value
			val checked = if (text == null) {
				BooleanArray(20)
			} else {
				BooleanArray(20) { i -> text.contains((i + 1).toString()) }
			}
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_choose_time)
					.setMultiChoiceItems(array, checked) { _, index, b ->
						checked[index] = b
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						queryClassroomViewModel.time.value = checked.mapIndexed { index, b -> if (b) index + 1 else 0 }.filter { it != 0 }.joinToString(",")
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		queryButton.setOnClickListener {
			val student = queryClassroomViewModel.student.value
			if (student == null) {
				toastLong(R.string.hint_action_not_login)
				return@setOnClickListener
			}
			val location = queryClassroomViewModel.location.value
			if (location.isNullOrEmpty()) {
				toastLong(R.string.hint_dialog_choose_location)
				return@setOnClickListener
			}
			val week = queryClassroomViewModel.week.value
			if (week.isNullOrEmpty()) {
				toastLong(R.string.hint_dialog_choose_week)
				return@setOnClickListener
			}
			val day = queryClassroomViewModel.day.value
			if (day.isNullOrEmpty()) {
				toastLong(R.string.hint_dialog_choose_day)
				return@setOnClickListener
			}
			val time = queryClassroomViewModel.time.value
			if (time.isNullOrEmpty()) {
				toastLong(R.string.hint_dialog_choose_time)
				return@setOnClickListener
			}
			queryClassroomViewModel.queryClassRoomList(student, location, week, day, time)
		}
	}

	override fun onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.END))
			drawerLayout.closeDrawer(GravityCompat.END)
		else
			super.onBackPressed()
	}

	private fun updateClassroomList(list: List<Classroom>) {
		queryClassroomRecyclerViewAdapter.items.clear()
		queryClassroomRecyclerViewAdapter.items.addAll(list)
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
