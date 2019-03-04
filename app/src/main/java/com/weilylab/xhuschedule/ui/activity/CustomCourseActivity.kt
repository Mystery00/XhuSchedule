package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.jrummyapps.android.colorpicker.ColorPickerDialog
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.CustomCourseRepository
import com.weilylab.xhuschedule.ui.adapter.CustomCourseAdapter
import com.weilylab.xhuschedule.ui.adapter.CustomCourseWeekAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.viewmodel.CustomCourseViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_custom_course.*
import kotlinx.android.synthetic.main.layout_add_custom_course.*

import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.Status
import java.util.*

class CustomCourseActivity : XhuBaseActivity(R.layout.activity_custom_course) {
	private val customCourseViewModel: CustomCourseViewModel by lazy {
		ViewModelProviders.of(this).get(CustomCourseViewModel::class.java)
	}
	private val customCourseAdapter: CustomCourseAdapter by lazy { CustomCourseAdapter(this) }
	private val customCourseWeekAdapter: CustomCourseWeekAdapter by lazy { CustomCourseWeekAdapter(this) }
	private lateinit var viewStubBinding: LayoutNullDataViewBinding
	private val behavior by lazy { BottomSheetBehavior.from(nestedScrollView) }
	private var isUpdate = false
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
					customCourseViewModel.student.value = map.keys.first { it.isMain }
					customCourseViewModel.year.value = CalendarUtil.getSelectArray(null).last()
					val now = Calendar.getInstance()
					now.firstDayOfWeek = Calendar.MONDAY
					val month = now.get(Calendar.MONTH)
					val week = now.get(Calendar.DAY_OF_WEEK)
					customCourseViewModel.term.value = if (month in Calendar.MARCH until Calendar.SEPTEMBER) "2" else "1"
					customCourseViewModel.weekIndex.value = week
					customCourseViewModel.time.value = Pair(1, 1)
				}
				dialog.dismiss()
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

	private val timeObserver = Observer<Pair<Int, Int>> {
		val text = getString(R.string.prompt_custom_course_time, it.first, it.second)
		textViewTime.text = text
	}

	private val weekIndexObserver = Observer<Int> {
		textViewWeekIndex.text = CalendarUtil.getWeekIndexInString(it)
	}

	private val studentObserver = Observer<Student> {
		val text = getString(R.string.prompt_custom_course_student, it.studentName, it.username)
		textViewStudent.text = text
	}

	private val yearObserver = Observer<String> {
		val text = getString(R.string.prompt_custom_course_year, it)
		textViewYear.text = text
	}

	private val termObserver = Observer<String> {
		val text = getString(R.string.prompt_custom_course_term, it)
		textViewTerm.text = text
	}

	private val customCourseListObserver = Observer<PackageData<List<Course>>> {
		when (it.status) {
			Status.Loading -> showRefresh()
			Status.Content -> {
				hideRefresh()
				hideNoDataLayout()
				customCourseAdapter.items.clear()
				customCourseAdapter.items.addAll(it.data!!)
				customCourseAdapter.notifyDataSetChanged()
			}
			Status.Error -> {
				Logs.wtfm("customCourseListObserver: ", it.error)
				hideRefresh()
				hideNoDataLayout()
				toastMessage(it.error?.message)
			}
			Status.Empty -> {
				hideRefresh()
				showNoDataLayout()
			}
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = customCourseAdapter
		recyclerViewWeek.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
		recyclerViewWeek.adapter = customCourseWeekAdapter
		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light)
		swipeRefreshLayout.setDistanceToTriggerSync(100)
		hideAddLayout()
	}

	override fun initData() {
		super.initData()
		initViewModel()
	}

	override fun requestData() {
		super.requestData()
		CustomCourseRepository.queryAllStudentInfo(customCourseViewModel)
		refresh()
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			LayoutRefreshConfigUtil.isRefreshNoticeDot = true
			finish()
		}
		swipeRefreshLayout.setOnRefreshListener { refresh() }
		nullDataViewStub.setOnInflateListener { _, inflated -> viewStubBinding = DataBindingUtil.bind(inflated)!! }
		floatingActionButton.setOnClickListener { showAddLayout() }
		customCourseAdapter.setOnClickListener { showAddLayout(it) }
		ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
			override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
				return false
			}

			override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
				val position = viewHolder.adapterPosition
				val item = customCourseAdapter.items.removeAt(position)
				customCourseAdapter.notifyItemRemoved(position)
				Snackbar.make(coordinatorLayout, R.string.hint_delete_done_snackbar, Snackbar.LENGTH_LONG)
						.setAction(R.string.action_cancel_do) {
							customCourseAdapter.items.add(position, item)
							customCourseAdapter.notifyItemInserted(position)
						}
						.addCallback(object : Snackbar.Callback() {
							override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
								if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
									CustomCourseRepository.delete(item) {
										LayoutRefreshConfigUtil.isRefreshTodayFragment = true
									}
									super.onDismissed(transientBottomBar, event)
								}
							}
						})
						.show()
			}
		}).attachToRecyclerView(recyclerView)
		textViewTime.setOnClickListener {
			val timeStartTextArray = Array(11) { i -> (i + 1).toString() }
			val newTime = IntArray(2)
			val now = customCourseViewModel.time.value!!
			var selectIndex = now.first - 1
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_custom_course_choose_time_start)
					.setSingleChoiceItems(timeStartTextArray, selectIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						newTime[0] = selectIndex + 1
						val timeEndTextArray = Array(11 - newTime[0] + 1) { i -> (i + newTime[0]).toString() }
						selectIndex = 0
						AlertDialog.Builder(this)
								.setTitle(R.string.hint_dialog_custom_course_choose_time_end)
								.setSingleChoiceItems(timeEndTextArray, selectIndex) { _, index ->
									selectIndex = index
								}
								.setPositiveButton(R.string.action_ok) { _, _ ->
									newTime[1] = selectIndex + newTime[0]
									customCourseViewModel.time.value = Pair(newTime[0], newTime[1])
								}
								.setNegativeButton(R.string.action_cancel, null)
								.show()
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewWeekIndex.setOnClickListener {
			val termTextArray = Array(7) { i -> CalendarUtil.getWeekIndexInString(i + 1) }
			var selectIndex = customCourseViewModel.weekIndex.value!! - 1
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_custom_course_choose_week_index)
					.setSingleChoiceItems(termTextArray, selectIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						customCourseViewModel.weekIndex.value = selectIndex + 1
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewStudent.setOnClickListener {
			if (customCourseViewModel.studentList.value == null || customCourseViewModel.studentList.value!!.data == null || customCourseViewModel.studentList.value!!.data!!.isEmpty()) {
				toastMessage(R.string.hint_action_not_login, true)
				return@setOnClickListener
			}
			val map = customCourseViewModel.studentInfoList.value!!.data!!
			val studentList = map.keys.toList()
			val studentTextArray = Array(studentList.size) { i -> "${studentList[i].studentName}(${studentList[i].username})" }
			var nowIndex = studentList.indexOf(customCourseViewModel.student.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_custom_course_choose_student)
					.setSingleChoiceItems(studentTextArray, nowIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						customCourseViewModel.student.value = studentList[selectIndex]
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewYear.setOnClickListener {
			if (customCourseViewModel.studentList.value == null || customCourseViewModel.studentList.value!!.data == null || customCourseViewModel.studentList.value!!.data!!.isEmpty()) {
				toastMessage(R.string.hint_action_not_login, true)
				return@setOnClickListener
			}
			val map = customCourseViewModel.studentInfoList.value!!.data!!
			val studentInfo = map[customCourseViewModel.student.value]
			val yearTextArray = CalendarUtil.getSelectArray(studentInfo?.grade)
			var nowIndex = yearTextArray.indexOf(customCourseViewModel.year.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_custom_course_choose_year)
					.setSingleChoiceItems(yearTextArray, nowIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						customCourseViewModel.year.value = yearTextArray[selectIndex]
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		textViewTerm.setOnClickListener {
			val termTextArray = Array(3) { i -> (i + 1).toString() }
			var nowIndex = termTextArray.indexOf(customCourseViewModel.term.value)
			if (nowIndex == -1) nowIndex = 0
			var selectIndex = nowIndex
			AlertDialog.Builder(this)
					.setTitle(R.string.hint_dialog_custom_course_choose_term)
					.setSingleChoiceItems(termTextArray, nowIndex) { _, index ->
						selectIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						customCourseViewModel.term.value = termTextArray[selectIndex]
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
		}
		imageViewClose.setOnClickListener { hideAddLayout() }
		textViewColor.setOnClickListener {
			val color = imageViewColor.imageTintList!!.defaultColor
			val colorPickerDialog = ColorPickerDialog.newBuilder()
					.setDialogType(ColorPickerDialog.TYPE_PRESETS)
					.setColor(color)
					.setShowAlphaSlider(false)
					.setShowColorShades(false)
					.create()
			colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
				override fun onDialogDismissed(dialogId: Int) {
				}

				override fun onColorSelected(dialogId: Int, color: Int) {
					imageViewColor.imageTintList = ColorStateList.valueOf(color)
				}
			})
			colorPickerDialog.show(fragmentManager, "custom-course-color")
		}
	}

	private fun initViewModel() {
		customCourseViewModel.studentInfoList.observe(this, studentInfoListObserver)
		customCourseViewModel.time.observe(this, timeObserver)
		customCourseViewModel.weekIndex.observe(this, weekIndexObserver)
		customCourseViewModel.student.observe(this, studentObserver)
		customCourseViewModel.year.observe(this, yearObserver)
		customCourseViewModel.term.observe(this, termObserver)
		customCourseViewModel.customCourseList.observe(this, customCourseListObserver)
	}

	private fun refresh() {
		showRefresh()
		CustomCourseRepository.getAll(customCourseViewModel)
	}

	private fun showAddLayout(data: Course? = null) {
		if (data != null) {
			editTextName.setText(data.name)
			editTextTeacher.setText(data.teacher)
			val weekList = data.week.split(",")
			customCourseWeekAdapter.selectedList.clear()
			customCourseWeekAdapter.selectedList.addAll(weekList)
			customCourseWeekAdapter.notifyDataSetChanged()
			editTextLocation.setText(data.location)
			val timeArray = data.time.split("-").map { it.toInt() }
			customCourseViewModel.time.value = Pair(timeArray[0], timeArray[1])
			customCourseViewModel.weekIndex.value = data.day.toInt()
			imageViewColor.imageTintList = ColorStateList.valueOf(Color.parseColor(data.color))
			customCourseViewModel.year.value = data.year
			customCourseViewModel.term.value = data.term
			customCourseViewModel.student.value = customCourseViewModel.studentList.value!!.data!!.find { it.username == data.studentID }
		} else {
			editTextName.setText("")
			editTextTeacher.setText("")
			customCourseWeekAdapter.selectedList.clear()
			customCourseWeekAdapter.notifyDataSetChanged()
			editTextLocation.setText("")
			imageViewColor.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
		}
		isUpdate = data != null
		buttonSave.setOnClickListener {
			doSave(data ?: Course()) {
				if (it) {
					hideAddLayout()
					LayoutRefreshConfigUtil.isRefreshTodayFragment = true
					refresh()
				}
			}
		}
		behavior.state = BottomSheetBehavior.STATE_EXPANDED
	}

	private fun hideAddLayout() {
		behavior.state = BottomSheetBehavior.STATE_HIDDEN
	}

	private fun doSave(course: Course, listener: (Boolean) -> Unit) {
		course.name = editTextName.text.toString()
		if (course.name == "") {
			Snackbar.make(imageViewClose, R.string.hint_empty_couse_name, Snackbar.LENGTH_LONG)
					.show()
			listener.invoke(false)
			return
		}
		course.teacher = editTextTeacher.text.toString()
		if (customCourseWeekAdapter.selectedList.isEmpty()) {
			Snackbar.make(imageViewClose, R.string.hint_empty_couse_week, Snackbar.LENGTH_LONG)
					.show()
			listener.invoke(false)
			return
		}
		course.week = customCourseWeekAdapter.selectedList.joinToString(",")
		course.location = editTextLocation.text.toString()
		val pair = customCourseViewModel.time.value!!
		course.time = "${pair.first}-${pair.second}"
		course.day = customCourseViewModel.weekIndex.value.toString()
		course.color = ConfigUtil.toHexEncoding(imageViewColor.imageTintList!!.defaultColor)
		course.year = customCourseViewModel.year.value!!
		course.term = customCourseViewModel.term.value!!
		course.studentID = customCourseViewModel.student.value!!.username
		course.type = "0"
		course.editType = 1
		if (isUpdate)
			CustomCourseRepository.update(course) { b, t ->
				if (t != null)
					Snackbar.make(imageViewClose, t.message
							?: getString(R.string.error_db_action), Snackbar.LENGTH_SHORT)
							.show()
				listener.invoke(b)
			}
		else
			CustomCourseRepository.save(course) { b, t ->
				if (t != null)
					Snackbar.make(imageViewClose, t.message
							?: getString(R.string.error_db_action), Snackbar.LENGTH_SHORT)
							.show()
				listener.invoke(b)
			}
	}

	private fun showRefresh() {
		if (!swipeRefreshLayout.isRefreshing)
			swipeRefreshLayout.isRefreshing = true
	}

	private fun hideRefresh() {
		if (swipeRefreshLayout.isRefreshing)
			swipeRefreshLayout.isRefreshing = false
	}

	private fun showNoDataLayout() {
		try {
			nullDataViewStub.inflate()
		} catch (e: Exception) {
			viewStubBinding.root.visibility = View.VISIBLE
		}
		recyclerView.visibility = View.GONE
	}

	private fun hideNoDataLayout() {
		if (::viewStubBinding.isInitialized)
			viewStubBinding.root.visibility = View.GONE
		recyclerView.visibility = View.VISIBLE
	}

	override fun onBackPressed() {
		if (behavior.state != BottomSheetBehavior.STATE_HIDDEN)
			behavior.state = BottomSheetBehavior.STATE_HIDDEN
		else
			super.onBackPressed()
	}
}
