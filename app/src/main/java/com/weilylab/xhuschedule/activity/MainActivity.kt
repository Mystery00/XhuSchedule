/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午3:19
 */

package com.weilylab.xhuschedule.activity

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ViewPagerAdapter
import com.weilylab.xhuschedule.adapter.WeekAdapter
import com.weilylab.xhuschedule.classes.*
import com.weilylab.xhuschedule.classes.rt.CourseRT
import com.weilylab.xhuschedule.classes.rt.LoginRT
import com.weilylab.xhuschedule.classes.rt.StudentInfoRT
import com.weilylab.xhuschedule.fragment.ProfileFragment
import com.weilylab.xhuschedule.fragment.TableFragment
import com.weilylab.xhuschedule.fragment.TodayFragment
import com.weilylab.xhuschedule.interfaces.CourseService
import com.weilylab.xhuschedule.listener.WeekChangeListener
import com.weilylab.xhuschedule.util.*
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

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = "MainActivity"
    }

    private lateinit var loadingDialog: ZLoadingDialog
    private lateinit var weekAdapter: WeekAdapter
    private var isTryRefreshData = false
    private var isTryLogin = false
    private var isRefreshData = false
    private var isWeekShow = false
    private var isAnimShow = false
    private var isDataNew = false
    private var studentList = ArrayList<Student>()
    private var weekList = LinkedList<LinkedList<Course>>()
    private val todayList = ArrayList<Course>()
    private val todayFragment = TodayFragment.newInstance(todayList)
    private val weekFragment = TableFragment.newInstance(weekList)
    private val profileFragment = ProfileFragment.newInstance(Profile())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadingDialog = ZLoadingDialog(this)
                .setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
                .setHintText(getString(R.string.hint_dialog_update_cache))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))

        initView()
        if (ScheduleHelper.isFromLogin)
            updateAllData()
        else {
            Logs.i(TAG, "show1")
            loadingDialog.show()
            updateAllView()
        }
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
            if (sharedPreference.getInt("updateVersion", 0) == getString(R.string.app_version_code).toInt() - 1)
                dialog.setPositiveButton("我真的明白了", { _, _ ->
                    sharedPreference.edit().putInt("updateVersion", getString(R.string.app_version_code).toInt()).apply()
                })
            else
                dialog.setPositiveButton(android.R.string.ok, null)
                        .setOnDismissListener {
                            sharedPreference.edit().putInt("updateVersion", getString(R.string.app_version_code).toInt() - 1).apply()
                        }
            if (ScheduleHelper.isLogin)
                dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ScheduleHelper.isImageChange) {
            val options = RequestOptions()
                    .signature(MediaStoreSignature("image/*", Calendar.getInstance().timeInMillis, 0))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            if (Settings.customBackgroundImg != "")
                Glide.with(this)
                        .load(Settings.customBackgroundImg)
                        .apply(options)
                        .into(background)
            else {
                val calendar = Calendar.getInstance()
                val month = calendar.get(Calendar.MONTH) + 1
                when (month) {
                    3, 4, 5 -> background.setImageResource(R.mipmap.a)
                    6, 7, 8 -> background.setImageResource(R.mipmap.b)
                    9, 10, 11 -> background.setImageResource(R.mipmap.c)
                    12, 1, 2 -> background.setImageResource(R.mipmap.d)
                }
            }
            profileFragment.setHeaderImg()
            profileFragment.setProfileImg()
        }
        if (ScheduleHelper.isUIChange) {
            loadingDialog.show()
            studentList.clear()
            studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
            titleTextView.setTextColor(Settings.customTableTextColor)
            Logs.i(TAG, "show2")
            loadingDialog.show()
            updateAllView()
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

        if (studentList.size > 0)
            if (studentList[0].profile != null)
                profileFragment.setProfile(studentList[0].profile!!)
            else
                updateProfile(studentList[0])

        weekAdapter = WeekAdapter(this, 1)
        weekAdapter.setWeekChangeListener(object : WeekChangeListener {
            override fun onChange(week: Int) {
                ScheduleHelper.weekIndex = week + 1
                weekAdapter.setWeekIndex(ScheduleHelper.weekIndex)
                titleTextView.text = getString(R.string.course_week_index, ScheduleHelper.weekIndex)
                updateAllView(ScheduleHelper.weekIndex)
            }
        })
        layout_week_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        layout_week_recycler_view.adapter = weekAdapter
        layout_week_recycler_view.scrollToPosition(0)

        val options = RequestOptions()
                .signature(MediaStoreSignature("image/*", Calendar.getInstance().timeInMillis, 0))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        if (Settings.customBackgroundImg != "")
            Glide.with(this)
                    .load(Settings.customBackgroundImg)
                    .apply(options)
                    .into(background)
        else {
            val calendar = Calendar.getInstance()
            val month = calendar.get(Calendar.MONTH) + 1
            when (month) {
                3, 4, 5 -> background.setImageResource(R.mipmap.a)
                6, 7, 8 -> background.setImageResource(R.mipmap.b)
                9, 10, 11 -> background.setImageResource(R.mipmap.c)
                12, 1, 2 -> background.setImageResource(R.mipmap.d)
            }
        }
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_today -> viewpager.currentItem = 0
                R.id.bottom_nav_week -> viewpager.currentItem = 1
                R.id.bottom_nav_profile -> viewpager.currentItem = 2
            }
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
                if (bottomNavigationView.menu.getItem(position).itemId != R.id.bottom_nav_week) {
                    if (isWeekShow)
                        showWeekAnim(false)
                    titleTextView.visibility = View.GONE
                } else
                    titleTextView.visibility = View.VISIBLE
            }
        })

        action_sync.setOnClickListener {
            if (isRefreshData)
                return@setOnClickListener
            isTryRefreshData = false
            updateAllData()
        }
        titleLayout.setOnClickListener {
            Logs.i(TAG, "initView: titleLayout")
        }
        titleTextView.setOnClickListener {
            showWeekAnim(!isWeekShow)
        }
    }

    fun updateAllView() {
        updateAllView(-1)
    }

    fun updateAllView(week: Int) {
        studentList.clear()
        studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
        if (studentList.size == 0) {
            ScheduleHelper.isLogin = false
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        weekList.clear()
        todayList.clear()
        ScheduleHelper.isLogin = true
        val array = ArrayList<Observable<Boolean>>()
        val showFile = File(filesDir.absolutePath + File.separator + "data" + File.separator + "show_user")
        val showList = XhuFileUtil.getArrayListFromFile(showFile, Student::class.java)
        if (showList.size == 0)
            showList.addAll(studentList)
        showList.forEach {
            array.add(updateView(it, week))
        }
        Observable.merge(array)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Boolean>() {

                    override fun onComplete() {
                        Logs.i(TAG, "dismiss1")
                        loadingDialog.dismiss()
                        if (viewpager.currentItem != 1)
                            titleTextView.visibility = View.GONE
                        else
                            titleTextView.visibility = View.VISIBLE
                        titleTextView.text = getString(R.string.course_week_index, ScheduleHelper.weekIndex)
                        weekAdapter.setWeekIndex(ScheduleHelper.weekIndex)
                        layout_week_recycler_view.scrollToPosition(ScheduleHelper.weekIndex - 1)
                        if (ScheduleHelper.isCookieAvailable) {
                            if (showList.size == 1)
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
                            showList.forEach {
                                val tempWeekList = CourseUtil.mergeCourses(weekList, it.weekCourses)
                                weekList.clear()
                                weekList.addAll(tempWeekList)
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
                        Logs.i(TAG, "dismiss2")
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
            val weekArray = if (week != -1)
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
            student.weekCourses.addAll(weekArray)
            val todayArray = CourseUtil.getTodayCourses(XhuFileUtil.getCoursesFromFile(this@MainActivity, oldFile))
            student.todayCourses.clear()
            student.todayCourses.addAll(todayArray)
            student.isReady = true
            subscriber.onComplete()
        }
    }

    private fun updateAllData() {
        Logs.i(TAG, "show3")
        loadingDialog.show()
        studentList.clear()
        studentList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
        if (studentList.size == 0) {
            ScheduleHelper.isLogin = false
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        updateProfile(studentList[0])
        weekList.clear()
        todayList.clear()
        val array = ArrayList<Observable<CourseRT>>()
        val showFile = File(filesDir.absolutePath + File.separator + "data" + File.separator + "show_user")
        val showList = XhuFileUtil.getArrayListFromFile(showFile, Student::class.java)
        if (showList.size == 0)
            showList.addAll(studentList)
        isRefreshData = true
        showList.forEach {
            array.add(updateData(it))
        }
        Observable.merge(array)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<CourseRT>() {
                    private var courseRT: CourseRT? = null
                    override fun onError(e: Throwable) {
                        Logs.i(TAG, "dismiss3")
                        loadingDialog.dismiss()
                        isRefreshData = false
                        e.printStackTrace()
                        if (e is UnknownHostException)
                            Snackbar.make(coordinatorLayout, R.string.error_network, Snackbar.LENGTH_SHORT)
                                    .show()
                        else
                            Snackbar.make(coordinatorLayout, "请求出错：" + e.message + "，请重试", Snackbar.LENGTH_SHORT)
                                    .show()
                    }

                    override fun onComplete() {
                        if (courseRT?.rt == "6") {
                            ScheduleHelper.isLogin = false
                            return
                        }
                        if (!isTryRefreshData && courseRT?.rt == "0") {
                            isTryRefreshData = true
                            updateAllData()
                            return
                        }
                        Logs.i(TAG, "dismiss4")
                        loadingDialog.dismiss()
                        if (courseRT?.rt != "1" && courseRT?.rt != "5") {
                            isRefreshData = false
                            Snackbar.make(coordinatorLayout, R.string.hint_invalid_cookie, Snackbar.LENGTH_LONG)
                                    .setAction(android.R.string.ok) {
                                        ScheduleHelper.isLogin = false
                                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                    .show()
                            return
                        }
                        isRefreshData = false
                        if (courseRT?.rt == "5")
                            Snackbar.make(coordinatorLayout, R.string.hint_update_data_error, Snackbar.LENGTH_LONG).show()
                        else {
                            if (isDataNew && showList.size == 1)
                                Snackbar.make(coordinatorLayout, R.string.hint_update_data_new, Snackbar.LENGTH_SHORT).show()
                            else
                                Snackbar.make(coordinatorLayout, R.string.hint_update_data, Snackbar.LENGTH_SHORT).show()
                        }
                        updateAllView()
                    }

                    override fun onNext(t: CourseRT) {
                        courseRT = t
                    }
                })
    }

    private fun updateData(student: Student): Observable<CourseRT> {
        val parentFile = File(filesDir.absolutePath + File.separator + "caches/")
        if (!parentFile.exists())
            parentFile.mkdirs()
        val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
        return ScheduleHelper.tomcatRetrofit
                .create(CourseService::class.java)
                .getCourses(student.username)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), CourseRT::class.java) })
                .subscribeOn(Schedulers.io())
                .doOnNext { contentRT ->
                    when (contentRT.rt) {
                        "1", "5" -> {
                            val newFile = File(parentFile, base64Name + ".temp")
                            newFile.createNewFile()
                            XhuFileUtil.saveObjectToFile(contentRT.courses, newFile)
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
                            Logs.i(TAG, "dismiss5")
                            loadingDialog.dismiss()
                        }
                        "2" -> {
                            Logs.i(TAG, "dismiss6")
                            loadingDialog.dismiss()
                            isRefreshData = false
                            ScheduleHelper.isLogin = false
                            Snackbar.make(coordinatorLayout, getString(R.string.hint_try_refresh_data_error, getString(R.string.error_invalid_username)), Snackbar.LENGTH_LONG)
                                    .setAction(android.R.string.ok) {
                                        ScheduleHelper.isLogin = false
                                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                    .show()
                        }
                        "3" -> {
                            Logs.i(TAG, "dismiss7")
                            loadingDialog.dismiss()
                            isRefreshData = false
                            ScheduleHelper.isLogin = false
                            Snackbar.make(coordinatorLayout, getString(R.string.hint_try_refresh_data_error, getString(R.string.error_invalid_password)), Snackbar.LENGTH_LONG)
                                    .setAction(android.R.string.ok) {
                                        ScheduleHelper.isLogin = false
                                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                    .show()
                        }
                        else -> {
                            isTryLogin = false
                            login(student)
                        }
                    }
                }
    }

    private fun updateProfile(student: Student) {
        student.getInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<StudentInfoRT>() {
                    private var profile = Profile()
                    override fun onNext(t: StudentInfoRT) {
                        profile.map(t)
                    }

                    override fun onComplete() {
                        XhuFileUtil.saveObjectToFile(studentList, File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"))
                        profileFragment.setProfile(profile)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    private fun login(student: Student) {
        student.login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<LoginRT> {
                    private var loginRT: LoginRT? = null
                    override fun onError(e: Throwable) {
                        Logs.i(TAG, "dismiss8")
                        loadingDialog.dismiss()
                        isRefreshData = false
                        e.printStackTrace()
                        if (e is UnknownHostException)
                            Toast.makeText(this@MainActivity, R.string.error_network, Toast.LENGTH_SHORT)
                                    .show()
                        else
                            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT)
                                    .show()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {
                        if (loginRT?.rt != "1") {
                            Logs.i(TAG, "dismiss9")
                            loadingDialog.dismiss()
                        }
                        when (loginRT?.rt) {
                            "0" -> {
                                if (!isTryLogin)
                                    login(student)
                                else {
                                    isRefreshData = false
                                    ScheduleHelper.isLogin = false
                                    Snackbar.make(coordinatorLayout, getString(R.string.hint_try_refresh_data_error, getString(R.string.error_timeout)), Snackbar.LENGTH_LONG)
                                            .setAction(android.R.string.ok) {
                                                ScheduleHelper.isLogin = false
                                                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                                finish()
                                            }
                                            .show()
                                }
                            }
                            "1" -> {
                                ScheduleHelper.isLogin = true
                                updateData(student)
                            }
                            "2" -> {
                                isRefreshData = false
                                ScheduleHelper.isLogin = false
                                Snackbar.make(coordinatorLayout, getString(R.string.hint_try_refresh_data_error, getString(R.string.error_invalid_username)), Snackbar.LENGTH_LONG)
                                        .setAction(android.R.string.ok) {
                                            ScheduleHelper.isLogin = false
                                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                            finish()
                                        }
                                        .show()
                            }
                            "3" -> {
                                isRefreshData = false
                                ScheduleHelper.isLogin = false
                                Snackbar.make(coordinatorLayout, getString(R.string.hint_try_refresh_data_error, getString(R.string.error_invalid_password)), Snackbar.LENGTH_LONG)
                                        .setAction(android.R.string.ok) {
                                            ScheduleHelper.isLogin = false
                                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                            finish()
                                        }
                                        .show()
                            }
                            else -> {
                                isRefreshData = false
                                ScheduleHelper.isLogin = false
                                Snackbar.make(coordinatorLayout, getString(R.string.hint_try_refresh_data_error, getString(R.string.error_other)), Snackbar.LENGTH_LONG)
                                        .setAction(android.R.string.ok) {
                                            ScheduleHelper.isLogin = false
                                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                            finish()
                                        }
                                        .show()
                            }
                        }
                    }

                    override fun onNext(t: LoginRT) {
                        loginRT = t
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

    private fun showWeekAnim(isShow: Boolean) {
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
                            val drawable = if (isWeekShow)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    resources.getDrawable(R.drawable.ic_expand_less, null)
                                } else {
                                    null
                                }
                            else
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    resources.getDrawable(R.drawable.ic_expand_more, null)
                                } else {
                                    null
                                }
                            if (drawable != null) {
                                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                                titleTextView.setCompoundDrawables(null, null, drawable, null)
                            }
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
}
