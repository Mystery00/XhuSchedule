/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-15 下午7:43
 */

package com.weilylab.xhuschedule.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ExamAdapter
import com.weilylab.xhuschedule.classes.Exam
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.listener.GetArrayListener
import com.weilylab.xhuschedule.util.ViewUtil
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.weilylab.xhuschedule.util.widget.WidgetHelper
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
        ViewUtil.setPopupView(this, array, textViewStudent, { position ->
            getTests(studentList[position])
        })
    }

    private fun getTests(student: Student) {
        loadingDialog.show()
        student.getTests(this, object : GetArrayListener<Exam> {
            override fun error(rt: Int, e: Throwable) {
                loadingDialog.dismiss()
                e.printStackTrace()
                Snackbar.make(coordinatorLayout, e.message!!, Snackbar.LENGTH_SHORT)
                        .show()
            }

            override fun got(array: Array<Exam>) {
                testList.clear()
                testList.addAll(array)
                adapter.notifyDataSetChanged()
                val parentFile = File(filesDir.absolutePath + File.separator + "exam/")
                if (!parentFile.exists())
                    parentFile.mkdirs()
                val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
                val savedFile = File(parentFile, base64Name)
                savedFile.createNewFile()
                XhuFileUtil.saveObjectToFile(testList, savedFile)
                sendBroadcast(Intent("android.appwidget.action.APPWIDGET_UPDATE")
                        .putExtra("TAG", WidgetHelper.ALL_TAG))
                loadingDialog.dismiss()
            }

            override fun doInThread() {
            }
        })
    }
}
