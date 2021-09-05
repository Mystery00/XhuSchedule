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
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.ui.adapter.QueryClassScoreRecyclerViewAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.viewmodel.QueryClassScoreViewModel
import kotlinx.android.synthetic.main.activity_query_class_score.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.rx.PackageDataObserver
import vip.mystery0.tools.toastLong
import vip.mystery0.tools.utils.screenWidth


class QueryClassScoreActivity : XhuBaseActivity(R.layout.activity_query_class_score) {
    private val queryClassScoreViewModel: QueryClassScoreViewModel by viewModel()
    private val shortcutManager: ShortcutManager by inject()
    private val queryClassScoreRecyclerViewAdapter: QueryClassScoreRecyclerViewAdapter by lazy { QueryClassScoreRecyclerViewAdapter(this) }
    private var hasData = false
    private val dialog: Dialog by lazy { buildDialog(R.string.hint_dialog_init) }

    private val scoreListObserver = object : PackageDataObserver<List<ClassScore>> {
        override fun loading() {
            showLoading()
        }

        override fun empty(data: List<ClassScore>?) {
            showEmpty()
        }

        override fun content(data: List<ClassScore>?) {
            hasData = true
            updateScoreList(data!!)
            showContent()
        }

        override fun error(data: List<ClassScore>?, e: Throwable?) {
            Log.e(TAG, "error: ", e)
            dismissLoading()
            e.toastLong(this@QueryClassScoreActivity)
        }
    }

    override fun initView() {
        super.initView()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        scoreListRecyclerView.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        scoreListRecyclerView.addItemDecoration(dividerItemDecoration)
        scoreListRecyclerView.adapter = queryClassScoreRecyclerViewAdapter
        val layoutParams = scoreListRecyclerView.layoutParams
        layoutParams.width = screenWidth
        scoreListRecyclerView.layoutParams = layoutParams
    }

    override fun initData() {
        super.initData()
        initViewModel()
        dialog.show()
        queryClassScoreViewModel.init()
        //添加Shortcut
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val intent = Intent(this, QueryClassScoreActivity::class.java)
            intent.action = "com.weilylab.xhuschedule.QUERY_CLASS_SCORE"
            val shortcutInfoBuilder = ShortcutInfo.Builder(this, "shortcut_title_query_class_score")
                    .setShortLabel(getString(R.string.shortcut_title_query_class_score))
                    .setLongLabel(getString(R.string.shortcut_title_query_class_score))
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_short_query_score))
                    .setIntent(intent)
            shortcutManager.addDynamicShortcuts(listOf(shortcutInfoBuilder.build()))
        }
    }

    private fun initViewModel() {
        queryClassScoreViewModel.student.observe(this, Observer {
            dialog.dismiss()
            if (it == null) {
                toastLong(R.string.hint_action_not_login)
                finish()
            }
            val text = "${it.studentName}(${it.username})"
            textViewStudent.text = text
        })
        queryClassScoreViewModel.year.observe(this, Observer {
            textViewYear.text = it
        })
        queryClassScoreViewModel.term.observe(this, Observer {
            textViewTerm.text = it
        })
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
                    drawerLayout.closeDrawer(GravityCompat.END)
            }
        })
        textViewStudent.setOnClickListener {
            val studentList = queryClassScoreViewModel.studentList.value
            if (studentList.isNullOrEmpty()) {
                toastLong(R.string.hint_action_not_login)
                return@setOnClickListener
            }
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
                        queryClassScoreViewModel.student.postValue(studentList[selectIndex])
                    }
                    .setNegativeButton(R.string.action_cancel, null)
                    .show()
        }
        textViewYear.setOnClickListener {
            val studentInfoList = queryClassScoreViewModel.studentInfoList.value
            if (studentInfoList.isNullOrEmpty()) {
                toastLong(R.string.hint_action_not_login)
                return@setOnClickListener
            }
            val studentInfo = studentInfoList[queryClassScoreViewModel.student.value]
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
            val termTextArray = Array(2) { i -> (i + 1).toString() }
            var nowIndex = termTextArray.indexOf(queryClassScoreViewModel.term.value)
            if (nowIndex == -1) nowIndex = 0
            var selectIndex = nowIndex
            AlertDialog.Builder(this)
                    .setTitle(R.string.hint_dialog_choose_term)
                    .setSingleChoiceItems(termTextArray, nowIndex) { _, index ->
                        selectIndex = index
                    }
                    .setPositiveButton(R.string.action_ok) { _, _ ->
                        queryClassScoreViewModel.term.postValue(termTextArray[selectIndex])
                    }
                    .setNegativeButton(R.string.action_cancel, null)
                    .show()
        }
        queryButton.setOnClickListener {
            val student = queryClassScoreViewModel.student.value
            if (student == null) {
                toastLong(R.string.hint_action_not_login)
                return@setOnClickListener
            }
            queryClassScoreViewModel.query(student, queryClassScoreViewModel.year.value!!, queryClassScoreViewModel.term.value!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_query_score, menu)
        menu.findItem(R.id.action_show_gpa).isChecked = ConfigurationUtil.isShowGpa
        menu.findItem(R.id.action_show_credit).isChecked = ConfigurationUtil.isShowCredit
        menu.findItem(R.id.action_show_course_type).isChecked = ConfigurationUtil.isShowCourseType
        menu.findItem(R.id.action_show_failed).isChecked = ConfigurationUtil.isShowFailed
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_show_gpa -> {
                item.isChecked = !item.isChecked
                ConfigurationUtil.isShowGpa = item.isChecked
                queryClassScoreRecyclerViewAdapter.notifyDataSetChanged()
                true
            }
            R.id.action_show_credit -> {
                item.isChecked = !item.isChecked
                ConfigurationUtil.isShowCredit = item.isChecked
                if (queryClassScoreViewModel.scoreList.value != null && queryClassScoreViewModel.scoreList.value!!.data != null)
                    updateScoreList(queryClassScoreViewModel.scoreList.value!!.data!!)
                true
            }
            R.id.action_show_course_type -> {
                item.isChecked = !item.isChecked
                ConfigurationUtil.isShowCourseType = item.isChecked
                if (queryClassScoreViewModel.scoreList.value != null && queryClassScoreViewModel.scoreList.value!!.data != null)
                    updateScoreList(queryClassScoreViewModel.scoreList.value!!.data!!)
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
        if (drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.closeDrawer(GravityCompat.END)
        else
            super.onBackPressed()
    }

    private fun updateScoreList(list: List<ClassScore>) {
        queryClassScoreRecyclerViewAdapter.items.clear()
        if (!ConfigurationUtil.isShowFailed)
            queryClassScoreRecyclerViewAdapter.items.addAll(list.filter { !it.failed })
        else
            queryClassScoreRecyclerViewAdapter.items.addAll(list)
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

    companion object {
        private const val TAG = "QueryClassScoreActivity"
    }
}
