/*
 * Created by Mystery0 on 17-12-3 上午3:32.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-3 上午3:29
 */

package com.weilylab.xhuschedule.activity

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ScoreAdapter
import com.weilylab.xhuschedule.classes.Profile
import com.weilylab.xhuschedule.classes.Score
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.listener.GetScoreListener
import com.weilylab.xhuschedule.listener.ProfileListener
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_score.*
import kotlinx.android.synthetic.main.content_score.*
import vip.mystery0.tools.logs.Logs

import java.io.File
import java.util.*

class ScoreActivity : AppCompatActivity() {
    companion object {
        private val TAG = "ScoreActivity"
    }

    private lateinit var initDialog: Dialog
    private lateinit var loadingDialog: Dialog
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
                .create()
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
                initProfile(studentList[position])
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
    }

    private fun initProfile(student: Student) {
        initDialog.show()
        initScores(student)
        if (student.profile != null) {
            try {
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
            } catch (e: Exception) {
                e.printStackTrace()
                getInfo(student)
            }
        } else {
            getInfo(student)
        }
    }

    private fun initScores(student: Student) {
        Observable.create<Boolean> { subscriber ->
            val parentFile = File(filesDir.absolutePath + File.separator + "score/")
            if (!parentFile.exists())
                parentFile.mkdirs()
            val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
            val savedFile = File(parentFile, "$base64Name-$year-$term")
            scoreList.clear()
            scoreList.addAll(XhuFileUtil.getArrayListFromFile(savedFile, Score::class.java))
            subscriber.onComplete()
        }
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Boolean>() {
                    override fun onNext(t: Boolean) {
                    }

                    override fun onComplete() {
                        adapter.clearList()
                        adapter.notifyDataSetChanged()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    private fun getInfo(student: Student) {
        student.getInfo(this, object : ProfileListener {
            override fun error(rt: Int, e: Throwable) {
                initDialog.dismiss()
                e.printStackTrace()
                Snackbar.make(coordinatorLayout, e.message!!, Snackbar.LENGTH_LONG)
                        .show()
            }

            override fun doInThread() {
                XhuFileUtil.saveObjectToFile(studentList, File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"))
            }

            override fun got(profile: Profile) {
                try {
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
                    initDialog.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@ScoreActivity, "数据解析错误，无法使用，请联系开发者！", Toast.LENGTH_LONG)
                            .show()
                }
            }
        })
    }

    private fun getScores(student: Student, year: String?, term: Int?) {
        loadingDialog.show()
        student.getScores(this, year, term, object : GetScoreListener {
            override fun got(array: Array<Score>, failedArray: Array<Score>) {
                loadingDialog.dismiss()
                scoreList.clear()
                scoreList.addAll(array)
                scoreList.add(Score())
                scoreList.addAll(failedArray)
                Thread(Runnable {
                    val parentFile = File(filesDir.absolutePath + File.separator + "score/")
                    if (!parentFile.exists())
                        parentFile.mkdirs()
                    val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
                    val savedFile = File(parentFile, "$base64Name-$year-$term")
                    savedFile.createNewFile()
                    XhuFileUtil.saveObjectToFile(scoreList, savedFile)
                }).start()
                adapter.clearList()
                adapter.notifyDataSetChanged()
            }

            override fun error(rt: Int, e: Throwable) {
                loadingDialog.dismiss()
                e.printStackTrace()
                Snackbar.make(coordinatorLayout, e.message!!, Snackbar.LENGTH_SHORT)
                        .show()
            }

            override fun doInThread() {
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
                getScores(studentList[spinner_student.selectedItemPosition], year, term)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
