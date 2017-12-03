/*
 * Created by Mystery0 on 17-12-3 上午3:32.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-3 上午3:29
 */

package com.weilylab.xhuschedule.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ScoreAdapter
import com.weilylab.xhuschedule.classes.Score
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.classes.rt.LoginRT
import com.weilylab.xhuschedule.classes.rt.ScoreRT
import com.weilylab.xhuschedule.classes.rt.StudentInfoRT
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_score.*
import kotlinx.android.synthetic.main.content_score.*
import vip.mystery0.tools.logs.Logs

import java.io.File
import java.net.UnknownHostException
import java.util.*

class ScoreActivity : AppCompatActivity() {
    companion object {
        private val TAG = "ScoreActivity"
    }

    private lateinit var initDialog: ZLoadingDialog
    private lateinit var loadingDialog: ZLoadingDialog
    private var isTryRefreshData = false
    private var isTryLogin = false
    private val studentList = ArrayList<Student>()
    private val scoreList = ArrayList<Score>()
    private lateinit var adapter: ScoreAdapter
    private var year = ""
    private var term = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initView()
    }

    private fun initView() {
        initDialog = ZLoadingDialog(this)
                .setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE)
                .setHintText(getString(R.string.hint_dialog_init))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        loadingDialog = ZLoadingDialog(this)
                .setLoadingBuilder(Z_TYPE.SEARCH_PATH)
                .setHintText(getString(R.string.hint_dialog_sync))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        adapter = ScoreAdapter(this, scoreList)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
        studentList.clear()
        studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
        val array = Array(studentList.size, { i -> "${studentList[i].name}(${studentList[i].username})" })
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_student.adapter = arrayAdapter
        spinner_student.setSelection(0)
        spinner_term.setSelection(when (Calendar.getInstance().get(Calendar.MONTH) + 1) {
            in 3 until 9 -> 1
            else -> 0
        })
        term = spinner_term.selectedItem.toString().toInt()
        spinner_student.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Logs.i(TAG, "onItemSelected: " + position)
                initProfile(studentList[position])
                scoreList.clear()
                adapter.clearList()
                adapter.notifyDataSetChanged()
            }
        }
        spinner_year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                year = spinner_year.selectedItem.toString()
            }
        }
        spinner_term.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                term = spinner_term.selectedItem.toString().toInt()
            }
        }
        getTests(studentList[0], null, null)
    }

    private fun initProfile(student: Student) {
        initDialog.show()
        if (student.profile != null) {
            val start = student.profile!!.grade.toInt()//进校年份
            val calendar = Calendar.getInstance()
            val end = when (calendar.get(Calendar.MONTH) + 1) {
                in 1 until 3 -> calendar.get(Calendar.YEAR) - 1
                in 3 until 9 -> calendar.get(Calendar.YEAR)
                in 9 until 13 -> calendar.get(Calendar.YEAR) + 1
                else -> {
                    Logs.i(TAG, "initProfile: " + (calendar.get(Calendar.MONTH) + 1))
                    0
                }
            }
            val array = Array(end - start, { i -> (start + i).toString() + '-' + (start + i + 1).toString() })
            val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_year.adapter = arrayAdapter
            spinner_year.setSelection(array.size - 1)
            year = spinner_year.selectedItem.toString()
            initDialog.dismiss()
        } else {
            student.getInfo()
                    .subscribeOn(Schedulers.io())
                    .doAfterNext {
                        XhuFileUtil.saveObjectToFile(studentList, File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"))
                    }
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : DisposableObserver<StudentInfoRT>() {
                        private var studentInfoRT: StudentInfoRT? = null
                        override fun onError(e: Throwable) {
                            initDialog.dismiss()
                            e.printStackTrace()
                            Toast.makeText(this@ScoreActivity, e.message, Toast.LENGTH_SHORT)
                                    .show()
                        }

                        override fun onNext(t: StudentInfoRT) {
                            studentInfoRT = t
                        }

                        override fun onComplete() {

                            when (studentInfoRT?.rt) {
                                "0" -> {
                                    if (!isTryRefreshData) {
                                        isTryRefreshData = true
                                        getTests(student, year, term)
                                    }
                                }
                                "1" -> {
                                    val start = student.profile!!.grade.toInt()//进校年份
                                    val calendar = Calendar.getInstance()
                                    val end = when (calendar.get(Calendar.MONTH) + 1) {
                                        in 1 until 3 -> calendar.get(Calendar.YEAR) - 1
                                        in 3 until 9 -> calendar.get(Calendar.YEAR)
                                        in 9 until 13 -> calendar.get(Calendar.YEAR) + 1
                                        else -> {
                                            Logs.i(TAG, "initProfile: " + (calendar.get(Calendar.MONTH) + 1))
                                            0
                                        }
                                    }
                                    val array = Array(end - start, { i -> (start + i).toString() + '-' + (start + i + 1).toString() })
                                    val arrayAdapter = ArrayAdapter<String>(this@ScoreActivity, android.R.layout.simple_spinner_item, array)
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    spinner_year.adapter = arrayAdapter
                                    spinner_year.setSelection(array.size - 1)
                                }
                                "2" -> Snackbar.make(coordinatorLayout, R.string.error_invalid_username, Snackbar.LENGTH_LONG)
                                        .show()
                                "3" -> Snackbar.make(coordinatorLayout, R.string.error_invalid_password, Snackbar.LENGTH_LONG)
                                        .show()
                                "6" -> {
                                    login(student, year, term)
                                    return
                                }
                            }
                            initDialog.dismiss()
                        }
                    })
        }
    }

    private fun getTests(student: Student, year: String?, term: Int?) {
        student.getScores(year, term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ScoreRT> {
                    private var scoreRT: ScoreRT? = null
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

                    override fun onNext(t: ScoreRT) {
                        scoreRT = t
                    }

                    override fun onSubscribe(d: Disposable) {
                        loadingDialog.show()
                    }

                    override fun onComplete() {
                        when (scoreRT?.rt) {
                            "0" -> {
                                if (!isTryRefreshData) {
                                    isTryRefreshData = true
                                    getTests(student, year, term)
                                } else
                                    Snackbar.make(coordinatorLayout, R.string.error_timeout, Snackbar.LENGTH_LONG)
                                            .show()
                            }
                            "1" -> {
                                scoreList.clear()
                                scoreList.addAll(scoreRT?.scores!!)
                                scoreList.add(Score())
                                scoreList.addAll(scoreRT?.failscores!!)
                                adapter.clearList()
                                adapter.notifyDataSetChanged()
                            }
                            "2" -> Snackbar.make(coordinatorLayout, R.string.error_invalid_username, Snackbar.LENGTH_LONG)
                                    .show()
                            "3" -> Snackbar.make(coordinatorLayout, R.string.error_invalid_password, Snackbar.LENGTH_LONG)
                                    .show()
                            "6" -> {
                                login(student, year, term)
                                return
                            }
                        }
                        loadingDialog.dismiss()
                    }
                })
    }

    private fun login(student: Student, year: String?, term: Int?) {
        student.login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<LoginRT> {
                    private var loginRT: LoginRT? = null
                    override fun onError(e: Throwable) {
                        loadingDialog.dismiss()
                        e.printStackTrace()
                        if (e is UnknownHostException)
                            Toast.makeText(this@ScoreActivity, R.string.error_network, Toast.LENGTH_SHORT)
                                    .show()
                        else
                            Toast.makeText(this@ScoreActivity, e.message, Toast.LENGTH_SHORT)
                                    .show()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {
                        when (loginRT?.rt) {
                            "0" -> {
                                if (!isTryLogin)
                                    login(student, year, term)
                                else {
                                    loadingDialog.dismiss()
                                    Snackbar.make(coordinatorLayout, R.string.error_timeout, Snackbar.LENGTH_LONG)
                                            .show()
                                }
                            }
                            "1" -> getTests(student, year, term)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_score, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_search -> {
                getTests(studentList[spinner_student.selectedItemPosition], year, term)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
