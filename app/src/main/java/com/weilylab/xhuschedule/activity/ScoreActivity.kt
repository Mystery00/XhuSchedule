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
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ScoreAdapter
import com.weilylab.xhuschedule.classes.baseClass.Profile
import com.weilylab.xhuschedule.classes.baseClass.Score
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.listener.GetScoreListener
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
import kotlinx.android.synthetic.main.activity_score.*
import kotlinx.android.synthetic.main.content_score.*

import java.io.File
import java.util.*

class ScoreActivity : BaseActivity() {

    private lateinit var loadingDialog: Dialog
    private val studentList = ArrayList<Student>()
    private val scoreList = ArrayList<Score>()
    private lateinit var adapter: ScoreAdapter
    private var currentStudent: Student? = null
    private var year = ""
    private var term = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
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
        adapter = ScoreAdapter(this, scoreList)
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
                        this@ScoreActivity.year = year
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
                this@ScoreActivity.year = year
                initScores(currentStudent)
            }

            override fun error(dialog: Dialog) {
                getInfo(currentStudent!!, dialog)
            }
        })
        floatingActionButton.setOnClickListener {
            getScores(currentStudent, year, term)
        }
    }


    private fun initScores(student: Student?) {
        if (student == null)
            return
        Observable.create<Boolean> { subscriber ->
            val parentFile = File(filesDir.absolutePath + File.separator + "score/")
            if (!parentFile.exists())
                parentFile.mkdirs()
            val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
            val savedFile = File(parentFile, "$base64Name-$year-$term")
            val savedFailedFile = File(parentFile, "$base64Name-$year-$term-failed")
            scoreList.clear()
            scoreList.addAll(XhuFileUtil.getArrayListFromFile(savedFile, Score::class.java))
            if (Settings.isShowFailed)
                scoreList.addAll(XhuFileUtil.getArrayListFromFile(savedFailedFile, Score::class.java))
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
        student.getScores(year, term, object : GetScoreListener {
            override fun got(array: Array<Score>, failedArray: Array<Score>) {
                scoreList.clear()
                scoreList.addAll(array)
                adapter.notifyDataSetChanged()
                val parentFile = File(filesDir.absolutePath + File.separator + "score/")
                if (!parentFile.exists())
                    parentFile.mkdirs()
                val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
                val savedFile = File(parentFile, "$base64Name-$year-$term")
                val savedFailedFile = File(parentFile, "$base64Name-$year-$term-failed")
                savedFile.createNewFile()
                savedFailedFile.createNewFile()
                XhuFileUtil.saveObjectToFile(scoreList, savedFile)
                XhuFileUtil.saveObjectToFile(failedArray.toList(), savedFailedFile)
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
                    ViewUtil.setPopupView(this@ScoreActivity, array, textViewYear, { position ->
                        year = array[position]
                        initScores(currentStudent)
                    })
                    initDialog.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@ScoreActivity, "数据解析错误，无法使用，请联系开发者！", Toast.LENGTH_LONG)
                            .show()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_score, menu)
        menu.findItem(R.id.action_show_failed).isChecked = Settings.isShowFailed
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_show_failed -> {
                item.isChecked = !item.isChecked
                Settings.isShowFailed = item.isChecked
                initScores(currentStudent)
                true
            }
            R.id.action_experiment -> {
                startActivity(Intent(this, ExpScoreActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
