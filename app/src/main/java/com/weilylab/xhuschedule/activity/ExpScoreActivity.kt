/*
 * Created by Mystery0 on 18-1-12 下午8:52.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                  =====================================================
 *                  =                                                   =
 *                  =                       _oo0oo_                     =
 *                  =                      o8888888o                    =
 *                  =                      88" . "88                    =
 *                  =                      (| -_- |)                    =
 *                  =                      0\  =  /0                    =
 *                  =                    ___/`---'\___                  =
 *                  =                  .' \\|     |# '.                 =
 *                  =                 / \\|||  :  |||# \                =
 *                  =                / _||||| -:- |||||- \              =
 *                  =               |   | \\\  -  #/ |   |              =
 *                  =               | \_|  ''\---/''  |_/ |             =
 *                  =               \  .-\__  '-'  ___/-. /             =
 *                  =             ___'. .'  /--.--\  `. .'___           =
 *                  =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                  =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                  =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                  =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                  =                       `=---='                     =
 *                  =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                  =                                                   =
 *                  =               佛祖保佑         永无BUG              =
 *                  =                                                   =
 *                  =====================================================
 *
 * Last modified 18-1-12 下午8:51
 */

package com.weilylab.xhuschedule.activity

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.analytics.FirebaseAnalytics
import com.jaredrummler.materialspinner.MaterialSpinner
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ExpScoreAdapter
import com.weilylab.xhuschedule.classes.baseClass.ExpScore
import com.weilylab.xhuschedule.classes.baseClass.Profile
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.listener.GetExpScoreListener
import com.weilylab.xhuschedule.listener.ProfileListener
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_experiment_score.*
import kotlinx.android.synthetic.main.content_experiment_score.*
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.util.*

class ExpScoreActivity : BaseActivity() {
    private val TAG = "ExpScoreActivity"
    private lateinit var initDialog: Dialog
    private lateinit var loadingDialog: Dialog
    private lateinit var alertDialog: AlertDialog
    private val studentList = ArrayList<Student>()
    private val scoreList = ArrayList<ExpScore>()
    private lateinit var adapter: ExpScoreAdapter
    private var currentStudent: Student? = null
    private var year = ""
    private var term = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exp_scores")
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params)
        setContentView(R.layout.activity_experiment_score)
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
                .create()
        initDialog = ZLoadingDialog(this)
                .setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE)
                .setHintText(getString(R.string.hint_dialog_init))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .create()
        adapter = ExpScoreAdapter(this, scoreList)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
        studentList.clear()
        studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
        initInfo()
        alertDialog.show()
        floatingActionButton.setOnClickListener {
            getExpScores(currentStudent, year, term)
        }
    }

    private fun initScores(student: Student?) {
        if (student == null)
            return
        Observable.create<Boolean> { subscriber ->
            val parentFile = File(filesDir.absolutePath + File.separator + "expScore/")
            if (!parentFile.exists())
                parentFile.mkdirs()
            val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
            val savedFile = File(parentFile, "$base64Name-$year-$term")
            scoreList.clear()
            scoreList.addAll(XhuFileUtil.getArrayListFromFile(savedFile, ExpScore::class.java))
            subscriber.onComplete()
        }
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Boolean>() {
                    override fun onNext(t: Boolean) {
                    }

                    override fun onComplete() {
                        adapter.notifyDataSetChanged()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    private fun getExpScores(student: Student?, year: String?, term: Int?) {
        Logs.i(TAG, "getExpScores: year: $year term: $term")
        if (student == null)
            return
        loadingDialog.show()
        student.getExpScores(year, term, object : GetExpScoreListener {
            override fun got(array: Array<ExpScore>) {
                scoreList.clear()
                scoreList.addAll(array)
                adapter.notifyDataSetChanged()
                val parentFile = File(filesDir.absolutePath + File.separator + "expScore/")
                if (!parentFile.exists())
                    parentFile.mkdirs()
                val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
                val savedFile = File(parentFile, "$base64Name-$year-$term")
                savedFile.createNewFile()
                XhuFileUtil.saveObjectToFile(scoreList, savedFile)
                loadingDialog.dismiss()
            }

            override fun error(rt: Int, e: Throwable) {
                loadingDialog.dismiss()
                e.printStackTrace()
                Snackbar.make(coordinatorLayout, e.message!!, Snackbar.LENGTH_SHORT)
                        .show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_score, menu)
        menu.removeItem(R.id.action_show_failed)
        menu.removeItem(R.id.action_experiment)
        menu.findItem(R.id.action_auto_select).isChecked = Settings.isAutoSelect
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_auto_select -> {
                item.isChecked = !item.isChecked
                Settings.isAutoSelect = item.isChecked
                true
            }
            R.id.action_filter_list -> {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                    alertDialog.dismiss()
                }
                alertDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initInfo() {
        val studentArray = Array(studentList.size, { i -> studentList[i].username })
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_spinner_username_term, null)
        val usernameSpinner = view.findViewById<MaterialSpinner>(R.id.spinner_username)
        val yearSpinner = view.findViewById<MaterialSpinner>(R.id.spinner_year)
        val termSpinner = view.findViewById<MaterialSpinner>(R.id.spinner_term)
        usernameSpinner.setItems(studentArray.toList())
        termSpinner.setItems(1, 2, 3)
        usernameSpinner.setOnItemSelectedListener { _, _, _, username ->
            setUsername(username.toString(), yearSpinner, termSpinner, true)
        }
        yearSpinner.setOnItemSelectedListener { _, _, _, year ->
            this.year = year.toString()
        }
        termSpinner.setOnItemSelectedListener { _, _, _, term ->
            this.term = term as Int
        }
        if (studentArray.size == 1) {
            usernameSpinner.selectedIndex = 0
            setUsername(studentArray[0], yearSpinner, termSpinner, true)
        }
        alertDialog = AlertDialog.Builder(this)
                .setTitle(R.string.title_dialog_select)
                .setView(view)
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    initScores(currentStudent)
                })
                .setNegativeButton(android.R.string.cancel, { _, _ ->
                    finish()
                })
                .create()
    }

    private fun setUsername(username: String?, yearSpinner: MaterialSpinner, termSpinner: MaterialSpinner, isAutoSelect: Boolean) {
        val userList = ArrayList<Student>()
        val yearList = ArrayList<String>()
        //初始化入学年份
        Observable.create<Any> {
            userList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
            val selectedStudent = userList.firstOrNull { it.username == username }
            if (selectedStudent == null) {
                it.onComplete()
                return@create
            }
            currentStudent = selectedStudent
            if (selectedStudent.profile != null) {
                val start = selectedStudent.profile!!.grade.toInt()//进校年份
                val calendar = Calendar.getInstance()
                val end = when (calendar.get(Calendar.MONTH) + 1) {
                    in 1 until 9 -> calendar.get(Calendar.YEAR)
                    in 9 until 13 -> calendar.get(Calendar.YEAR) + 1
                    else -> 0
                }
                val yearArray = Array(end - start, { i -> "${start + i}-${start + i + 1}" })
                yearList.clear()
                yearList.addAll(yearArray)
                it.onComplete()
            } else {
                selectedStudent.getInfo(object : ProfileListener {
                    override fun error(rt: Int, e: Throwable) {
                        it.onError(e)
                    }

                    override fun got(profile: Profile) {
                        val start = profile.grade.toInt()//进校年份
                        val calendar = Calendar.getInstance()
                        val end = when (calendar.get(Calendar.MONTH) + 1) {
                            in 1 until 9 -> calendar.get(Calendar.YEAR)
                            in 9 until 13 -> calendar.get(Calendar.YEAR) + 1
                            else -> 0
                        }
                        val yearArray = Array(end - start, { i -> "${start + i}-${start + i + 1}" })
                        yearList.clear()
                        yearList.addAll(yearArray)
                        it.onComplete()
                    }
                })
            }
        }
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Any> {
                    override fun onComplete() {
                        initDialog.dismiss()
                        yearSpinner.setItems(yearList)
                        if (isAutoSelect) {
                            val term = CalendarUtil.getTermType()
                            yearSpinner.selectedIndex = yearList.size - 1//自动选择最后一年
                            termSpinner.selectedIndex = term - 1//自动选择学期
                            year = yearList[yearList.size - 1]
                            this@ExpScoreActivity.term = term
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        initDialog.show()
                    }

                    override fun onNext(t: Any) {
                    }

                    override fun onError(e: Throwable) {
                        initDialog.dismiss()
                        Logs.wtf(TAG, "onError: ", e)
                        Snackbar.make(coordinatorLayout, e.message.toString(), Snackbar.LENGTH_LONG)
                                .show()
                    }
                })
    }
}
