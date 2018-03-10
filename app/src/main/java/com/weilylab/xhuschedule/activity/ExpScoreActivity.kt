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
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.CustomMaterialSpinnerAdapter
import com.weilylab.xhuschedule.classes.baseClass.ExpScore
import com.weilylab.xhuschedule.classes.baseClass.Profile
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.listener.GetExpScoreListener
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
import kotlinx.android.synthetic.main.activity_experiment_score.*
import kotlinx.android.synthetic.main.content_experiment_score.*
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.util.*

class ExpScoreActivity : XhuBaseActivity() {
	private lateinit var initDialog: Dialog
	private lateinit var loadingDialog: Dialog
	private val studentList = ArrayList<Student>()
	private val scoreList = ArrayList<ExpScore>()
	private val dropMaxHeight = 999
	private var valueAnimator: ValueAnimator? = null
	private var currentStudent: Student? = null
	private var currentIndex = -1
	private lateinit var pointDrawable: VectorDrawableCompat
	private var year: String? = null
	private var term: Int? = null

	override fun initData() {
		super.initData()
		val params = Bundle()
		params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "exp_scores")
		mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params)
	}

	override fun initView() {
		super.initView()
		pointDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_point, null)!!
		pointDrawable.setBounds(0, 0, pointDrawable.minimumWidth, pointDrawable.minimumHeight)
		pointDrawable.setTint(ContextCompat.getColor(this, R.color.colorAccent))
		setContentView(R.layout.activity_experiment_score)
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
			getExpScores(currentStudent, year, term)
		}
	}

	private fun initScores(student: Student?) {
		if (student == null || year == null || term == null)
			return
		Observable.create<Boolean> { subscriber ->
			val parentFile = XhuFileUtil.getExpScoreParentFile(this)
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
						if (scoreList.size == 0 && Settings.isAutoSelect)
							getExpScores(currentStudent, year, term)
						else
							setLayout()
					}

					override fun onError(e: Throwable) {
						e.printStackTrace()
					}
				})
	}

	private fun getExpScores(student: Student?, year: String?, term: Int?) {
		Logs.i(TAG, "getExpScores: year: $year term: $term")
		loadingDialog.show()
		Observable.create<Any> {
			if (student == null) {
				it.onComplete()
				return@create
			}
			student.getExpScores(year, term, object : GetExpScoreListener {
				override fun got(array: Array<ExpScore>) {
					scoreList.clear()
					scoreList.addAll(array)
					it.onComplete()
				}

				override fun error(rt: Int, e: Throwable) {
					it.onError(e)
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

						val parentFile = XhuFileUtil.getExpScoreParentFile(this@ExpScoreActivity)
						if (!parentFile.exists())
							parentFile.mkdirs()
						val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
						val savedFile = File(parentFile, "$base64Name-$year-$term")
						savedFile.createNewFile()
						XhuFileUtil.saveObjectToFile(scoreList, savedFile)
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
			this.term = term as Int
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
						spinner_year.setAdapter(CustomMaterialSpinnerAdapter(this@ExpScoreActivity, yearList))
						spinner_year.selectedIndex = yearList.size - 1
						if (isAutoSelect) {
							val term = CalendarUtil.getTermType()
							spinner_year.selectedIndex = yearList.size - 2//自动选择最后一年
							spinner_term.selectedIndex = term - 1//自动选择学期
							year = yearList[yearList.size - 2]
							this@ExpScoreActivity.term = term
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
			linearLayout.addView(ViewUtil.buildNoDataView(this@ExpScoreActivity, getString(R.string.hint_data_empty)))
		else
			for (i in scoreList.indices) {
				val expScore = scoreList[i]
				val itemView = ViewUtil.buildExpScoreItem(this@ExpScoreActivity, expScore, pointDrawable)
				val imageView: ImageView = itemView.findViewById(R.id.imageView)
				val detailsTextView: TextView = itemView.findViewById(R.id.textView_details)
				itemView.setOnClickListener {
					Logs.i(TAG, "onBindViewHolder: 点击事件")
					valueAnimator?.cancel()
					//带动画的展开收缩
					when (currentIndex) {
						-1 -> {
							Logs.i(TAG, "onBindViewHolder: 没有条目被选中")
							valueAnimator = TextViewUtils.setMaxLinesWithAnimation(detailsTextView, Int.MAX_VALUE)
							ObjectAnimator.ofFloat(imageView, Constants.ANIMATION_ALPHA, 0F, 1F).start()
							currentIndex = i
						}
						i -> {
							Logs.i(TAG, "onBindViewHolder: 选中的是当前条目")
							valueAnimator = TextViewUtils.setMaxLinesWithAnimation(detailsTextView, 1)
							ObjectAnimator.ofFloat(imageView, Constants.ANIMATION_ALPHA, 1F, 0F).start()
							currentIndex = -1
						}
						else -> {
							Logs.i(TAG, "onBindViewHolder: 选中的其他条目")
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
