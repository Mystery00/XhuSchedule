/*
 * Created by Mystery0 on 17-12-3 上午1:24.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-3 上午1:24
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
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ExamAdapter
import com.weilylab.xhuschedule.classes.Exam
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.classes.rt.ExamRT
import com.weilylab.xhuschedule.classes.rt.LoginRT
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

import kotlinx.android.synthetic.main.activity_exam.*
import kotlinx.android.synthetic.main.content_exam.*
import java.io.File
import java.net.UnknownHostException

class ExamActivity : AppCompatActivity() {

    private lateinit var loadingDialog: ZLoadingDialog
    private var isTryRefreshData = false
    private var isTryLogin = false
    private val studentList = ArrayList<Student>()
    private val testList = ArrayList<Exam>()
    private lateinit var adapter: ExamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
        student.getTests()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ExamRT> {
                    private var examRT: ExamRT? = null
                    override fun onError(e: Throwable) {
                        loadingDialog.dismiss()
                        e.printStackTrace()
                        if (e is UnknownHostException)
                            Snackbar.make(coordinatorLayout, R.string.error_network, Snackbar.LENGTH_SHORT)
                                    .show()
                        else
                            Snackbar.make(coordinatorLayout, "请求出错：" + e.message + "，请重试", Snackbar.LENGTH_SHORT)
                                    .show()
                    }

                    override fun onNext(t: ExamRT) {
                        examRT = t
                    }

                    override fun onSubscribe(d: Disposable) {
                        loadingDialog.show()
                    }

                    override fun onComplete() {
                        when (examRT?.rt) {
                            "0" -> {
                                if (!isTryRefreshData) {
                                    isTryRefreshData = true
                                    getTests(student)
                                } else
                                    Snackbar.make(coordinatorLayout, R.string.error_timeout, Snackbar.LENGTH_LONG)
                                            .show()
                            }
                            "1" -> {
                                testList.clear()
                                testList.addAll(examRT?.tests!!)
                                adapter.clearList()
                                adapter.notifyDataSetChanged()
                            }
                            "2" -> Snackbar.make(coordinatorLayout, R.string.error_invalid_username, Snackbar.LENGTH_LONG)
                                    .show()
                            "3" -> Snackbar.make(coordinatorLayout, R.string.error_invalid_password, Snackbar.LENGTH_LONG)
                                    .show()
                            "6" -> {
                                login(student)
                                return
                            }
                        }
                        loadingDialog.dismiss()
                    }
                })
    }

    private fun login(student: Student) {
        student.login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<LoginRT> {
                    private var loginRT: LoginRT? = null
                    override fun onError(e: Throwable) {
                        loadingDialog.dismiss()
                        e.printStackTrace()
                        if (e is UnknownHostException)
                            Toast.makeText(this@ExamActivity, R.string.error_network, Toast.LENGTH_SHORT)
                                    .show()
                        else
                            Toast.makeText(this@ExamActivity, e.message, Toast.LENGTH_SHORT)
                                    .show()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {
                        when (loginRT?.rt) {
                            "0" -> {
                                if (!isTryLogin)
                                    login(student)
                                else {
                                    loadingDialog.dismiss()
                                    Snackbar.make(coordinatorLayout, R.string.error_timeout, Snackbar.LENGTH_LONG)
                                            .show()
                                }
                            }
                            "1" -> getTests(student)
                            "2" -> {
                                loadingDialog.dismiss()
                                Snackbar.make(coordinatorLayout, R.string.error_invalid_username, Snackbar.LENGTH_LONG)
                                        .show()
                            }
                            "3" -> {
                                loadingDialog.dismiss()
                                Snackbar.make(coordinatorLayout, R.string.error_invalid_password, Snackbar.LENGTH_LONG)
                                        .show()
                            }
                            else -> {
                                loadingDialog.dismiss()
                                Snackbar.make(coordinatorLayout, R.string.error_other, Snackbar.LENGTH_LONG)
                                        .show()
                            }
                        }
                    }

                    override fun onNext(t: LoginRT) {
                        loginRT = t
                    }
                })
    }
}
