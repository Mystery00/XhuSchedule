package com.weilylab.xhuschedule.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ViewPagerAdapter
import com.weilylab.xhuschedule.classes.ContentRT
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.LoginRT
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.fragment.TableFragment
import com.weilylab.xhuschedule.fragment.TodayFragment
import com.weilylab.xhuschedule.interfaces.RTResponse
import com.weilylab.xhuschedule.util.*
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.io.InputStreamReader
import java.net.UnknownHostException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private val TAG = "MainActivity"
    }

    private lateinit var loadingDialog: ZLoadingDialog
    private var isTryRefreshData = false
    private var weekList = ArrayList<Course?>()
    private var allList = ArrayList<Course?>()
    private val todayList = ArrayList<Course>()
    private val todayFragment = TodayFragment.newInstance(todayList)
    private val weekFragment = TableFragment.newInstance(weekList, true)
    private val allFragment = TableFragment.newInstance(allList, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadingDialog = ZLoadingDialog(this)
                .setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
                .setHintText(getString(R.string.hint_dialog_update_cache))
                .setHintTextSize(16F)
                .setCancelable(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            loadingDialog.setLoadingColor(resources.getColor(R.color.colorAccent, null))
            loadingDialog.setHintTextColor(resources.getColor(R.color.colorAccent, null))
        } else {
            loadingDialog.setLoadingColor(Color.parseColor("#4053ff"))
            loadingDialog.setHintTextColor(Color.parseColor("#4053ff"))
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        initView()
        if (ScheduleHelper.isFromLogin)
            updateData()
        else
            updateView()
        if (Settings.isFirstRun)
            showcase()
        showUpdateLog()
    }

    private fun showUpdateLog() {
        val sharedPreference = getSharedPreferences("update", Context.MODE_PRIVATE)
        if (sharedPreference.getInt("updateVersion", 0) < getString(R.string.app_version_code).toInt()) {
            var message = ""
            resources.getStringArray(R.array.update_list)
                    .forEach { message += it + '\n' }
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.title_update_log, getString(R.string.app_version_name) + '-' + getString(R.string.app_version_code)))
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .setOnDismissListener {
                        sharedPreference.edit().putInt("updateVersion", getString(R.string.app_version_code).toInt()).apply()
                    }
                    .show()
        }
    }

    override fun onResume() {
        super.onResume()
        val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
        if (Settings.customBackgroundImg != "")
            Glide.with(this).load(Settings.customBackgroundImg).apply(options).into(background)
        if (Settings.customHeaderImg != "")
            Glide.with(this).load(Settings.customHeaderImg).apply(options).into(nav_view.getHeaderView(0).findViewById(R.id.background))
        if (ScheduleHelper.isUIChange)
            updateView()
        ScheduleHelper.isUIChange = false
    }

    private fun initView() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(todayFragment)
        viewPagerAdapter.addFragment(weekFragment)
        viewPagerAdapter.addFragment(allFragment)
        viewpager.offscreenPageLimit = 2
        viewpager.adapter = viewPagerAdapter
        val userIMG: CircleImageView = nav_view.getHeaderView(0).findViewById(R.id.userIMG)
        val nickName: TextView = nav_view.getHeaderView(0).findViewById(R.id.nickName)
        userIMG.setImageResource(R.mipmap.ic_launcher)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_today -> viewpager.currentItem = 0
                R.id.bottom_nav_week -> viewpager.currentItem = 1
                R.id.bottom_nav_all -> viewpager.currentItem = 2
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
                toolbar.title = resources.getStringArray(R.array.title_array)[position]
            }
        })
        toolbar.title = resources.getStringArray(R.array.title_array)[0]

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
        swipeRefreshLayout.setProgressViewEndTarget(true, 200)
        swipeRefreshLayout.setDistanceToTriggerSync(100)
        swipeRefreshLayout.setOnRefreshListener {
            updateView()
        }

        toolbar.inflateMenu(R.menu.menu_activity_main)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                android.R.id.home -> {
                    drawer_layout.openDrawer(GravityCompat.START)
                    true
                }
                R.id.action_sync -> {
                    updateData()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
    }

    fun updateView() {
        Observable.create<HashMap<String, ArrayList<Course?>>> { subscriber ->
            val parentFile = File(filesDir.absolutePath + File.separator + "caches/")
            if (!parentFile.exists())
                parentFile.mkdirs()
            val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
            val studentNumber = sharedPreference.getString("username", "0")
            val studentName = sharedPreference.getString("studentName", "0")
            if (studentNumber == "0" || studentName == "0") {
                ScheduleHelper.isLogin = false
                subscriber.onComplete()
                return@create
            }
            ScheduleHelper.isLogin = true
            ScheduleHelper.studentName = studentName
            ScheduleHelper.studentNumber = studentNumber
            val base64Name = FileUtil.filterString(Base64.encodeToString(studentNumber.toByteArray(), Base64.DEFAULT))
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
            val courses = FileUtil.getCoursesFromFile(this@MainActivity, oldFile)
            if (courses.isEmpty()) {
                ScheduleHelper.isCookieAvailable = false
                subscriber.onComplete()
                return@create
            }
            ScheduleHelper.isCookieAvailable = true
            val allArray = CourseUtil.formatCourses(FileUtil.getCoursesFromFile(this@MainActivity, oldFile))
            allList.clear()
            allList.addAll(allArray)
            val weekArray = CourseUtil.getWeekCourses(FileUtil.getCoursesFromFile(this@MainActivity, oldFile))
            weekList.clear()
            weekList.addAll(weekArray)
            val todayArray = CourseUtil.getTodayCourses(FileUtil.getCoursesFromFile(this@MainActivity, oldFile))
            todayList.clear()
            todayList.addAll(todayArray)
            subscriber.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<HashMap<String, ArrayList<Course?>>> {
                    override fun onSubscribe(d: Disposable) {
                        swipeRefreshLayout.isRefreshing = true
                    }

                    override fun onComplete() {
                        swipeRefreshLayout.isRefreshing = false
                        Logs.i(TAG, "onComplete: ")

                        if (!ScheduleHelper.isLogin) {
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            finish()
                            return
                        }
                        val group = nav_view.menu.findItem(R.id.nav_group).subMenu
                        group.clear()
                        group.add(ScheduleHelper.studentName + "(" + ScheduleHelper.studentNumber + ")")
                        if (ScheduleHelper.isCookieAvailable) {
                            when (todayList.size) {
                                0 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_very_satisfied)
                                1 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_very_satisfied)
                                2 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_satisfied)
                                3 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_neutral)
                                4 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_dissatisfied)
                                else -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_very_dissatisfied)
                            }
                            weekFragment.refreshData()
                            allFragment.refreshData()
                            todayFragment.refreshData()
                        } else
                            updateData()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        swipeRefreshLayout.isRefreshing = false
                    }

                    override fun onNext(map: HashMap<String, ArrayList<Course?>>) {
                        Logs.i(TAG, "onNext: ")
                    }
                })
    }

    private fun updateData() {
        val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
        val username = sharedPreference.getString("username", "0")
        val password = sharedPreference.getString("password", "0")
        if (username == "0" || password == "0") {
            ScheduleHelper.isLogin = false
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        val parentFile = File(filesDir.absolutePath + File.separator + "caches/")
        val base64Name = FileUtil.filterString(Base64.encodeToString(username.toByteArray(), Base64.DEFAULT))
        var isDataNew = false
        ScheduleHelper.tomcatRetrofit
                .create(RTResponse::class.java)
                .getCourses(username)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), ContentRT::class.java) })
                .subscribeOn(Schedulers.io())
                .doOnNext { contentRT ->
                    if (contentRT.rt == "1") {
                        val newFile = File(parentFile, base64Name + ".temp")
                        newFile.createNewFile()
                        FileUtil.saveObjectToFile(contentRT.courses, newFile)
                        val newMD5 = FileUtil.getMD5(newFile)
                        val oldFile = File(parentFile, base64Name)
                        var oldMD5 = ""
                        if (oldFile.exists())
                            oldMD5 = FileUtil.getMD5(oldFile)!!
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
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ContentRT> {
                    private var contentRT: ContentRT? = null
                    override fun onError(e: Throwable) {
                        loadingDialog.dismiss()
                        if (e is UnknownHostException)
                            Snackbar.make(coordinatorLayout, R.string.error_network, Snackbar.LENGTH_SHORT)
                                    .show()
                        else
                            Snackbar.make(coordinatorLayout, "请求出错：" + e.message, Snackbar.LENGTH_SHORT)
                                    .show()
                    }

                    override fun onComplete() {
                        loadingDialog.dismiss()
                        if (isTryRefreshData && contentRT?.rt == "6") {
                            ScheduleHelper.isLogin = false
                            login(username, password)
                            return
                        }
                        if (contentRT?.rt != "1") {
                            Snackbar.make(coordinatorLayout, R.string.hint_invalid_cookie, Snackbar.LENGTH_LONG)
                                    .setAction(android.R.string.ok) {
                                        ScheduleHelper.isLogin = false
                                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                    .show()
                            return
                        }
                        if (isDataNew)
                            Snackbar.make(coordinatorLayout, R.string.hint_update_data_new, Snackbar.LENGTH_SHORT).show()
                        else
                            Snackbar.make(coordinatorLayout, R.string.hint_update_data, Snackbar.LENGTH_SHORT).show()
                        updateView()
                    }

                    override fun onSubscribe(d: Disposable) {
                        loadingDialog.show()
                    }

                    override fun onNext(t: ContentRT) {
                        contentRT = t
                    }
                })
    }

    private fun login(username: String, password: String) {
        isTryRefreshData = true
        val student = Student()
        student.username = username
        student.password = password
        student.login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<LoginRT> {
                    private var loginRT: LoginRT? = null
                    override fun onError(e: Throwable) {
                        loadingDialog.dismiss()
                        e.printStackTrace()
                        if (e is UnknownHostException)
                            Toast.makeText(this@MainActivity, R.string.error_network, Toast.LENGTH_SHORT)
                                    .show()
                        else
                            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT)
                                    .show()
                    }

                    override fun onSubscribe(d: Disposable) {
                        loadingDialog.show()
                    }

                    override fun onComplete() {
                        loadingDialog.dismiss()
                        when (loginRT?.rt) {
                            "0" -> {
                                ScheduleHelper.isLogin = false
                                Snackbar.make(coordinatorLayout, getString(R.string.hint_try_refresh_data_error, getString(R.string.error_timeout)), Snackbar.LENGTH_LONG)
                                        .setAction(android.R.string.ok) {
                                            ScheduleHelper.isLogin = false
                                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                            finish()
                                        }
                                        .show()
                            }
                            "1" -> {
                                ScheduleHelper.isLogin = true
                                updateData()
                            }
                            "2" -> {
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

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_logout -> {
                val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
                sharedPreference.edit()
                        .remove("username")
                        .remove("password")
                        .remove("studentName")
                        .apply()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showcase() {
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        TapTargetSequence(this)
                .targets(
                        TapTarget.forView(bottomNavigationView.findViewById(R.id.bottom_nav_today), getString(R.string.showcase_today)),
                        TapTarget.forView(bottomNavigationView.findViewById(R.id.bottom_nav_week), getString(R.string.showcase_week)),
                        TapTarget.forView(bottomNavigationView.findViewById(R.id.bottom_nav_all), getString(R.string.showcase_all)),
                        TapTarget.forToolbarMenuItem(toolbar, R.id.action_sync, getString(R.string.showcase_sync)),
                        TapTarget.forBounds(Rect(
                                (size.x / 2) - 100,
                                200 + resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android")),
                                (size.x / 2) + 100,
                                400 + resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
                        ), getString(R.string.showcase_swipe_refresh)).transparentTarget(true))
                .continueOnCancel(true)
                .listener(object : TapTargetSequence.Listener {
                    override fun onSequenceCanceled(lastTarget: TapTarget?) {
                    }

                    override fun onSequenceFinish() {
                        Logs.i(TAG, "onSequenceFinish: ")
                        swipeRefreshLayout.isRefreshing = false
                        Settings.isFirstRun = false
                    }

                    override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                        Logs.i(TAG, "onSequenceStep: ")
                        swipeRefreshLayout.isRefreshing = true
                    }
                })
                .start()
    }
}
