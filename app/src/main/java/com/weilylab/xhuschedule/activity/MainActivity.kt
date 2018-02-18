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

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.widget.Toast
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ViewPagerAdapter
import com.weilylab.xhuschedule.adapter.WeekAdapter
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.classes.baseClass.Profile
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.classes.rt.GetCourseRT
import com.weilylab.xhuschedule.fragment.ProfileFragment
import com.weilylab.xhuschedule.fragment.TableFragment
import com.weilylab.xhuschedule.fragment.TodayFragment
import com.weilylab.xhuschedule.interfaces.StudentService
import com.weilylab.xhuschedule.listener.LoginListener
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
import java.io.File
import java.io.InputStreamReader
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity() {
    companion object {
        private val TAG = "MainActivity"
        private const val ADD_ACCOUNT_CODE = 1
    }

    private lateinit var loadingDialog: Dialog
    private lateinit var updateProfileDialog: Dialog
    private lateinit var weekAdapter: WeekAdapter
    private lateinit var mainStudent: Student
    private var arrowDrawable: Drawable? = null
    private var isTryRefreshData = false
    private var isRefreshData = false
    private var isWeekShow = false
    private var isAnimShow = false
    private var isDataNew = false
    private var lastPressBack = 0L
    private var studentList = ArrayList<Student>()
    private var weekList = ArrayList<ArrayList<ArrayList<Course>>>()
    private val todayList = ArrayList<Course>()
    private val todayFragment = TodayFragment.newInstance(todayList)
    private val weekFragment = TableFragment.newInstance(weekList)
    private val profileFragment = ProfileFragment.newInstance(Profile())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "main")
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params)
        setContentView(R.layout.activity_main)
        arrowDrawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ms__arrow)
        loadingDialog = ZLoadingDialog(this)
                .setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
                .setHintText(getString(R.string.hint_dialog_update_cache))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .create()
        updateProfileDialog = ZLoadingDialog(this)
                .setLoadingBuilder(Z_TYPE.CIRCLE_CLOCK)
                .setHintText(getString(R.string.hint_dialog_update_profile))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .create()
        initView()
        updateAllView()
        showUpdateLog()
        if (Settings.isFirstRun)
            showcase()
    }

    private fun showUpdateLog() {
        val sharedPreference = getSharedPreferences("updateData", Context.MODE_PRIVATE)
        if (sharedPreference.getInt("updateVersion", 0) < getString(R.string.app_version_code).toInt()) {
            var message = ""
            resources.getStringArray(R.array.update_list)
                    .forEach { message += it + '\n' }
            val dialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_title_update_log, getString(R.string.app_version_name) + '-' + getString(R.string.app_version_code)))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, null)
                    .setOnDismissListener {
                        sharedPreference.edit().putInt("updateVersion", getString(R.string.app_version_code).toInt()).apply()
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
            if (Settings.customBackgroundImg != "") {
                todayFragment.setBackground()
                weekFragment.setBackground()
            }
            profileFragment.setProfileImg()
        }
        if (ScheduleHelper.isUIChange) {
            loadingDialog.show()
            studentList.clear()
            studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
            titleTextView.setTextColor(Settings.customTableTextColor)
            updateAllView()
        }
        if (bottomNavigationView.menu.getItem(2).isChecked) {//刷新小红点状态
            profileFragment.updateNoticeBadge()
        }
        ScheduleHelper.isImageChange = false
        ScheduleHelper.isUIChange = false
    }

    private fun initView() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(todayFragment)
        viewPagerAdapter.addFragment(weekFragment)
        viewPagerAdapter.addFragment(profileFragment)
        viewpager.offscreenPageLimit = 2
        viewpager.adapter = viewPagerAdapter

        studentList.clear()
        studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))

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

        if (Settings.customBackgroundImg != "") {
            todayFragment.setBackground()
            weekFragment.setBackground()
        }
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
        studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
        if (studentList.size == 0) {
            ScheduleHelper.isLogin = false
            startActivityForResult(Intent(this, LoginActivity::class.java), ADD_ACCOUNT_CODE)
            return
        }
        loadingDialog.show()
        //清空数组
        weekList.clear()
        todayList.clear()
        ScheduleHelper.isLogin = true
        val array = ArrayList<Observable<Boolean>>()
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
                .subscribe(object : DisposableObserver<Boolean>() {
                    override fun onComplete() {
                        loadingDialog.dismiss()
                        swipeLayout(bottomNavigationView.menu.getItem(viewpager.currentItem).itemId)
                        weekAdapter.setWeekIndex(ScheduleHelper.weekIndex)
                        layout_week_recycler_view.scrollToPosition(ScheduleHelper.weekIndex - 1)
                        if (ScheduleHelper.isCookieAvailable) {
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
                        } else
                            updateAllData()
                        isRefreshData = false
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        isRefreshData = false
                        loadingDialog.dismiss()
                    }

                    override fun onNext(map: Boolean) {
                    }
                })
    }

    private fun updateView(student: Student, week: Int): Observable<Boolean> {
        return Observable.create<Boolean> { subscriber ->
            val parentFile = File(filesDir.absolutePath + File.separator + "caches/")
            if (!parentFile.exists())
                parentFile.mkdirs()
            val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
            //判断是否有缓存
            val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
            if (!cacheResult) {
                ScheduleHelper.isCookieAvailable = false
                subscriber.onComplete()
                return@create
            }
            val oldFile = File(parentFile, base64Name)
            if (!oldFile.exists()) {
                ScheduleHelper.isCookieAvailable = false
                subscriber.onComplete()
                return@create
            }
            val courses = XhuFileUtil.getCoursesFromFile(this@MainActivity, oldFile)
            if (courses.isEmpty()) {
                ScheduleHelper.isCookieAvailable = false
                subscriber.onComplete()
                return@create
            }
            ScheduleHelper.isCookieAvailable = true
            val tempArray = if (week != -1)
                if (Settings.isShowNot)
                    CourseUtil.formatCourses(XhuFileUtil.getCoursesFromFile(this@MainActivity, oldFile), week)
                else
                    CourseUtil.getWeekCourses(XhuFileUtil.getCoursesFromFile(this@MainActivity, oldFile), week)
            else
                if (Settings.isShowNot)
                    CourseUtil.formatCourses(XhuFileUtil.getCoursesFromFile(this@MainActivity, oldFile))
                else
                    CourseUtil.getWeekCourses(XhuFileUtil.getCoursesFromFile(this@MainActivity, oldFile))
            student.weekCourses.clear()
            student.weekCourses.addAll(tempArray)
            val todayArray = CourseUtil.getTodayCourses(XhuFileUtil.getCoursesFromFile(this@MainActivity, oldFile))
            student.todayCourses.clear()
            student.todayCourses.addAll(todayArray)
            student.isReady = true
            subscriber.onComplete()
        }
    }

    private fun updateAllData() {
        Logs.i(TAG, "updateAllData: ")
        loadingDialog.show()
        ObjectAnimator.ofFloat(action_sync, "rotation", 0F, 360F).setDuration(1000).start()
        studentList.clear()
        studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
        if (studentList.size == 0) {
            ScheduleHelper.isLogin = false
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
                .subscribeWith(object : DisposableObserver<GetCourseRT>() {
                    private var getCourseRT: GetCourseRT? = null
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
                        Logs.i(TAG, "updateAllData: onComplete: " + getCourseRT?.rt)
                        loadingDialog.dismiss()
                        isRefreshData = false
                        when (getCourseRT?.rt) {
                            "0", "202" -> {
                                if (getCourseRT?.rt == "202")
                                    Snackbar.make(coordinatorLayoutView, R.string.hint_update_data_error, Snackbar.LENGTH_LONG).show()
                                else {
                                    if (ScheduleHelper.isAnalysisError) {
                                        Snackbar.make(coordinatorLayoutView, R.string.hint_analyze_error, Snackbar.LENGTH_LONG).show()
                                    } else if (isDataNew && updateList.size == 1)
                                        Snackbar.make(coordinatorLayoutView, R.string.hint_update_data_new, Snackbar.LENGTH_SHORT).show()
                                    else
                                        Snackbar.make(coordinatorLayoutView, R.string.hint_update_data, Snackbar.LENGTH_SHORT).show()
                                }
                                sendBroadcast(Intent("android.appwidget.action.APPWIDGET_UPDATE")
                                        .putExtra("TAG", WidgetHelper.ALL_TAG))
                                updateAllView()
                            }
                            else -> Logs.i(TAG, "onComplete: ${getCourseRT?.rt} ${getCourseRT?.msg}")
                        }
                    }

                    override fun onNext(t: GetCourseRT) {
                        getCourseRT = t
                    }
                })
    }

    private fun updateData(student: Student): Observable<GetCourseRT> {
        val parentFile = File(filesDir.absolutePath + File.separator + "caches/")
        if (!parentFile.exists())
            parentFile.mkdirs()
        val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
        return ScheduleHelper.tomcatRetrofit
                .create(StudentService::class.java)
                .getCourses(student.username, null, null)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), GetCourseRT::class.java) })
                .subscribeOn(Schedulers.io())
                .doOnNext { getCourseRT ->
                    Logs.i(TAG, "updateData: " + getCourseRT.rt)
                    when (getCourseRT.rt) {
                        "0", "202" -> {//请求成功或者数据存在问题
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
                            loadingDialog.dismiss()
                        }
                        "401", "402" -> {//前端信息错误
                            loadingDialog.dismiss()
                            isRefreshData = false
                            ScheduleHelper.isLogin = false
                            Snackbar.make(coordinatorLayoutView, getString(R.string.hint_try_refresh_data_error, getCourseRT?.msg), Snackbar.LENGTH_LONG)
                                    .setAction(android.R.string.ok) {
                                        ScheduleHelper.isLogin = false
                                        startActivityForResult(Intent(this, LoginActivity::class.java), ADD_ACCOUNT_CODE)
                                    }
                                    .show()
                        }
                        "405" -> {//未登录
                            login(student)
                        }
                        else -> {
                            loadingDialog.dismiss()
                            isRefreshData = false
                            Snackbar.make(coordinatorLayoutView, getCourseRT.msg, Snackbar.LENGTH_LONG)
                                    .show()
                        }
                    }
                }
    }

    private fun login(student: Student) {
        student.login(object : LoginListener {
            override fun error(rt: Int, e: Throwable) {
                isRefreshData = false
                ScheduleHelper.isLogin = false
                loadingDialog.dismiss()
                Snackbar.make(coordinatorLayoutView, e.message.toString(), Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.ok) {
                            ScheduleHelper.isLogin = false
                            startActivityForResult(Intent(this@MainActivity, LoginActivity::class.java), ADD_ACCOUNT_CODE)
                        }
                        .show()
            }

            override fun loginDone() {
                ScheduleHelper.isLogin = true
                updateAllData()
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
        if (isAnimShow)
            return
        val trueHeight = DensityUtil.dip2px(this, 60F)
        layout_week_recycler_view.post {
            val height = layout_week_recycler_view.measuredHeight
            val barLayoutParams = appBar.layoutParams
            Observable.create<Int> { subscriber ->
                val showDistanceArray = Array(31, { i -> (height / 30F) * i })
                if (!isShow)
                    showDistanceArray.reverse()
                showDistanceArray.forEach {
                    subscriber.onNext(it.toInt())
                    Thread.sleep(8)
                }
                subscriber.onComplete()
            }
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Int> {
                        override fun onSubscribe(d: Disposable) {
                            isAnimShow = true
                        }

                        override fun onComplete() {
                            isWeekShow = isShow
                            titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, if (isShowArrow) arrowDrawable else null, null)
                            val start = if (isShow) 0 else 10000
                            val end = if (isShow) 10000 else 0
                            val animator = ObjectAnimator.ofInt(arrowDrawable!!, "level", start, end)
                            animator.start()
                            isAnimShow = false
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            isAnimShow = false
                        }

                        override fun onNext(t: Int) {
                            barLayoutParams.height = trueHeight + t
                            appBar.layoutParams = barLayoutParams
                        }
                    })
        }
    }

    private fun swipeLayout(itemId: Int) {
        when (itemId) {
            R.id.bottom_nav_today -> {
                if (isWeekShow)
                    showWeekAnim(false, false)
                titleTextView.setOnClickListener(null)
                titleTextView.text = CalendarUtil.getTodayInfo(this@MainActivity)
                titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
            R.id.bottom_nav_week -> {
                titleTextView.setOnClickListener {
                    showWeekAnim(!isWeekShow, true)
                }
                titleTextView.text = getString(R.string.course_week_index, ScheduleHelper.weekIndex)
                titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null)
            }
            R.id.bottom_nav_profile -> {
                if (isWeekShow)
                    showWeekAnim(false, false)
                profileFragment.updateNoticeBadge()
                titleTextView.text = getString(R.string.course_profile_title)
                titleTextView.setOnClickListener(null)
                titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                if (mainStudent.profile == null) {
                    updateProfileDialog.show()
                    mainStudent.getInfo(object : ProfileListener {
                        override fun error(rt: Int, e: Throwable) {
                            updateProfileDialog.dismiss()
                            Logs.e(TAG, "error: " + rt)
                            e.printStackTrace()
                        }

                        override fun got(profile: Profile) {
                            updateProfileDialog.dismiss()
                            XhuFileUtil.saveObjectToFile(studentList, File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"))
                            profileFragment.setProfile(profile)
                        }
                    })
                } else
                    profileFragment.setProfile(mainStudent.profile!!)
            }
        }
    }

    override fun onBackPressed() {
        val press = Calendar.getInstance().timeInMillis
        if (press - lastPressBack <= 1000) {
            super.onBackPressed()
        } else {
            lastPressBack = press
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ACCOUNT_CODE && resultCode == Activity.RESULT_OK) {
            updateAllData()
        } else
            finish()
    }
}
