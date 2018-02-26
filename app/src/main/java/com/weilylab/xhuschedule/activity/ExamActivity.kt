/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.view.MenuItem
import com.google.firebase.analytics.FirebaseAnalytics
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ExamAdapter
import com.weilylab.xhuschedule.classes.baseClass.Exam
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.listener.GetArrayListener
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.weilylab.xhuschedule.util.widget.WidgetHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_exam.*
import kotlinx.android.synthetic.main.content_exam.*
import vip.mystery0.tools.logs.Logs
import java.io.File

class ExamActivity : BaseActivity() {
    private val TAG = "ExamActivity"
    private lateinit var loadingDialog: ZLoadingDialog
    private val studentList = ArrayList<Student>()
    private val testList = ArrayList<Exam>()
    private lateinit var adapter: ExamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exam")
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params)
        setContentView(R.layout.activity_exam)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initView()
    }

    private fun initView() {
        loadingDialog = ZLoadingDialog(this)
                .setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
                .setHintText(getString(R.string.hint_dialog_sync))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        adapter = ExamAdapter(this, testList)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
        studentList.clear()
        studentList.addAll(XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(this), Student::class.java))
        initInfo()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initInfo() {
        val studentArray = Array(studentList.size, { i -> studentList[i].username })
        spinner_username.setItems(studentArray.toList())
        spinner_username.setOnItemSelectedListener { _, _, _, username ->
            setUsername(username.toString())
        }
        if (studentList.size == 1) {
            spinner_username.selectedIndex = 0
            setUsername(studentArray[0])
        }
    }

    private fun setUsername(username: String?) {
        Observable.create<Any> {
            val selectedStudent = studentList.firstOrNull { it.username == username }
            if (selectedStudent == null) {
                it.onComplete()
                return@create
            }
            selectedStudent.getTests(object : GetArrayListener<Exam> {
                override fun error(rt: Int, e: Throwable) {
                    it.onError(e)
                }

                override fun got(array: Array<Exam>) {
                    testList.clear()
                    testList.addAll(array)
                    it.onComplete()
                }
            })
        }
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Any> {
                    override fun onComplete() {
                        loadingDialog.dismiss()
                        adapter.notifyDataSetChanged()
                        val parentFile = XhuFileUtil.getExamParentFile(this@ExamActivity)
                        if (!parentFile.exists())
                            parentFile.mkdirs()
                        val base64Name = XhuFileUtil.filterString(Base64.encodeToString(username!!.toByteArray(), Base64.DEFAULT))
                        val savedFile = File(parentFile, base64Name)
                        savedFile.createNewFile()
                        XhuFileUtil.saveObjectToFile(testList, savedFile)
                        sendBroadcast(Intent(Constants.WIDGET_UPDATE_BROADCAST)
                                .putExtra("TAG", WidgetHelper.ALL_TAG))
                    }

                    override fun onSubscribe(d: Disposable) {
                        loadingDialog.show()
                    }

                    override fun onNext(t: Any) {
                    }

                    override fun onError(e: Throwable) {
                        loadingDialog.dismiss()
                        Logs.wtf(TAG, "onError: ", e)
                        Snackbar.make(coordinatorLayout, e.message.toString(), Snackbar.LENGTH_SHORT)
                                .show()
                    }
                })
    }
}
