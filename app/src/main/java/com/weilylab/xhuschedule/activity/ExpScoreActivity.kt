/*
 * Created by Mystery0 on 18-1-4 下午8:18.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-1-4 下午8:18
 */

package com.weilylab.xhuschedule.activity

import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ExpScoreAdapter
import com.weilylab.xhuschedule.classes.baseClass.ExpScore
import com.weilylab.xhuschedule.classes.baseClass.Profile
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.listener.GetExpScoreListener
import com.weilylab.xhuschedule.listener.InitProfileListener
import com.weilylab.xhuschedule.listener.ProfileListener
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.ViewUtil
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_experiment_score.*
import kotlinx.android.synthetic.main.content_experiment_score.*
import java.io.File
import java.util.*

class ExpScoreActivity : AppCompatActivity() {

    private lateinit var loadingDialog: Dialog
    private val studentList = ArrayList<Student>()
    private val scoreList = ArrayList<ExpScore>()
    private lateinit var adapter: ExpScoreAdapter
    private var currentStudent: Student? = null
    private var year = ""
    private var term = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_experiment_score)
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
                .create()
        adapter = ExpScoreAdapter(this, scoreList)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
        studentList.clear()
        studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))

        val studentArray = Array(studentList.size, { i -> studentList[i].username })
        val termArray = arrayOf("1", "2", "3")

        ViewUtil.setPopupView(this, studentArray, textViewStudent, { position ->
            currentStudent = studentList[position]
            if (currentStudent != null)
                ViewUtil.initProfile(this, currentStudent!!, textViewYear, object : InitProfileListener {
                    override fun done(position: Int, year: String) {
                        this@ExpScoreActivity.year = year
                        initScores(currentStudent)
                    }

                    override fun error(dialog: Dialog) {
                        getInfo(currentStudent!!, dialog)
                    }
                })
        })
        ViewUtil.setPopupView(this, termArray, textViewTerm, { position ->
            term = position + 1
            initScores(currentStudent)
        })

        //初始化显示数据
        textViewStudent.text = studentArray[0]
        currentStudent = studentList[0]
        textViewTerm.text = termArray[0]
        ViewUtil.initProfile(this, currentStudent!!, textViewYear, object : InitProfileListener {
            override fun done(position: Int, year: String) {
                this@ExpScoreActivity.year = year
                initScores(currentStudent)
            }

            override fun error(dialog: Dialog) {
                getInfo(currentStudent!!, dialog)
            }
        })
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

    private fun getScores(student: Student?, year: String?, term: Int?) {
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

    private fun getInfo(student: Student, initDialog: Dialog) {
        student.getInfo(object : ProfileListener {
            override fun error(rt: Int, e: Throwable) {
                initDialog.dismiss()
                e.printStackTrace()
                Snackbar.make(coordinatorLayout, e.message.toString(), Snackbar.LENGTH_LONG)
                        .show()
            }

            override fun got(profile: Profile) {
                XhuFileUtil.saveObjectToFile(studentList, File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"))
                try {
                    val start = student.profile!!.grade.toInt()//进校年份
                    val calendar = Calendar.getInstance()
                    val end = when (calendar.get(Calendar.MONTH) + 1) {
                        in 1 until 9 -> calendar.get(Calendar.YEAR)
                        in 9 until 13 -> calendar.get(Calendar.YEAR) + 1
                        else -> 0
                    }
                    val array = Array(end - start, { i -> "${start + i}-${start + i + 1}" })
                    ViewUtil.setPopupView(this@ExpScoreActivity, array, textViewYear, { position ->
                        year = array[position]
                        initScores(currentStudent)
                    })
                    initDialog.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@ExpScoreActivity, "数据解析错误，无法使用，请联系开发者！", Toast.LENGTH_LONG)
                            .show()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_score, menu)
        menu.removeItem(R.id.action_show_failed)
        menu.removeItem(R.id.action_experiment)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_search -> {
                getScores(currentStudent, year, term)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
