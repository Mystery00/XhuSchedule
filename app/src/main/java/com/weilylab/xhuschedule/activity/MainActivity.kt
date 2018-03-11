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

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.widget.Toast
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.perf.metrics.AddTrace
import com.google.gson.Gson
import com.sina.weibo.sdk.api.share.IWeiboShareAPI
import com.sina.weibo.sdk.api.share.WeiboShareSDK
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.Tencent
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ViewPagerAdapter
import com.weilylab.xhuschedule.adapter.WeekAdapter
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.classes.baseClass.Profile
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.classes.rt.AutoLoginRT
import com.weilylab.xhuschedule.classes.rt.GetCourseRT
import com.weilylab.xhuschedule.fragment.ProfileFragment
import com.weilylab.xhuschedule.fragment.TableFragment
import com.weilylab.xhuschedule.fragment.TodayFragment
import com.weilylab.xhuschedule.interfaces.StudentService
import com.weilylab.xhuschedule.interfaces.UserService
import com.weilylab.xhuschedule.listener.ProfileListener
import com.weilylab.xhuschedule.listener.WeekChangeListener
import com.weilylab.xhuschedule.util.*
import com.weilylab.xhuschedule.util.widget.WidgetHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import vip.mystery0.tools.logs.Logs
import vip.mystery0.tools.utils.Mystery0DensityUtil
import java.io.File
import java.io.InputStreamReader
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : XhuBaseActivity() {
	companion object {
		private const val ADD_ACCOUNT_CODE = 1
		private const val ANIMATION_DURATION = 480L
	}

	val permissionWriteExternalCode = 20
	private lateinit var loadingDialog: Dialog
	private lateinit var updateProfileDialog: Dialog
	private lateinit var weekAdapter: WeekAdapter
	private var mainStudent: Student?=null
	private var weekAnimator: ObjectAnimator? = null
	private var arrowDrawable: Drawable? = null
	private var isTryRefreshData = false
	private var isRefreshData = false
	private var isWeekShow = false
	private var isDataNew = false
	private var lastPressBack = 0L
	private var studentList = ArrayList<Student>()
	private var weekList = ArrayList<ArrayList<ArrayList<Course>>>()
	private val todayList = ArrayList<Course>()
	private val animatorList = ArrayList<ObjectAnimator>()
	private val needLoginStudents = ArrayList<Student>()
	private val todayFragment = TodayFragment.newInstance(todayList)
	private val weekFragment = TableFragment.newInstance(weekList)
	private val profileFragment = ProfileFragment.newInstance(Profile())
	private var lastIndex = 0
	lateinit var mWeiboShareAPI: IWeiboShareAPI
	lateinit var wxAPI: IWXAPI

	override fun initData() {
		super.initData()
		val params = Bundle()
		params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "main")
		mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params)
	}

	private fun showUpdateLog() {
		val sharedPreference = getSharedPreferences(Constants.SHARED_PREFERENCE_UPDATE_DATA, Context.MODE_PRIVATE)
		if (sharedPreference.getInt(Constants.UPDATE_VERSION, 0) < getString(R.string.app_version_code).toInt()) {
			var message = ""
			resources.getStringArray(R.array.update_list)
					.forEach { message += it + '\n' }
			val dialog = AlertDialog.Builder(this)
					.setTitle(getString(R.string.dialog_title_update_log, getString(R.string.app_version_name) + '-' + getString(R.string.app_version_code)))
					.setMessage(message)
					.setCancelable(false)
					.setPositiveButton(android.R.string.ok, null)
					.setOnDismissListener {
						sharedPreference.edit().putInt(Constants.UPDATE_VERSION, getString(R.string.app_version_code).toInt()).apply()
					}
					.create()
			if (APPActivityManager.appManager.currentActivity() == this)
				dialog.show()
			Observable.create<Boolean> { subscriber ->
				Thread.sleep(2000)
				subscriber.onComplete()
			}
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : Observer<Boolean> {
						override fun onSubscribe(d: Disposable) {
							dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
						}

						override fun onError(e: Throwable) {
						}

						override fun onComplete() {
							dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
						}

						override fun onNext(t: Boolean) {
						}
					})
		}
	}

	override fun onResume() {
		super.onResume()
		if (ScheduleHelper.isImageChange) {
			todayFragment.setBackground()
			weekFragment.setBackground()
			profileFragment.setProfileImg()
		}
		if (ScheduleHelper.isUIChange) {
			loadingDialog.show()
			studentList.clear()
			studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
			updateAllView()
		}
		if (ScheduleHelper.isTableLayoutChange)
			weekFragment.updateTableLayout(Settings.customTableItemWidth != -1)
		if (bottomNavigationView.menu.getItem(2).isChecked) {//刷新小红点状态
			profileFragment.updateNoticeBadge()
		}
		ScheduleHelper.isImageChange = false
		ScheduleHelper.isUIChange = false
		ScheduleHelper.isTableLayoutChange = false
	}

	override fun initView() {
		super.initView()
		setContentView(R.layout.activity_main)
		arrowDrawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ms__arrow)
		registerWeibo()
		registerWeiXin()
		loadingDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_update_cache))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setCancelable(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
		updateProfileDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.CIRCLE_CLOCK)
				.setHintText(getString(R.string.hint_dialog_update_profile))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setCancelable(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
		initLayout()
		val todayInfoText = CalendarUtil.getTodayText()
		if (todayInfoText != Settings.isFirstEnterToday) {
			Logs.i(TAG, "initView: 这是今天的第一次运行")
			Settings.isFirstEnterToday = todayInfoText
			updateAllData()
		} else {
			Logs.i(TAG, "initView: 这不是今天的第一次运行")
			updateAllView()
		}
		if (Settings.isFirstRun)
			showcase()
	}

	private fun initLayout() {
		val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
		viewPagerAdapter.addFragment(todayFragment)
		viewPagerAdapter.addFragment(weekFragment)
		viewPagerAdapter.addFragment(profileFragment)
		viewpager.offscreenPageLimit = 2
		viewpager.adapter = viewPagerAdapter

		studentList.clear()
		studentList.addAll(XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(this), Student::class.java))

		weekAdapter = WeekAdapter(this, 1)
		weekAdapter.setWeekChangeListener(object : WeekChangeListener {
			override fun onChange(week: Int) {
				ScheduleHelper.weekIndex = week + 1
				weekAdapter.setWeekIndex(ScheduleHelper.weekIndex)
				swipeLayout(bottomNavigationView.menu.getItem(viewpager.currentItem).itemId)
				updateAllView(ScheduleHelper.weekIndex)
			}
		})
		layout_week_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
		layout_week_recycler_view.adapter = weekAdapter
		layout_week_recycler_view.scrollToPosition(0)
		layout_week_recycler_view_internal.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
		layout_week_recycler_view_internal.adapter = weekAdapter
		layout_week_recycler_view_internal.scrollToPosition(0)

		todayFragment.setBackground()
		weekFragment.setBackground()
	}

	override fun monitor() {
		super.monitor()
		bottomNavigationView.setOnNavigationItemSelectedListener { item ->
			when (item.itemId) {
				R.id.bottom_nav_today -> viewpager.currentItem = 0
				R.id.bottom_nav_week -> viewpager.currentItem = 1
				R.id.bottom_nav_profile -> viewpager.currentItem = 2
			}
			swipeLayout(item.itemId)
			true
		}
		viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
			override fun onPageScrollStateChanged(state: Int) {
			}

			override fun onPageScrolled(position: Int, positionOffset: Float,
										positionOffsetPixels: Int) {
			}

			override fun onPageSelected(position: Int) {
				bottomNavigationView.menu.getItem(position).isChecked = true
				swipeLayout(bottomNavigationView.menu.getItem(position).itemId)
			}
		})

		action_sync.setOnClickListener {
			if (isRefreshData)
				return@setOnClickListener
			isTryRefreshData = false
			loadingDialog.setOnDismissListener {
				isRefreshData = false
			}
			updateAllData()
		}
		action_settings.setOnClickListener {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				startActivity(Intent(this, SettingsActivity::class.java))
				overridePendingTransition(R.anim.animation_settings_in_enter, R.anim.animation_settings_in_exit)
			} else {
				ObjectAnimator.ofFloat(action_settings, Constants.ANIMATION_ROTATION, 0F, 360F).start()
				startActivity(Intent(this, SettingsActivity::class.java), ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
			}
		}
		titleLayout.setOnClickListener {
			//占位，在上层处理点击事件
			Logs.i(TAG, "initView: titleLayout")
		}
	}

	fun updateAllView() {
		updateAllView(-1)
	}

	fun updateAllView(week: Int) {
		ScheduleHelper.isAnalysisError = false
		studentList.clear()
		studentList.addAll(XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(this), Student::class.java))
		if (studentList.size == 0) {
			startActivityForResult(Intent(this, LoginActivity::class.java), ADD_ACCOUNT_CODE)
			return
		}
		loadingDialog.show()
		//清空数组
		weekList.clear()
		todayList.clear()
		val array = ArrayList<Observable<Student>>()
		val updateList = ArrayList<Student>()
		if (Settings.isEnableMultiUserMode) {
			studentList.forEach {
				array.add(updateView(it, week))
				updateList.add(it)
			}
			mainStudent = studentList[0]
		} else {
			var tempStudent: Student? = (0 until studentList.size)
					.firstOrNull { studentList[it].isMain }
					?.let { studentList[it] }
			if (tempStudent == null)
				tempStudent = studentList[0]
			array.add(updateView(tempStudent, week))
			updateList.add(tempStudent)
			mainStudent = tempStudent
		}
		Observable.merge(array)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : DisposableObserver<Student>() {
					override fun onComplete() {
						if (needLoginStudents.size != 0) {
							login()
							return
						}
						loadingDialog.dismiss()
						swipeLayout(bottomNavigationView.menu.getItem(viewpager.currentItem).itemId)
						weekAdapter.setWeekIndex(ScheduleHelper.weekIndex)
						layout_week_recycler_view.scrollToPosition(ScheduleHelper.weekIndex - 1)
						layout_week_recycler_view_internal.scrollToPosition(ScheduleHelper.weekIndex - 1)
						if (!Settings.isEnableMultiUserMode)
							when (todayList.size) {
								0 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_very_satisfied)
								1 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_very_satisfied)
								2 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_satisfied)
								3 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_neutral)
								4 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_dissatisfied)
								else -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_very_dissatisfied)
							}
						else
							bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_very_satisfied)
						updateList.forEach {
							weekList.addAll(CourseUtil.mergeCourses(weekList, it.weekCourses))
							todayList.addAll(it.todayCourses)
						}
						weekFragment.refreshData()
						todayFragment.refreshData()
						isRefreshData = false
						showUpdateLog()
					}

					override fun onError(e: Throwable) {
						e.printStackTrace()
						isRefreshData = false
						loadingDialog.dismiss()
						showUpdateLog()
					}

					override fun onNext(student: Student) {
						needLoginStudents.add(student)
					}
				})
	}

	private fun updateView(student: Student, week: Int): Observable<Student> {
		return Observable.create<Student> { subscriber ->
			val parentFile = XhuFileUtil.getCourseCacheParentFile(this)
			if (!parentFile.exists())
				parentFile.mkdirs()
			val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
			//判断是否有缓存
			val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
			if (!cacheResult) {
				subscriber.onNext(student)
				subscriber.onComplete()
				return@create
			}
			val oldFile = File(parentFile, base64Name)
			if (!oldFile.exists()) {
				subscriber.onNext(student)
				subscriber.onComplete()
				return@create
			}
			val tempArray = if (week != -1)
				if (Settings.isShowNot)
					CourseUtil.formatCourses(CourseUtil.getCoursesFromFile(oldFile), week)
				else
					CourseUtil.getWeekCourses(CourseUtil.getCoursesFromFile(oldFile), week)
			else
				if (Settings.isShowNot)
					CourseUtil.formatCourses(CourseUtil.getCoursesFromFile(oldFile))
				else
					CourseUtil.getWeekCourses(CourseUtil.getCoursesFromFile(oldFile))
			student.weekCourses.clear()
			student.weekCourses.addAll(tempArray)
			val todayArray = CourseUtil.getTodayCourses(CourseUtil.getCoursesFromFile(oldFile))
			student.todayCourses.clear()
			student.todayCourses.addAll(todayArray)
			student.isReady = true
			subscriber.onComplete()
		}
	}

	@AddTrace(name = "sync course data trace", enabled = true)
	private fun updateAllData() {
		Logs.i(TAG, "updateAllData: ")
		loadingDialog.show()
		ObjectAnimator.ofFloat(action_sync, Constants.ANIMATION_ROTATION, 0F, 360F).setDuration(1000).start()
		studentList.clear()
		studentList.addAll(XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(this), Student::class.java))
		if (studentList.size == 0) {
			startActivityForResult(Intent(this, LoginActivity::class.java), ADD_ACCOUNT_CODE)
			return
		}
		todayList.clear()
		val array = ArrayList<Observable<GetCourseRT>>()
		isRefreshData = true
		val updateList = ArrayList<Student>()
		if (Settings.isEnableMultiUserMode)
			studentList.forEach {
				array.add(updateData(it))
				updateList.add(it)
			}
		else {
			var mainStudent: Student? = (0 until studentList.size)
					.firstOrNull { studentList[it].isMain }
					?.let { studentList[it] }
			if (mainStudent == null)
				mainStudent = studentList[0]
			array.add(updateData(mainStudent))
			updateList.add(mainStudent)
		}
		Observable.merge(array)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : DisposableObserver<GetCourseRT>() {
					override fun onError(e: Throwable) {
						loadingDialog.dismiss()
						isRefreshData = false
						e.printStackTrace()
						if (e is UnknownHostException)
							Snackbar.make(coordinatorLayoutView, R.string.error_network, Snackbar.LENGTH_SHORT)
									.show()
						else
							Snackbar.make(coordinatorLayoutView, "请求出错：${e.message.toString()}，请重试", Snackbar.LENGTH_SHORT)
									.show()
					}

					override fun onComplete() {
						Logs.i(TAG, "updateAllData: onComplete: ")
						if (needLoginStudents.size != 0) {
							login()
							return
						}
						isRefreshData = false
						sendBroadcast(Intent(Constants.ACTION_WIDGET_UPDATE_BROADCAST)
								.putExtra(Constants.INTENT_TAG_NAME_TAG, WidgetHelper.ALL_TAG))
						updateAllView()
					}

					override fun onNext(getCourseRT: GetCourseRT) {
						Logs.i(TAG, "updateAllData: onNext: " + getCourseRT.rt)
						when (getCourseRT.rt) {
							ConstantsCode.DONE, ConstantsCode.SERVER_COURSE_ANALYZE_ERROR -> {
								if (getCourseRT.rt == ConstantsCode.SERVER_COURSE_ANALYZE_ERROR)
									Snackbar.make(coordinatorLayoutView, R.string.hint_update_data_error, Snackbar.LENGTH_LONG).show()
								else {
									if (ScheduleHelper.isAnalysisError) {
										Snackbar.make(coordinatorLayoutView, R.string.hint_analyze_error, Snackbar.LENGTH_LONG).show()
									} else if (isDataNew && updateList.size == 1)
										Snackbar.make(coordinatorLayoutView, R.string.hint_update_data_new, Snackbar.LENGTH_SHORT).show()
									else
										Snackbar.make(coordinatorLayoutView, R.string.hint_update_data, Snackbar.LENGTH_SHORT).show()
								}
							}
							ConstantsCode.ERROR_USERNAME, ConstantsCode.ERROR_PASSWORD -> {//前端信息错误
								isRefreshData = false
								Snackbar.make(coordinatorLayoutView, getString(R.string.hint_try_refresh_data_error, getCourseRT.msg), Snackbar.LENGTH_LONG)
										.setAction(android.R.string.ok) {
											startActivityForResult(Intent(this@MainActivity, LoginActivity::class.java), ADD_ACCOUNT_CODE)
										}
										.show()
							}
							ConstantsCode.ERROR_NOT_LOGIN -> Logs.i(TAG, "updateAllData: onNext: 未登录")
							else -> {
								Logs.i(TAG, "updateAllData: onNext: ${getCourseRT.rt} ${getCourseRT.msg}")
								Snackbar.make(coordinatorLayoutView, getCourseRT.msg, Snackbar.LENGTH_LONG)
										.show()
							}
						}
					}
				})
	}

	private fun updateData(student: Student): Observable<GetCourseRT> {
		return ScheduleHelper.tomcatRetrofit
				.create(StudentService::class.java)
				.getCourses(student.username, null, null)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map({ responseBody ->
					val getCourseRT = Gson().fromJson(InputStreamReader(responseBody.byteStream()), GetCourseRT::class.java)
					val parentFile = XhuFileUtil.getCourseCacheParentFile(this)
					if (!parentFile.exists())
						parentFile.mkdirs()
					val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
					when (getCourseRT.rt) {
						ConstantsCode.DONE, ConstantsCode.SERVER_COURSE_ANALYZE_ERROR -> {//请求成功或者数据存在问题
							val newFile = File(parentFile, base64Name + ".temp")
							newFile.createNewFile()
							XhuFileUtil.saveObjectToFile(getCourseRT.courses, newFile)
							val newMD5 = XhuFileUtil.getMD5(newFile)
							val oldFile = File(parentFile, base64Name)
							var oldMD5 = ""
							if (oldFile.exists())
								oldMD5 = XhuFileUtil.getMD5(oldFile)!!
							isDataNew = if (newMD5 != oldMD5) {
								oldFile.delete()
								newFile.renameTo(oldFile)
								Logs.i(TAG, "updateData: 数据更新")
								true
							} else {
								newFile.delete()
								Logs.i(TAG, "updateData: 数据未变")
								false
							}
						}
						ConstantsCode.ERROR_NOT_LOGIN -> {//未登录
							needLoginStudents.add(student)
						}
					}
					getCourseRT
				})
	}

	private fun login() {
		Logs.i(TAG, "login: needLogin: ${needLoginStudents.size}")
		val array = ArrayList<Observable<AutoLoginRT>>()
		needLoginStudents.forEach {
			Logs.i(TAG, "login: add: ${it.username}")
			array.add(ScheduleHelper.tomcatRetrofit
					.create(UserService::class.java)
					.autoLogin(it.username, it.password)
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), AutoLoginRT::class.java) }))
		}
		Observable.merge(array)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : DisposableObserver<AutoLoginRT>() {
					override fun onComplete() {
						needLoginStudents.clear()
						updateAllData()
					}

					override fun onNext(autoLoginRT: AutoLoginRT) {
						Logs.i(TAG, "onNext: rt: ${autoLoginRT.rt}")
						if (autoLoginRT.rt != ConstantsCode.DONE) {
							val snackBar = Snackbar.make(coordinatorLayoutView, autoLoginRT.msg, Snackbar.LENGTH_LONG)
							if (autoLoginRT.rt.startsWith('4'))
								snackBar.setAction(android.R.string.ok) {
									startActivityForResult(Intent(this@MainActivity, LoginActivity::class.java), ADD_ACCOUNT_CODE)
								}
							snackBar.show()
						}
					}

					override fun onError(e: Throwable) {
						loadingDialog.dismiss()
						isRefreshData = false
						needLoginStudents.clear()
						e.printStackTrace()
						if (e is UnknownHostException)
							Snackbar.make(coordinatorLayoutView, R.string.error_network, Snackbar.LENGTH_SHORT)
									.show()
						else
							Snackbar.make(coordinatorLayoutView, "请求出错：${e.message.toString()}，请重试", Snackbar.LENGTH_SHORT)
									.show()
					}
				})
	}

	private fun showcase() {
		val size = Point()
		windowManager.defaultDisplay.getSize(size)
		TapTargetSequence(this)
				.targets(
						TapTarget.forView(bottomNavigationView.findViewById(R.id.bottom_nav_today), getString(R.string.showcase_today)),
						TapTarget.forView(bottomNavigationView.findViewById(R.id.bottom_nav_week), getString(R.string.showcase_week)),
						TapTarget.forView(action_sync, getString(R.string.showcase_sync)))
				.continueOnCancel(true)
				.considerOuterCircleCanceled(true)
				.listener(object : TapTargetSequence.Listener {
					override fun onSequenceCanceled(lastTarget: TapTarget?) {
					}

					override fun onSequenceFinish() {
						Settings.isFirstRun = false
					}

					override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
					}
				})
				.start()
	}

	private fun showWeekAnim(isShow: Boolean, isShowArrow: Boolean) {
		titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, if (isShowArrow) arrowDrawable else null, null)
		weekAnimator?.cancel()
		weekAnimator = if (isShow)
			ObjectAnimator.ofFloat(layout_week_recycler_view, Constants.ANIMATION_TRANSLATION_Y, 0F, Mystery0DensityUtil.dip2px(this, 56F).toFloat())
		else
			ObjectAnimator.ofFloat(layout_week_recycler_view, Constants.ANIMATION_TRANSLATION_Y, Mystery0DensityUtil.dip2px(this, 56F).toFloat(), 0F)
		weekAnimator?.addListener(object : Animator.AnimatorListener {
			override fun onAnimationRepeat(animation: Animator?) {
			}

			override fun onAnimationEnd(animation: Animator?) {
				if (!isShow && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
					layout_week_recycler_view.elevation = 6F
			}

			override fun onAnimationCancel(animation: Animator?) {
			}

			override fun onAnimationStart(animation: Animator?) {
				if (isShow && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
					layout_week_recycler_view.elevation = 0F
			}
		})
		weekAnimator?.start()
		val start = if (isShow) 0 else 10000
		val end = if (isShow) 10000 else 0
		ObjectAnimator.ofInt(arrowDrawable!!, Constants.ANIMATION_LEVEL, start, end).start()
		isWeekShow = isShow
	}

	private fun swipeLayout(itemId: Int) {
		when (itemId) {
			R.id.bottom_nav_today -> {
				if (lastIndex == 2)
					setRefresh()
				lastIndex = viewpager.currentItem
				if (isWeekShow)
					showWeekAnim(false, false)
				titleTextView.setOnClickListener(null)
				titleTextView.text = CalendarUtil.getTodayInfo(this@MainActivity)
				titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
			}
			R.id.bottom_nav_week -> {
				if (lastIndex == 2)
					setRefresh()
				lastIndex = viewpager.currentItem
				titleTextView.setOnClickListener {
					showWeekAnim(!isWeekShow, true)
				}
				titleTextView.text = getString(R.string.course_week_index, ScheduleHelper.weekIndex)
				titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null)
			}
			R.id.bottom_nav_profile -> {
				if (lastIndex != 2)
					setSettings()
				lastIndex = viewpager.currentItem
				if (isWeekShow)
					showWeekAnim(false, false)
				profileFragment.updateNoticeBadge()
				titleTextView.text = getString(R.string.course_profile_title)
				titleTextView.setOnClickListener(null)
				titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
				if (mainStudent?.profile == null) {
					updateProfileDialog.show()
					mainStudent?.getInfo(object : ProfileListener {
						override fun error(rt: Int, e: Throwable) {
							updateProfileDialog.dismiss()
							Logs.e(TAG, "error: " + rt)
							e.printStackTrace()
						}

						override fun got(profile: Profile) {
							updateProfileDialog.dismiss()
							XhuFileUtil.saveObjectToFile(studentList, XhuFileUtil.getStudentListFile(this@MainActivity))
							profileFragment.setProfile(profile)
						}
					})
				} else
					profileFragment.setProfile(mainStudent?.profile!!)
			}
		}
	}

	private fun setRefresh() {
		animatorList.forEach { it.cancel() }
		animatorList.clear()
		animatorList.add(ObjectAnimator.ofFloat(action_sync, Constants.ANIMATION_ROTATION, 360F, 0F).setDuration(ANIMATION_DURATION))
		animatorList.add(ObjectAnimator.ofFloat(action_sync, Constants.ANIMATION_TRANSLATION_X, Mystery0DensityUtil.dip2px(this, 68F).toFloat(), 0F).setDuration(ANIMATION_DURATION))
		animatorList.add(ObjectAnimator.ofFloat(action_settings, Constants.ANIMATION_ROTATION, 0F, 360F).setDuration(ANIMATION_DURATION))
		animatorList.add(ObjectAnimator.ofFloat(action_settings, Constants.ANIMATION_TRANSLATION_X, -Mystery0DensityUtil.dip2px(this, 68F).toFloat(), 0F).setDuration(ANIMATION_DURATION))
		animatorList.forEach { it.start() }
	}

	private fun setSettings() {
		animatorList.forEach { it.cancel() }
		animatorList.clear()
		animatorList.add(ObjectAnimator.ofFloat(action_sync, Constants.ANIMATION_ROTATION, 0F, 360F).setDuration(ANIMATION_DURATION))
		animatorList.add(ObjectAnimator.ofFloat(action_sync, Constants.ANIMATION_TRANSLATION_X, 0F, Mystery0DensityUtil.dip2px(this, 68F).toFloat()).setDuration(ANIMATION_DURATION))
		animatorList.add(ObjectAnimator.ofFloat(action_settings, Constants.ANIMATION_ROTATION, 360F, 0F).setDuration(ANIMATION_DURATION))
		animatorList.add(ObjectAnimator.ofFloat(action_settings, Constants.ANIMATION_TRANSLATION_X, 0F, -Mystery0DensityUtil.dip2px(this, 68F).toFloat()).setDuration(ANIMATION_DURATION))
		animatorList.forEach { it.start() }
	}

	override fun onBackPressed() {
		val press = Calendar.getInstance().timeInMillis
		if (press - lastPressBack <= 2000) {
			super.onBackPressed()
		} else {
			lastPressBack = press
			Toast.makeText(this, R.string.hint_twice_press_exit, Toast.LENGTH_SHORT).show()
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when {
			requestCode == ADD_ACCOUNT_CODE && resultCode == Activity.RESULT_OK ->
				updateAllData()
			requestCode == com.tencent.connect.common.Constants.REQUEST_QQ_SHARE ->
				Tencent.onActivityResultData(requestCode, resultCode, data, APP.tencentListener)
			else ->
				finish()
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	private fun registerWeibo() {
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.WEIBO_API_KEY, false)
	}

	private fun registerWeiXin() {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		wxAPI = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_API_KEY, false)
		// 将该app注册到微信
		wxAPI.registerApp(Constants.WEIXIN_API_KEY)
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		mWeiboShareAPI.handleWeiboResponse(intent) {
			Logs.i(TAG, "registerWeibo: $it")
		}
	}
}
