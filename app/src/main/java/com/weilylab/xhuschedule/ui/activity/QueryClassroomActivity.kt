package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.Classroom
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.ClassRoomRepository
import com.weilylab.xhuschedule.ui.adapter.QueryClassroomRecyclerViewAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.viewmodel.QueryClassroomViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_query_class_room.*
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageDataObserver
import vip.mystery0.tools.toastLong
import vip.mystery0.tools.utils.DensityTools

class QueryClassroomActivity : XhuBaseActivity(R.layout.activity_query_class_room) {
	private val queryClassroomViewModel: QueryClassroomViewModel by lazy {
		ViewModelProvider(this)[QueryClassroomViewModel::class.java]
	}
	private val queryClassroomRecyclerViewAdapter: QueryClassroomRecyclerViewAdapter by lazy { QueryClassroomRecyclerViewAdapter(this) }
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

	private val studentObserver = object : PackageDataObserver<Student> {
		override fun loading() {
			dialog.show()
		}

		override fun content(data: Student?) {
			dialog.dismiss()
		}

		override fun error(data: Student?, e: Throwable?) {
			dialog.dismiss()
			e.toastLong(this@QueryClassroomActivity)
			finish()
		}

		override fun empty(data: Student?) {
			toastMessage(R.string.error_init_failed)
		}
	}

	private val locationObserver = Observer<String> {
		textViewLocation.text = it
	}

	private val weekObserver = Observer<String> {
		val string = "第${it.replace(",", "，")}周"
		textViewWeek.text = string
	}

	private val dayObserver = Observer<String> { s ->
		val list = s.split(",")
		val string = list.joinToString("，") { CalendarUtil.getWeekIndexInString(it.toInt()) }
		textViewDay.text = string
	}

	private val timeObserver = Observer<String> {
		val string = "第${it.replace(",", "，")}节"
		textViewTime.text = string
	}

	private val classroomObserver = object : PackageDataObserver<List<Classroom>> {
		override fun loading() {
			showLoading()
		}

		override fun empty(data: List<Classroom>?) {
			showEmpty()
		}

		override fun content(data: List<Classroom>?) {
			hasData = true
			updateClassroomList(data!!)
			showContent()
		}

		override fun error(data: List<Classroom>?, e: Throwable?) {
			Logs.wtfm("scoreListObserver: ", e)
			dismissLoading()
			e.toastLong(this@QueryClassroomActivity)
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
		layoutParams.width = DensityTools.instance.getScreenWidth()
		scoreListRecyclerView.layoutParams = layoutParams

		textViewLocation.setText(R.string.hint_dialog_choose_location)
		textViewWeek.setText(R.string.hint_dialog_choose_week)
		textViewDay.setText(R.string.hint_dialog_choose_day)
		textViewTime.setText(R.string.hint_dialog_choose_time)
	}

	override fun initData() {
		super.initData()
		initViewModel()
		ClassRoomRepository.queryStudentList(queryClassroomViewModel)
	}

	private fun initViewModel() {
		queryClassroomViewModel.student.observe(this, studentObserver)
		queryClassroomViewModel.location.observe(this, locationObserver)
		queryClassroomViewModel.week.observe(this, weekObserver)
		queryClassroomViewModel.day.observe(this, dayObserver)
		queryClassroomViewModel.time.observe(this, timeObserver)
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
			if (queryClassroomViewModel.student.value == null || queryClassroomViewModel.student.value!!.data == null) {
				toastMessage(R.string.hint_action_not_login, true)
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
			if (queryClassroomViewModel.student.value == null || queryClassroomViewModel.student.value!!.data == null) {
				toastMessage(R.string.hint_action_not_login, true)
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
			if (queryClassroomViewModel.student.value == null || queryClassroomViewModel.student.value!!.data == null) {
				toastMessage(R.string.hint_action_not_login, true)
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
			if (queryClassroomViewModel.student.value == null || queryClassroomViewModel.student.value!!.data == null) {
				toastMessage(R.string.hint_action_not_login, true)
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
			if (queryClassroomViewModel.student.value == null || queryClassroomViewModel.student.value!!.data == null) {
				toastMessage(R.string.hint_action_not_login, true)
				return@setOnClickListener
			}
			if (queryClassroomViewModel.location.value.isNullOrEmpty()) {
				toastMessage(R.string.hint_dialog_choose_location, true)
				return@setOnClickListener
			}
			if (queryClassroomViewModel.week.value.isNullOrEmpty()) {
				toastMessage(R.string.hint_dialog_choose_week, true)
				return@setOnClickListener
			}
			if (queryClassroomViewModel.day.value.isNullOrEmpty()) {
				toastMessage(R.string.hint_dialog_choose_day, true)
				return@setOnClickListener
			}
			if (queryClassroomViewModel.time.value.isNullOrEmpty()) {
				toastMessage(R.string.hint_dialog_choose_time, true)
				return@setOnClickListener
			}
			ClassRoomRepository.queryClassroom(queryClassroomViewModel)
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
		toastMessage(R.string.hint_data_null, true)
	}

	private fun showContent() {
		dismissLoading()
		drawerLayout.openDrawer(GravityCompat.END)
	}
}
