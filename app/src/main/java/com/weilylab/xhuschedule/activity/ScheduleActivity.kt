package com.weilylab.xhuschedule.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.Profile
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.classes.TableLayoutHelper
import com.weilylab.xhuschedule.classes.rt.CourseRT
import com.weilylab.xhuschedule.interfaces.StudentService
import com.weilylab.xhuschedule.listener.LoginListener
import com.weilylab.xhuschedule.listener.ProfileListener
import com.weilylab.xhuschedule.util.*
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.content_schedule.*
import java.io.File
import java.io.InputStreamReader
import java.util.*
import kotlin.math.max

class ScheduleActivity : AppCompatActivity() {

    private lateinit var initDialog: Dialog
    private lateinit var loadingDialog: Dialog
    private val studentList = ArrayList<Student>()
    private var weekList = ArrayList<ArrayList<ArrayList<Course>>>()
    private var year = ""
    private var term = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
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
        action_back.setOnClickListener {
            finish()
        }
        action_sync.setOnClickListener {
            getCourses(studentList[spinner_student.selectedItemPosition], year, term)
        }
    }

    private fun initProfile(student: Student) {
        initDialog.show()
        showCourses(student)
        if (student.profile != null) {
            try {
                val start = student.profile!!.grade.toInt()//进校年份
                val calendar = Calendar.getInstance()
                val end = when (calendar.get(Calendar.MONTH) + 1) {
                    in 1 until 3 -> calendar.get(Calendar.YEAR) - 1
                    in 3 until 9 -> calendar.get(Calendar.YEAR)
                    in 9 until 13 -> calendar.get(Calendar.YEAR) + 1
                    else -> {
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

    private fun getCourses(student: Student, year: String?, term: Int?) {
        loadingDialog.show()
        ScheduleHelper.tomcatRetrofit
                .create(StudentService::class.java)
                .getCourses(student.username, year, term)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), CourseRT::class.java) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<CourseRT>() {
                    private lateinit var contentRT: CourseRT
                    override fun onComplete() {
                        val parentFile = File(filesDir.absolutePath + File.separator + "courses/")
                        if (!parentFile.exists())
                            parentFile.mkdirs()
                        val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
                        when (contentRT.rt) {
                            "1", "5" -> {
                                val newFile = File(parentFile, "$base64Name-$year-$term")
                                newFile.createNewFile()
                                XhuFileUtil.saveObjectToFile(contentRT.courses, newFile)
                                showCourses(student)
                            }
                            "2" -> {
                                loadingDialog.dismiss()
                                Snackbar.make(coordinatorLayout, getString(R.string.hint_try_refresh_data_error, getString(R.string.error_invalid_username)), Snackbar.LENGTH_LONG)
                                        .show()
                            }
                            "3" -> {
                                loadingDialog.dismiss()
                                Snackbar.make(coordinatorLayout, getString(R.string.hint_try_refresh_data_error, getString(R.string.error_invalid_password)), Snackbar.LENGTH_LONG)
                                        .show()
                            }
                            else -> {
                                login(student, year, term)
                            }
                        }
                    }

                    override fun onNext(t: CourseRT) {
                        contentRT = t
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    private fun showCourses(student: Student) {
        Observable.create<Boolean> { subscriber ->
            val parentFile = File(filesDir.absolutePath + File.separator + "courses/")
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
                            0
                        }
                    }
                    val array = Array(end - start, { i -> (start + i).toString() + '-' + (start + i + 1).toString() })
                    val arrayAdapter = ArrayAdapter<String>(this@ScheduleActivity, android.R.layout.simple_spinner_item, array)
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner_year.adapter = arrayAdapter
                    spinner_year.setSelection(array.size - 1)
                    initDialog.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@ScheduleActivity, "数据解析错误，无法使用，请联系开发者！", Toast.LENGTH_LONG)
                            .show()
                }
            }
        })
    }

    private fun login(student: Student, year: String?, term: Int?) {
        student.login(this, object : LoginListener {
            override fun error(rt: Int, e: Throwable) {
                loadingDialog.dismiss()
                Snackbar.make(coordinatorLayout, e.message!!, Snackbar.LENGTH_LONG)
                        .show()
            }

            override fun loginDone(name: String) {
                getCourses(student, year, term)
            }

            override fun doInThread() {
            }
        })
    }
}