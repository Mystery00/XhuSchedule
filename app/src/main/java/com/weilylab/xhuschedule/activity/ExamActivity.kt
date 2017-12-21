/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-15 下午7:43
 */

package com.weilylab.xhuschedule.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ExamAdapter
import com.weilylab.xhuschedule.classes.Exam
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.listener.GetArrayListener
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE

import kotlinx.android.synthetic.main.activity_exam.*
import kotlinx.android.synthetic.main.content_exam.*
import java.io.File

class ExamActivity : AppCompatActivity() {

    private lateinit var loadingDialog: ZLoadingDialog
    private val studentList = ArrayList<Student>()
    private val testList = ArrayList<Exam>()
    private lateinit var adapter: ExamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        initView()
    }

    private fun initView() {
        loadingDialog = ZLoadingDialog(this)
                .setLoadingBuilder(Z_TYPE.SEARCH_PATH)
                .setHintText(getString(R.string.hint_dialog_sync))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        adapter = ExamAdapter(this, testList)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
        studentList.clear()
        studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
        val array = Array(studentList.size, { i -> "${studentList[i].name}(${studentList[i].username})" })
        val arrayAdapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, array)
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item)
        spinner_student.adapter = arrayAdapter
        spinner_student.setSelection(0)
        spinner_student.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                getTests(studentList[position])
            }
        }
    }

    private fun getTests(student: Student) {
        loadingDialog.show()
        student.getTests(this,object :GetArrayListener<Exam>{
            override fun error(rt: Int, e: Throwable) {
                loadingDialog.dismiss()
                e.printStackTrace()
                Snackbar.make(coordinatorLayout, e.message!!, Snackbar.LENGTH_SHORT)
                        .show()
            }

            override fun got(array: Array<Exam>) {
                loadingDialog.dismiss()
                testList.clear()
                testList.addAll(array)
                adapter.clearList()
                adapter.notifyDataSetChanged()
            }

            override fun doInThread() {
            }
        })
    }
}
