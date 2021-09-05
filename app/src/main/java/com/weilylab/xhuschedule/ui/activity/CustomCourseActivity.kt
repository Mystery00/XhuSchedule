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
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.model.event.UI
import com.weilylab.xhuschedule.model.event.UIConfigEvent
import com.weilylab.xhuschedule.ui.adapter.CustomCourseAdapter
import com.weilylab.xhuschedule.ui.adapter.CustomCourseWeekAdapter
import com.weilylab.xhuschedule.utils.AnimationUtil
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.viewmodel.CustomCourseViewModel
import kotlinx.android.synthetic.main.activity_custom_course.*
import kotlinx.android.synthetic.main.layout_add_custom_course.*
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import vip.mystery0.rx.DataObserver

class CustomCourseActivity : XhuBaseActivity(R.layout.activity_custom_course), KoinComponent {
    private val customCourseViewModel: CustomCourseViewModel by viewModel()
    private val eventBus: EventBus by inject()
    private val customCourseAdapter: CustomCourseAdapter by lazy { CustomCourseAdapter(this) }
    private val customCourseWeekAdapter: CustomCourseWeekAdapter by lazy {
        CustomCourseWeekAdapter(
            this
        )
    }
    private lateinit var viewStubBinding: LayoutNullDataViewBinding
    private val behavior by lazy { BottomSheetBehavior.from(nestedScrollView) }
    private var isUpdate = false
    private var collapsedHeight = 0
    private var expandedHeight = 0
    private val dialog: Dialog by lazy { buildDialog(R.string.hint_dialog_init) }

    private val studentInfoListObserver = object : DataObserver<Map<Student, StudentInfo?>> {
        override fun loading() {
            dialog.show()
        }

        override fun contentNoEmpty(data: Map<Student, StudentInfo?>) {
            dialog.dismiss()
        }

        override fun error(e: Throwable?) {
            dialog.dismiss()
            Log.e(TAG, "error: ", e)
            toastLong(R.string.error_init_failed)
            finish()
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
        expandLayout.text = getString(R.string.hint_custom_course_sync, it.studentName)
    }

    private val yearObserver = Observer<String> {
        val text = getString(R.string.prompt_custom_course_year, it)
        textViewYear.text = text
    }

    private val termObserver = Observer<String> {
        val text = getString(R.string.prompt_custom_course_term, it)
        textViewTerm.text = text
    }

    private val customCourseListObserver = object : DataObserver<List<Any>> {
        override fun loading() {
            showRefresh()
        }

        override fun contentNoEmpty(data: List<Any>) {
            hideRefresh()
            toast(R.string.hint_course_sync_done)
            customCourseAdapter.items.clear()
            customCourseAdapter.items.addAll(data)
            customCourseAdapter.updateMap()
            checkData()
        }

        override fun error(e: Throwable?) {
            Log.e(TAG, "error: ", e)
            hideRefresh()
            checkData()
            toastLong(e)
        }

        override fun empty() {
            toast(R.string.hint_data_null)
            hideRefresh()
            showNoDataLayout()
        }
    }

    override fun initView() {
        super.initView()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customCourseAdapter
        recyclerViewWeek.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewWeek.adapter = customCourseWeekAdapter
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_light,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        swipeRefreshLayout.setDistanceToTriggerSync(100)
        hideAddLayout()
        initExpand()
    }

    override fun initData() {
        super.initData()
        initViewModel()
    }

    override fun requestData() {
        super.requestData()
        customCourseViewModel.init()
        refresh()
    }

    override fun monitor() {
        super.monitor()
        toolbar.setNavigationOnClickListener { finish() }
        swipeRefreshLayout.setOnRefreshListener { refresh() }
        nullDataViewStub.setOnInflateListener { _, inflated ->
            viewStubBinding = DataBindingUtil.bind(inflated)!!
        }
        floatingActionButton.setOnClickListener { showAddLayout() }
        customCourseAdapter.setOnClickListener { showAddLayout(it) }
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (customCourseAdapter.items[position] is Course) {
                    val item = customCourseAdapter.items.removeAt(position) as Course
                    checkData()
                    Snackbar.make(
                        coordinatorLayout,
                        R.string.hint_delete_done_snackbar,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.action_cancel_do) {
                            customCourseAdapter.items.add(position, item)
                            checkData()
                        }
                        .addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                if (event != DISMISS_EVENT_ACTION) {
                                    customCourseViewModel.deleteCustomCourse(item) {
                                        eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
                                        customCourseAdapter.updateMap()
                                    }
                                    super.onDismissed(transientBottomBar, event)
                                }
                            }
                        })
                        .show()
                } else {
                    customCourseAdapter.notifyItemChanged(position)
                }
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
                    val timeEndTextArray =
                        Array(11 - newTime[0] + 1) { i -> (i + newTime[0]).toString() }
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
            if (customCourseViewModel.studentList.value == null || customCourseViewModel.studentList.value!!.isEmpty()) {
                toastLong(R.string.hint_action_not_login)
                return@setOnClickListener
            }
            val map = customCourseViewModel.studentInfoList.value!!.data!!
            val studentList = map.keys.toList()
            val studentTextArray =
                Array(studentList.size) { i -> "${studentList[i].studentName}(${studentList[i].username})" }
            var nowIndex = studentList.indexOf(customCourseViewModel.mainStudent.value)
            if (nowIndex == -1) nowIndex = 0
            var selectIndex = nowIndex
            AlertDialog.Builder(this)
                .setTitle(R.string.hint_dialog_custom_course_choose_student)
                .setSingleChoiceItems(studentTextArray, nowIndex) { _, index ->
                    selectIndex = index
                }
                .setPositiveButton(R.string.action_ok) { _, _ ->
                    customCourseViewModel.mainStudent.value = studentList[selectIndex]
                }
                .setNegativeButton(R.string.action_cancel, null)
                .show()
        }
        textViewYear.setOnClickListener {
            if (customCourseViewModel.studentList.value == null || customCourseViewModel.studentList.value!!.isEmpty()) {
                toastLong(R.string.hint_action_not_login)
                return@setOnClickListener
            }
            val map = customCourseViewModel.studentInfoList.value!!.data!!
            val studentInfo = map[customCourseViewModel.mainStudent.value]
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
            val termTextArray = Array(2) { i -> (i + 1).toString() }
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
            colorPickerDialog.show(supportFragmentManager, "custom-course-color")
        }
        expandClose.setOnClickListener {
            collapse()
            expandClose.visibility = View.GONE
        }
        expandLayout.setOnClickListener {
            expand()
            expandClose.visibility = View.VISIBLE
        }
    }

    private fun initViewModel() {
        customCourseViewModel.studentInfoList.observe(this, studentInfoListObserver)
        customCourseViewModel.time.observe(this, timeObserver)
        customCourseViewModel.weekIndex.observe(this, weekIndexObserver)
        customCourseViewModel.mainStudent.observe(this, studentObserver)
        customCourseViewModel.year.observe(this, yearObserver)
        customCourseViewModel.term.observe(this, termObserver)
        customCourseViewModel.customCourseList.observe(this, customCourseListObserver)
    }

    private fun initExpand() {
        expandLayout.post {
            collapsedHeight = expandLayout.measuredHeight
            expandLayout.maxLines = Int.MAX_VALUE
            expandLayout.postInvalidate()
            expandLayout.post {
                expandedHeight = expandLayout.measuredHeight
                expandLayout.layoutParams.height = expandedHeight
                expandLayout.requestLayout()
            }
        }
    }

    private fun refresh() {
        showRefresh()
        customCourseViewModel.getAllCustomCourse()
    }

    private fun expand() {
        if (collapsedHeight == 0 || expandedHeight == 0)
            return
        AnimationUtil.expandLayout(expandLayout, collapsedHeight, expandedHeight)
    }

    private fun collapse() {
        if (collapsedHeight == 0 || expandedHeight == 0)
            return
        AnimationUtil.expandLayout(expandLayout, expandedHeight, collapsedHeight)
    }

    private fun showAddLayout(data: Course? = null) {
        if (data != null) {
            editTextName.setText(data.name)
            editTextTeacher.setText(data.teacher)
            val weekList = data.week.split(",")
            customCourseWeekAdapter.selectedList.clear()
            customCourseWeekAdapter.selectedList.addAll(weekList)
            editTextLocation.setText(data.location)
            val timeArray = data.time.split("-").map { it.toInt() }
            customCourseViewModel.time.postValue(Pair(timeArray[0], timeArray[1]))
            customCourseViewModel.weekIndex.postValue(data.day.toInt())
            imageViewColor.imageTintList = ColorStateList.valueOf(Color.parseColor(data.color))
            customCourseViewModel.year.postValue(data.year)
            customCourseViewModel.term.postValue(data.term)
            customCourseViewModel.mainStudent.postValue(customCourseViewModel.studentList.value!!.find { it.username == data.studentID })
        } else {
            editTextName.setText("")
            editTextTeacher.setText("")
            customCourseWeekAdapter.selectedList.clear()
            editTextLocation.setText("")
            imageViewColor.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
        }
        isUpdate = data != null
        buttonSave.setOnClickListener {
            doSave(data ?: Course()) {
                hideAddLayout()
                eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
                refresh()
            }
        }
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideAddLayout() {
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun doSave(course: Course, listener: () -> Unit) {
        course.name = editTextName.text.toString()
        if (course.name == "") {
            Snackbar.make(imageViewClose, R.string.hint_empty_couse_name, Snackbar.LENGTH_LONG)
                .show()
            return
        }
        course.teacher = editTextTeacher.text.toString()
        if (customCourseWeekAdapter.selectedList.isEmpty()) {
            Snackbar.make(imageViewClose, R.string.hint_empty_couse_week, Snackbar.LENGTH_LONG)
                .show()
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
        course.studentID = customCourseViewModel.mainStudent.value!!.username
        course.type = "0"
        course.editType = 1
        if (isUpdate)
            customCourseViewModel.updateCustomCourse(course, listener)
        else
            customCourseViewModel.saveCustomCourse(course, listener)
    }

    private fun showRefresh() {
        if (!swipeRefreshLayout.isRefreshing)
            swipeRefreshLayout.isRefreshing = true
    }

    private fun hideRefresh() {
        if (swipeRefreshLayout.isRefreshing)
            swipeRefreshLayout.isRefreshing = false
    }

    private fun checkData() {
        if (customCourseAdapter.items.isEmpty())
            showNoDataLayout()
        else
            hideNoDataLayout()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_custom_course_thing, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_upload -> {
                val main = customCourseViewModel.mainStudent.value
                if (main == null)
                    toast(R.string.error_init_failed)
                else {
                    customCourseViewModel.syncForRemote(main)
                    eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
                }
                true
            }
            R.id.action_download -> {
                val main = customCourseViewModel.mainStudent.value
                if (main == null)
                    toast(R.string.error_init_failed)
                else {
                    customCourseViewModel.syncForLocal(main)
                    eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        customCourseAdapter.release()
    }

    companion object {
        private const val TAG = "CustomCourseActivity"
    }
}
