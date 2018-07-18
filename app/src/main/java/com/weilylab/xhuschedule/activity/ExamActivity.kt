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
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.core.content.ContextCompat
import android.util.Base64
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.CustomMaterialSpinnerAdapter
import com.weilylab.xhuschedule.classes.baseClass.Exam
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.listener.GetArrayListener
import com.weilylab.xhuschedule.newPackage.base.XhuBaseActivity
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.ViewUtil
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.weilylab.xhuschedule.util.widget.WidgetHelper
import com.weilylab.xhuschedule.view.TextViewUtils
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_exam.*
import kotlinx.android.synthetic.main.content_exam.*
import vip.mystery0.logs.Logs
import java.io.File

class ExamActivity : XhuBaseActivity(R.layout.activity_exam) {
	private lateinit var loadingDialog: ZLoadingDialog
	private val studentList = ArrayList<Student>()
	private val testList = ArrayList<Exam>()
	private val dropMaxHeight = 999
	private var valueAnimator: ValueAnimator? = null
	private var currentIndex = -1
	private lateinit var pointDrawable: VectorDrawableCompat

	override fun initView() {
		super.initView()
		pointDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_point, null)!!
		pointDrawable.setBounds(0, 0, pointDrawable.minimumWidth, pointDrawable.minimumHeight)
		pointDrawable.setTint(ContextCompat.getColor(this, R.color.colorAccent))
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		loadingDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_sync))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setCancelable(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
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
		val studentShowList = Array(studentList.size, { i -> studentList[i].username }).toMutableList()
		studentShowList.add(getString(R.string.hint_popup_view_student))
		spinner_username.setAdapter(CustomMaterialSpinnerAdapter(this, studentShowList))
		spinner_username.setOnItemSelectedListener { _, _, _, username ->
			spinner_username.setDropdownMaxHeight(dropMaxHeight)
			setUsername(username.toString())
		}
		spinner_username.selectedIndex = studentShowList.size - 1
		if (studentList.size == 1) {
			spinner_username.selectedIndex = 0
			setUsername(studentShowList[0])
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
						currentIndex = -1
						linearLayout.removeAllViews()
						if (testList.size == 0)
							linearLayout.addView(ViewUtil.buildNoDataView(this@ExamActivity, getString(R.string.hint_data_empty)))
						else
							for (i in testList.indices) {
								val exam = testList[i]
								val itemView = ViewUtil.buildExamItem(this@ExamActivity, exam, pointDrawable)
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

						val parentFile = XhuFileUtil.getExamParentFile(this@ExamActivity)
						if (!parentFile.exists())
							parentFile.mkdirs()
						val base64Name = XhuFileUtil.filterString(Base64.encodeToString(username!!.toByteArray(), Base64.DEFAULT))
						val savedFile = File(parentFile, base64Name)
						savedFile.createNewFile()
						XhuFileUtil.saveObjectToFile(testList, savedFile)
						sendBroadcast(Intent(Constants.ACTION_WIDGET_UPDATE_BROADCAST)
								.putExtra(Constants.INTENT_TAG_NAME_TAG, WidgetHelper.ALL_TAG))
					}

					override fun onSubscribe(d: Disposable) {
						loadingDialog.show()
					}

					override fun onNext(t: Any) {
					}

					override fun onError(e: Throwable) {
						loadingDialog.dismiss()
						Logs.wtf("onError: ", e)
						Snackbar.make(coordinatorLayout, e.message.toString(), Snackbar.LENGTH_SHORT)
								.show()
					}
				})
	}
}
