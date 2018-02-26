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

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.classes.baseClass.Profile
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.classes.baseClass.TableLayoutHelper
import com.weilylab.xhuschedule.classes.rt.GetCourseRT
import com.weilylab.xhuschedule.interfaces.StudentService
import com.weilylab.xhuschedule.listener.LoginListener
import com.weilylab.xhuschedule.listener.ProfileListener
import com.weilylab.xhuschedule.util.*
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.content_schedule.*
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.io.InputStreamReader
import java.net.UnknownHostException
import java.util.*
import kotlin.math.max

class ScheduleActivity : BaseActivity() {
    private val TAG = "ScheduleActivity"
    private lateinit var initDialog: Dialog
    private lateinit var loadingDialog: Dialog
    private val studentList = ArrayList<Student>()
    private var weekList = ArrayList<ArrayList<ArrayList<Course>>>()
    private var currentStudent: Student? = null
    private var year: String? = null
    private var term: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "schedule")
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params)
        setContentView(R.layout.activity_schedule)
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
                .setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
                .setHintText(getString(R.string.hint_dialog_sync))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .create()
        val options = RequestOptions()
                .signature(MediaStoreSignature("image/*", Calendar.getInstance().timeInMillis, 0))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        if (Settings.customBackgroundImg != "")
            Glide.with(this)
                    .load(Settings.customBackgroundImg)
                    .apply(options)
                    .into(background)
        val tableNav = table_nav as LinearLayout
        for (i in 0 until tableNav.childCount) {
            val layoutParams = tableNav.getChildAt(i).layoutParams
            layoutParams.height = DensityUtil.dip2px(this, Settings.customTextHeight.toFloat())
            tableNav.getChildAt(i).layoutParams = layoutParams
            (tableNav.getChildAt(i) as TextView).setTextColor(Settings.customTableTextColor)
        }
        studentList.clear()
        studentList.addAll(XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(this), Student::class.java))
        initInfo()
    }


    private fun getCourses(student: Student?, year: String?, term: Int?) {
        if (student == null)
            return
        loadingDialog.show()
        ScheduleHelper.tomcatRetrofit
                .create(StudentService::class.java)
                .getCourses(student.username, year, term)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), GetCourseRT::class.java) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<GetCourseRT>() {
                    private lateinit var getCourseRT: GetCourseRT
                    override fun onComplete() {
                        val parentFile = XhuFileUtil.getCourseParentFile(this@ScheduleActivity)
                        if (!parentFile.exists())
                            parentFile.mkdirs()
                        val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
                        when (getCourseRT.rt) {
                            "0", "202" -> {
                                val newFile = File(parentFile, "$base64Name-$year-$term")
                                newFile.createNewFile()
                                XhuFileUtil.saveObjectToFile(getCourseRT.courses, newFile)
                                showCourses(student)
                            }
                            "401", "402" -> {
                                loadingDialog.dismiss()
                                Snackbar.make(coordinatorLayout, getString(R.string.hint_try_refresh_data_error, getCourseRT.msg), Snackbar.LENGTH_LONG)
                                        .show()
                            }
                            "405" -> {
                                login(student, year, term)
                            }
                            else -> {
                                loadingDialog.dismiss()
                                Snackbar.make(coordinatorLayout, getCourseRT.msg, Snackbar.LENGTH_LONG)
                                        .show()
                            }
                        }
                    }

                    override fun onNext(t: GetCourseRT) {
                        getCourseRT = t
                    }

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
                })
    }

    private fun showCourses(student: Student?) {
        if (student == null)
            return
        Observable.create<Boolean> { subscriber ->
            val parentFile = XhuFileUtil.getCourseParentFile(this)
            if (!parentFile.exists())
                parentFile.mkdirs()
            val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
            val savedFile = File(parentFile, "$base64Name-$year-$term")
            val courses = XhuFileUtil.getCoursesFromFile(this@ScheduleActivity, savedFile)
            if (courses.isEmpty()) {
                subscriber.onComplete()
                return@create
            }
            val tempArray = CourseUtil.getAllCourses(courses)
            weekList.clear()
            weekList.addAll(tempArray)
            subscriber.onComplete()
        }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Boolean>() {
                    override fun onNext(t: Boolean) {
                    }

                    override fun onComplete() {
                        formatView()
                        loadingDialog.dismiss()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loadingDialog.dismiss()
                    }
                })
    }

    private fun formatView() {
        var hasData = false
        for (i in 0 until weekList.size)
            (0 until weekList[i].size)
                    .filter { weekList[i][it].isNotEmpty() }
                    .forEach { hasData = true }
        if (!hasData)
            return
        val itemHeight = DensityUtil.dip2px(this, Settings.customTextHeight.toFloat())
        for (day in 0 until 7) {
            val layoutList = ArrayList<TableLayoutHelper>()
            val temp = resources.getIdentifier("table_schedule" + (day + 1), "id", "com.weilylab.xhuschedule")
            val linearLayout: LinearLayout = findViewById(temp)
            linearLayout.removeAllViews()
            for (time in 0 until 11) {
                val linkedList = weekList[time][day]
                if (linkedList.isEmpty()) {//如果这个位置没有课
                    if (isShowInLayout(layoutList, time))//如果格子被占用，直接继续循环
                        continue
                    val textView = LayoutInflater.from(this).inflate(R.layout.layout_text_view, null)
                    linearLayout.addView(textView)
                    val params = textView.layoutParams
                    params.height = itemHeight
                    textView.layoutParams = params
                    continue
                }
                //该位置有课
                //判断这个格子是否被占用
                if (isShowInLayout(layoutList, time)) {
                    var tableHelper = TableLayoutHelper()
                    for (i in 0 until layoutList.size) {
                        if (time in layoutList[i].start..layoutList[i].end) {
                            tableHelper = layoutList[i]
                            break
                        }
                    }
                    linkedList.forEach { course ->
                        val timeArray = course.time.split('-')
                        tableHelper.end = max(tableHelper.end, timeArray[1].toInt() - 1)
                        tableHelper.viewGroup.addView(getItemView(course, tableHelper.start))
                    }
                    val params = tableHelper.viewGroup.layoutParams
                    params.height = (tableHelper.end - tableHelper.start + 1) * itemHeight
                    tableHelper.viewGroup.layoutParams = params
                } else {//这个格子没有被占用
                    val view = LayoutInflater.from(this).inflate(R.layout.item_linear_layout, null)
                    val viewGroup: LinearLayout = view.findViewById(R.id.linearLayout)
                    var maxHeight = 0
                    linkedList.forEach { course ->
                        //循环确定这个格子的高度
                        val timeArray = course.time.split('-')
                        val courseTime = timeArray[1].toInt() - timeArray[0].toInt() + 1//计算这节课长度
                        maxHeight = max(maxHeight, courseTime * itemHeight)
                        viewGroup.addView(getItemView(course, time))
                    }
                    val tableHelper = TableLayoutHelper()
                    tableHelper.start = time
                    tableHelper.end = maxHeight / itemHeight + time - 1
                    tableHelper.viewGroup = viewGroup
                    layoutList.add(tableHelper)//将这个布局添加进list
                    linearLayout.addView(viewGroup)
                    val params = viewGroup.layoutParams
                    params.height = maxHeight
                    viewGroup.layoutParams = params
                }
            }
        }
    }

    private fun isShowInLayout(list: ArrayList<TableLayoutHelper>, itemIndex: Int): Boolean {
        list.forEach {
            if (itemIndex in it.start..it.end)
                return true
        }
        return false
    }

    private fun getItemView(course: Course, startTime: Int): View {
        val itemHeight = DensityUtil.dip2px(this, Settings.customTextHeight.toFloat())
        val itemView = View.inflate(this, R.layout.item_widget_table, null)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textViewName: TextView = itemView.findViewById(R.id.textView_name)
        val textViewTeacher: TextView = itemView.findViewById(R.id.textView_teacher)
        val textViewLocation: TextView = itemView.findViewById(R.id.textView_location)
        val textSize = Settings.customTextSize
        textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        textViewTeacher.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        textViewLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        textViewName.text = course.name
        textViewTeacher.text = course.teacher
        textViewLocation.text = course.location
        textViewName.setTextColor(Settings.customTableTextColor)
        textViewTeacher.setTextColor(Settings.customTableTextColor)
        textViewLocation.setTextColor(Settings.customTableTextColor)
        val color: Int = try {
            Color.parseColor('#' + Integer.toHexString(Settings.customTableOpacity) + course.color.substring(1))
        } catch (e: Exception) {
            Color.parseColor('#' + Integer.toHexString(Settings.customTableOpacity) + ScheduleHelper.getRandomColor())
        }
        val gradientDrawable = imageView.background as GradientDrawable
        when (course.type) {
            "-1" -> gradientDrawable.setColor(Color.RED)
            "not" -> {
                textViewName.setTextColor(Color.GRAY)
                textViewTeacher.setTextColor(Color.GRAY)
                textViewLocation.setTextColor(Color.GRAY)
                gradientDrawable.setColor(Color.parseColor("#9AEEEEEE"))
            }
            else -> gradientDrawable.setColor(color)
        }
        val timeArray = course.time.split('-')
        val height = (timeArray[1].toInt() - timeArray[0].toInt() + 1) * itemHeight
        val linearLayoutParams = LinearLayout.LayoutParams(0, height, 1F)
        linearLayoutParams.topMargin = (timeArray[0].toInt() - startTime - 1) * itemHeight
        itemView.layoutParams = linearLayoutParams
        return itemView
    }

    private fun login(student: Student, year: String?, term: Int?) {
        student.login(object : LoginListener {
            override fun error(rt: Int, e: Throwable) {
                loadingDialog.dismiss()
                Snackbar.make(coordinatorLayout, e.message!!, Snackbar.LENGTH_LONG)
                        .show()
            }

            override fun loginDone() {
                getCourses(student, year, term)
            }
        })
    }

    private fun initInfo() {
        val studentArray = Array(studentList.size, { i -> studentList[i].username })
        spinner_username.setItems(studentArray.toList())
        spinner_term.setItems(1, 2, 3)
        spinner_username.setOnItemSelectedListener { _, _, _, username ->
            setUsername(username.toString(), true)
        }
        spinner_year.setOnItemSelectedListener { _, _, _, year ->
            this.year = year.toString()
            showCourses(currentStudent)
        }
        spinner_term.setOnItemSelectedListener { _, _, _, term ->
            this.term = term as Int
            showCourses(currentStudent)
        }
        if (studentArray.size == 1) {
            spinner_username.selectedIndex = 0
            setUsername(studentArray[0], true)
        }
    }

    private fun setUsername(username: String?, isAutoSelect: Boolean) {
        val userList = ArrayList<Student>()
        val yearList = ArrayList<String>()
        //初始化入学年份
        Observable.create<Any> {
            userList.addAll(XhuFileUtil.getArrayFromFile(File(filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java))
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
                        spinner_year.setItems(yearList)
                        if (isAutoSelect) {
                            val term = CalendarUtil.getTermType()
                            spinner_year.selectedIndex = yearList.size - 1//自动选择最后一年
                            spinner_term.selectedIndex = term - 1//自动选择学期
                            year = yearList[yearList.size - 1]
                            this@ScheduleActivity.term = term
                        }
                        showCourses(currentStudent)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_schedule, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_sync -> {
                getCourses(currentStudent, year, term)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
