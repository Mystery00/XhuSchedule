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

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.CustomMaterialSpinnerAdapter
import com.weilylab.xhuschedule.classes.baseClass.Profile
import com.weilylab.xhuschedule.classes.baseClass.Score
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.listener.GetScoreListener
import com.weilylab.xhuschedule.listener.ProfileListener
import com.weilylab.xhuschedule.util.*
import com.weilylab.xhuschedule.view.TextViewUtils
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_score.*
import kotlinx.android.synthetic.main.content_score.*
import vip.mystery0.logs.Logs

import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ScoreActivity : XhuBaseActivity() {
	private lateinit var initDialog: Dialog
	private lateinit var loadingDialog: Dialog
	private val studentList = ArrayList<Student>()
	private val scoreList = ArrayList<Score>()
	private val failedList = ArrayList<Score>()
	private val dropMaxHeight = 999
	private var valueAnimator: ValueAnimator? = null
	private var currentStudent: Student? = null
	private var currentIndex = -1
	private lateinit var pointDrawable: VectorDrawableCompat
	private var year: String? = null
	private var term: Int? = null

	override fun initData() {
		super.initData()
	}

	override fun initView() {
		super.initView()
		pointDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_point, null)!!
		pointDrawable.setBounds(0, 0, pointDrawable.minimumWidth, pointDrawable.minimumHeight)
		pointDrawable.setTint(ContextCompat.getColor(this, R.color.colorAccent))
		setContentView(R.layout.activity_score)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
		studentList.clear()
		studentList.addAll(XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(this), Student::class.java))
		initInfo()
		floatingActionButton.setOnClickListener {
			getScores(currentStudent, year, term)
		}
	}

	private fun initScores(student: Student?) {
		if (student == null || year == null || term == null)
			return
		Observable.create<Boolean> { subscriber ->
			val parentFile = XhuFileUtil.getScoreParentFile(this)
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
						if (scoreList.size == 0 && Settings.isAutoSelect)
							getScores(currentStudent, year, term)
						else
							setLayout()
					}

					override fun onError(e: Throwable) {
						e.printStackTrace()
					}
				})
	}

	private fun getScores(student: Student?, year: String?, term: Int?) {
		Logs.i(TAG, "getScore: year: $year term: $term")
		loadingDialog.show()
		Observable.create<Any> {
			if (student == null) {
				it.onComplete()
				return@create
			}
			student.getScores(year, term, object : GetScoreListener {
				override fun error(rt: Int, e: Throwable) {
					it.onError(e)
				}

				override fun got(array: Array<Score>, failedArray: Array<Score>) {
					scoreList.clear()
					scoreList.addAll(array)
					failedList.clear()
					failedList.addAll(failedArray)
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
						if (student == null)
							return
						setLayout()

						val parentFile = XhuFileUtil.getScoreParentFile(this@ScoreActivity)
						if (!parentFile.exists())
							parentFile.mkdirs()
						val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
						val savedFile = File(parentFile, "$base64Name-$year-$term")
						val savedFailedFile = File(parentFile, "$base64Name-$year-$term-failed")
						savedFile.createNewFile()
						savedFailedFile.createNewFile()
						XhuFileUtil.saveObjectToFile(scoreList, savedFile)
						XhuFileUtil.saveObjectToFile(failedList, savedFailedFile)
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

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_activity_score, menu)
		menu.findItem(R.id.action_show_failed).isChecked = Settings.isShowFailed
		menu.findItem(R.id.action_auto_select).isChecked = Settings.isAutoSelect
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
			R.id.action_auto_select -> {
				item.isChecked = !item.isChecked
				Settings.isAutoSelect = item.isChecked
				true
			}
			R.id.action_experiment -> {
				startActivity(Intent(this, ExpScoreActivity::class.java))
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun initInfo() {
		val studentShowList = Array(studentList.size, { i -> studentList[i].username }).toMutableList()
		studentShowList.add(getString(R.string.hint_popup_view_student))
		spinner_username.setAdapter(CustomMaterialSpinnerAdapter(this, studentShowList))
		val yearShowList = arrayListOf(getString(R.string.hint_popup_view_year))
		spinner_year.setAdapter(CustomMaterialSpinnerAdapter(this, yearShowList))
		val termShowList = arrayListOf("1", "2", "3", getString(R.string.hint_popup_view_term))
		spinner_term.setAdapter(CustomMaterialSpinnerAdapter(this, termShowList))
		spinner_username.setOnItemSelectedListener { _, _, _, username ->
			spinner_username.setDropdownMaxHeight(dropMaxHeight)
			spinner_year.setDropdownMaxHeight(dropMaxHeight)
			spinner_term.setDropdownMaxHeight(dropMaxHeight)
			setUsername(username.toString(), true)
		}
		spinner_year.setOnItemSelectedListener { _, _, _, year ->
			spinner_username.setDropdownMaxHeight(dropMaxHeight)
			spinner_year.setDropdownMaxHeight(dropMaxHeight)
			spinner_term.setDropdownMaxHeight(dropMaxHeight)
			this.year = year.toString()
			initScores(currentStudent)
		}
		spinner_term.setOnItemSelectedListener { _, _, _, term ->
			spinner_username.setDropdownMaxHeight(dropMaxHeight)
			spinner_year.setDropdownMaxHeight(dropMaxHeight)
			spinner_term.setDropdownMaxHeight(dropMaxHeight)
			this.term = Integer.parseInt(term.toString())
			initScores(currentStudent)
		}
		spinner_username.selectedIndex = studentShowList.size - 1
		spinner_year.selectedIndex = 0
		spinner_term.selectedIndex = termShowList.size - 1
		if (studentList.size == 1) {
			spinner_username.selectedIndex = 0
			setUsername(studentShowList[0], true)
		}
	}

	private fun setUsername(username: String?, isAutoSelect: Boolean) {
		val userList = ArrayList<Student>()
		val yearList = ArrayList<String>()
		//初始化入学年份
		Observable.create<Any> {
			userList.addAll(XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(this), Student::class.java))
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
						yearList.add(getString(R.string.hint_popup_view_year))
						spinner_year.setAdapter(CustomMaterialSpinnerAdapter(this@ScoreActivity, yearList))
						spinner_year.selectedIndex = yearList.size - 1
						if (isAutoSelect) {
							val term = CalendarUtil.getTermType()
							spinner_year.selectedIndex = yearList.size - 2//自动选择最后一年
							spinner_term.selectedIndex = term - 1//自动选择学期
							year = yearList[yearList.size - 2]
							this@ScoreActivity.term = term
						}
						spinner_username.setDropdownMaxHeight(dropMaxHeight)
						spinner_year.setDropdownMaxHeight(dropMaxHeight)
						spinner_term.setDropdownMaxHeight(dropMaxHeight)
						initScores(currentStudent)
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

	private fun setLayout() {
		currentIndex = -1
		linearLayout.removeAllViews()
		if (scoreList.size == 0)
			linearLayout.addView(ViewUtil.buildNoDataView(this@ScoreActivity, getString(R.string.hint_data_empty)))
		else
			for (i in scoreList.indices) {
				val score = scoreList[i]
				val itemView = ViewUtil.buildScoreItem(this@ScoreActivity, score, pointDrawable)
				val imageView: ImageView = itemView.findViewById(R.id.imageView)
				val detailsTextView: TextView = itemView.findViewById(R.id.textView_details)
				itemView.setOnClickListener {
					valueAnimator?.cancel()
					//带动画的展开收缩
					when (currentIndex) {
						-1 -> {
							valueAnimator = TextViewUtils.setMaxLinesWithAnimation(detailsTextView, Int.MAX_VALUE)
							ObjectAnimator.ofFloat(imageView, Constants.ANIMATION_ALPHA, 0F, 1F).start()
							currentIndex = i
						}
						i -> {
							valueAnimator = TextViewUtils.setMaxLinesWithAnimation(detailsTextView, 1)
							ObjectAnimator.ofFloat(imageView, Constants.ANIMATION_ALPHA, 1F, 0F).start()
							currentIndex = -1
						}
						else -> {
							val openedView = linearLayout.getChildAt(currentIndex)
							val openedImageView: ImageView = openedView.findViewById(R.id.imageView)
							val openedDetailsTextView: TextView = openedView.findViewById(R.id.textView_details)
							valueAnimator = TextViewUtils.setMaxLinesWithAnimation(openedDetailsTextView, 1)
							ObjectAnimator.ofFloat(openedImageView, Constants.ANIMATION_ALPHA, 1F, 0F).start()

							valueAnimator = TextViewUtils.setMaxLinesWithAnimation(detailsTextView, Int.MAX_VALUE)
							ObjectAnimator.ofFloat(imageView, Constants.ANIMATION_ALPHA, 0F, 1F).start()
							currentIndex = i
						}
					}
				}
				linearLayout.addView(itemView)
			}
	}
}
