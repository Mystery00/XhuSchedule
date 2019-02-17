package com.weilylab.xhuschedule.ui.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.DialogShowCourseBinding
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.ui.ZoomOutPageTransformer
import com.weilylab.xhuschedule.ui.adapter.ShowCourseRecyclerViewAdapter
import com.weilylab.xhuschedule.ui.adapter.ViewPagerAdapter
import com.weilylab.xhuschedule.ui.fragment.ProfileFragment
import com.weilylab.xhuschedule.ui.fragment.TableFragment
import com.weilylab.xhuschedule.ui.fragment.TodayFragment
import com.weilylab.xhuschedule.utils.*
import com.weilylab.xhuschedule.utils.userDo.CourseUtil
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.zhuangfei.timetable.listener.IWeekView
import com.zhuangfei.timetable.model.Schedule
import com.zhuangfei.timetable.model.ScheduleSupport
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.content_bottom_navigation.*
import vip.mystery0.bottomTabView.BottomTabItem
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status.*
import vip.mystery0.tools.utils.DensityTools
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BottomNavigationActivity : XhuBaseActivity(R.layout.activity_bottom_navigation) {
	companion object {
		private const val ADD_ACCOUNT_CODE = 21
		private const val ACTION_NONE = 30
		private const val ACTION_INIT = 31
		private const val ACTION_REFRESH = 32
	}

	private val bottomNavigationViewModel: BottomNavigationViewModel by lazy {
		ViewModelProviders.of(this)[BottomNavigationViewModel::class.java]
	}
	private val viewPagerAdapter: ViewPagerAdapter by lazy { ViewPagerAdapter(supportFragmentManager) }
	private val dialog: Dialog by lazy {
		ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_init))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(this, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}
	private val courseList = ArrayList<Schedule>()
	private var animation: ObjectAnimator? = null
	private var arrowAnimation: ObjectAnimator? = null
	private var isShowWeekView = false
	private val showAdapter: ShowCourseRecyclerViewAdapter by lazy { ShowCourseRecyclerViewAdapter(this) }
	private lateinit var loadingAnimation: ObjectAnimator
	private var action = ACTION_NONE

	private val studentListObserver = Observer<PackageData<List<Student>>> {
		when (it.status) {
			Loading -> {
				if (action == ACTION_INIT)
					showDialog()
			}
			Content -> {
				try {
					if (getString(R.string.app_version_code).toInt() > ConfigurationUtil.updatedVersion)
						ConfigUtil.showUpdateLog(this)
				} catch (e: Exception) {
				}
				if (ConfigurationUtil.isEnableMultiUserMode) {
					val mainStudent = UserUtil.findMainStudent(it.data)
					if (mainStudent != null)
						BottomNavigationRepository.queryStudentInfo(bottomNavigationViewModel, mainStudent)
					else
						BottomNavigationRepository.queryStudentList(bottomNavigationViewModel)
					BottomNavigationRepository.queryCacheCoursesForManyStudent(bottomNavigationViewModel)
				} else {
					BottomNavigationRepository.queryStudentInfo(bottomNavigationViewModel)
					BottomNavigationRepository.queryCacheCourses(bottomNavigationViewModel)
				}
				BottomNavigationRepository.queryCurrentWeek(bottomNavigationViewModel)
				BottomNavigationRepository.queryFeedBack(bottomNavigationViewModel)
			}
			Empty -> {
				startActivityForResult(Intent(this, LoginActivity::class.java), ADD_ACCOUNT_CODE)
				hideDialog()
			}
			Error -> {
				Logs.wtfm("studentListObserver: ", it.error)
				toastMessage(it.error?.message, true)
				hideDialog()
			}
		}
	}

	private val courseListObserver = Observer<PackageData<List<Schedule>>> { packageData ->
		when (packageData.status) {
			Content -> {
				if (action == ACTION_REFRESH) {
					toastMessage(R.string.hint_course_sync_done)
					action = ACTION_NONE
				}
				cancelLoading()
				hideDialog()
				val nowString = CalendarUtil.getTodayDateString()
				if (nowString != ConfigurationUtil.lastUpdateDate) {
					if (ConfigurationUtil.isEnableMultiUserMode)
						BottomNavigationRepository.queryCoursesOnlineForManyStudent(bottomNavigationViewModel, false)
					else
						BottomNavigationRepository.queryCoursesOnline(bottomNavigationViewModel, false)
				}
			}
			Loading -> showLoading()
			Error -> {
				Logs.wtfm("courseListObserver: ", packageData.error)
				action = ACTION_NONE
				toastMessage(packageData.error?.message)
				cancelLoading()
				hideDialog()
			}
			Empty -> {
				if (action == ACTION_REFRESH) {
					toastMessage(R.string.hint_course_sync_done)
					action = ACTION_NONE
				}
				cancelLoading()
				hideDialog()
			}
		}
	}

	private val currentWeekObserver = Observer<PackageData<Int>> {
		when (it.status) {
			Content -> {
				val week = when {
					it.data!! < 1 -> 1
					it.data!! > 20 -> 20
					else -> it.data!!
				}
				weekView.curWeek(week).showView()
				bottomNavigationViewModel.week.value = week
				viewPagerAdapter.getItem(viewPager.currentItem).updateTitle()
			}
			Error -> {
				Logs.wtfm("currentWeekObserver: ", it.error)
				toastMessage(it.error?.message)
				cancelLoading()
				hideDialog()
			}
			Loading -> showLoading()
			Empty -> {
				cancelLoading()
				hideDialog()
			}
		}
	}

	private val weekObserver = Observer<Int> {
		viewPagerAdapter.getItem(viewPager.currentItem).updateTitle()
	}

	private val titleObserver = Observer<String> {
		titleTextView.text = it
	}

	private val showCourseObserver = Observer<List<Schedule>> {
		showAdapter.items.clear()
		val week = bottomNavigationViewModel.week.value ?: 0
		showAdapter.items.addAll(CourseUtil.filterShowCourse(it, week))
		showAdapter.notifyDataSetChanged()
		showPopupWindow()
	}

	override fun initView() {
		super.initView()
		titleTextView.text = title
		showBackground()
		initPopupWindow()
		viewPagerAdapter.addFragment(TodayFragment.newInstance())
		viewPagerAdapter.addFragment(TableFragment.newInstance())
		viewPagerAdapter.addFragment(ProfileFragment.newInstance())
		viewPager.offscreenPageLimit = 2
		viewPager.adapter = viewPagerAdapter
		if (ConfigurationUtil.enableViewPagerTransform)
			viewPager.setPageTransformer(true, ZoomOutPageTransformer())
		weekView.data(courseList)
				.curWeek(1)
				.callback(IWeekView.OnWeekItemClickedListener {
					bottomNavigationViewModel.week.value = it
					configWeekView(viewPager.currentItem)
					viewPagerAdapter.getItem(viewPager.currentItem).updateTitle()
				})
				.showView()
		bottomNavigationView.config { it.setGradientColors(intArrayOf(Color.parseColor("#0297fe"), Color.parseColor("#0fc8ff"))) }
				.setMenuList(arrayListOf(
						BottomTabItem(getString(R.string.nav_today), R.drawable.ic_today_selected, R.drawable.ic_today),
						BottomTabItem(getString(R.string.nav_week), R.drawable.ic_week_selected, R.drawable.ic_week),
						BottomTabItem(getString(R.string.nav_profile), R.drawable.ic_profile_selected, R.drawable.ic_profile)
				))
				.init()
	}

	private fun showBackground() {
		val path = ConfigurationUtil.customBackgroundImage
		if (path == "" || !File(path).exists()) {
			backgroundImageView.setImageResource(R.mipmap.bg1)
		} else {
			val options = RequestOptions()
					.signature(MediaStoreSignature("image/*", File(path).lastModified(), 0))
					.diskCacheStrategy(DiskCacheStrategy.NONE)
			Glide.with(this)
					.load(path)
					.apply(options)
					.into(backgroundImageView)
		}
	}

	override fun initData() {
		super.initData()
		initViewModel()
		viewPagerAdapter.getItem(0).updateTitle()
		configWeekView(0)
		BottomNavigationRepository.queryStudentList(bottomNavigationViewModel)
		BottomNavigationRepository.queryNotice(bottomNavigationViewModel, true)
	}

	private fun initViewModel() {
		bottomNavigationViewModel.studentList.observe(this, studentListObserver)
		bottomNavigationViewModel.currentWeek.observe(this, currentWeekObserver)
		bottomNavigationViewModel.courseList.observe(this, courseListObserver)
		bottomNavigationViewModel.week.observe(this, weekObserver)
		bottomNavigationViewModel.title.observe(this, titleObserver)
		bottomNavigationViewModel.showCourse.observe(this, showCourseObserver)
	}

	override fun monitor() {
		super.monitor()
		bottomNavigationView.linkViewPager(viewPager,{
			viewPagerAdapter.getItem(it).updateTitle()
			configWeekView(it)
		},false)
		appBarLayout.setOnClickListener { }
		arrowImageView.setOnClickListener {
			if (isShowWeekView)
				hideWeekView()
			else
				showWeekView()
		}
		titleTextView.setOnClickListener {
			if (isShowWeekView)
				hideWeekView()
			else
				showWeekView()
		}
		imageSync.setOnClickListener {
			action = ACTION_REFRESH
			if (ConfigurationUtil.isEnableMultiUserMode)
				BottomNavigationRepository.queryCoursesOnlineForManyStudent(bottomNavigationViewModel)
			else
				BottomNavigationRepository.queryCoursesOnline(bottomNavigationViewModel)
		}
	}

	override fun onResume() {
		super.onResume()
		if (LayoutRefreshConfigUtil.isChangeBackgroundImage) {
			showBackground()
			LayoutRefreshConfigUtil.isChangeBackgroundImage = false
		}
		if (LayoutRefreshConfigUtil.isRefreshNoticeDot) {
			BottomNavigationRepository.queryNotice(bottomNavigationViewModel, false)
			LayoutRefreshConfigUtil.isRefreshNoticeDot = false
		}
		if (LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity) {
			BottomNavigationRepository.queryStudentList(bottomNavigationViewModel)
			LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = false
		}
	}

	override fun onBackPressed() {
		if (ConfigUtil.isTwiceClick())
			super.onBackPressed()
		else
			toastMessage(R.string.hint_twice_press_exit)
	}

	private fun configWeekView(position: Int) {
		when (position) {
			0 -> {
				if (isShowWeekView) hideWeekView()
				titleTextView.isClickable = false
				arrowImageView.visibility = View.GONE
			}
			1 -> {
				titleTextView.isClickable = true
				arrowImageView.visibility = View.VISIBLE
			}
			2 -> {
				if (isShowWeekView) hideWeekView()
				titleTextView.isClickable = false
				arrowImageView.visibility = View.GONE
			}
		}
	}

	private fun showWeekView() {
		animation?.cancel()
		arrowAnimation?.cancel()
		animation = ObjectAnimator.ofFloat(weekView, "translationY", 0F, DensityTools.dp2px(71F).toFloat())
		arrowAnimation = ObjectAnimator.ofFloat(arrowImageView, "rotation", 0F, 180F)
		animation!!.start()
		arrowAnimation!!.start()
		isShowWeekView = true
	}

	private fun hideWeekView() {
		animation?.cancel()
		arrowAnimation?.cancel()
		animation = ObjectAnimator.ofFloat(weekView, "translationY", DensityTools.dp2px(71F).toFloat(), 0F)
		arrowAnimation = ObjectAnimator.ofFloat(arrowImageView, "rotation", 180F, 0F)
		animation!!.start()
		arrowAnimation!!.start()
		isShowWeekView = false
	}

	private fun showDialog() {
		if (!dialog.isShowing)
			dialog.show()
	}

	private fun hideDialog() {
		if (dialog.isShowing)
			dialog.dismiss()
	}

	private fun showLoading() {
		if (!LayoutRefreshConfigUtil.isRefreshDone)
			return
		if (!::loadingAnimation.isInitialized) {
			Observable.create<Boolean> {
				while (!LayoutRefreshConfigUtil.isRefreshDone) {
					it.onNext(true)
					Thread.sleep(1000)
				}
				it.onComplete()
			}
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : io.reactivex.Observer<Boolean> {
						override fun onComplete() {
							if (bottomNavigationViewModel.courseList.value != null && bottomNavigationViewModel.courseList.value!!.data != null) {
								courseList.clear()
								courseList.addAll(bottomNavigationViewModel.courseList.value!!.data!!)
								if (bottomNavigationViewModel.startDateTime.value != null && bottomNavigationViewModel.startDateTime.value!!.data != null) {
									val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
									bottomNavigationViewModel.week.value = ScheduleSupport.timeTransfrom(simpleDateFormat.format(bottomNavigationViewModel.startDateTime.value!!.data!!.time))
								}
								weekView.data(courseList).showView()
							}
						}

						override fun onSubscribe(d: Disposable) {
							LayoutRefreshConfigUtil.isRefreshDone = false
						}

						override fun onNext(t: Boolean) {
							ObjectAnimator.ofFloat(imageSync, "rotation", 0F, 360F)
									.setDuration(1000)
									.start()
						}

						override fun onError(e: Throwable) {
							Logs.wtf("onError: ", e)
						}
					})
		}
	}

	private fun cancelLoading() {
		LayoutRefreshConfigUtil.isRefreshDone = true
	}

	private lateinit var popupWindow: PopupWindow
	private lateinit var dialogShowCourseBinding: DialogShowCourseBinding
	private var distance = 40

	private fun initPopupWindow() {
		dialogShowCourseBinding = DialogShowCourseBinding.inflate(LayoutInflater.from(this))
		val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
		dialogShowCourseBinding.recyclerView.layoutManager = linearLayoutManager
		dialogShowCourseBinding.recyclerView.adapter = showAdapter
		dialogShowCourseBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				val horizontalOffset = recyclerView.computeHorizontalScrollOffset().toFloat() / DensityTools.getScreenWidth().toFloat()
				val params = dialogShowCourseBinding.point.layoutParams as ConstraintLayout.LayoutParams
				super.onScrolled(recyclerView, dx, dy)
				params.leftMargin = Math.round(distance * horizontalOffset)
				dialogShowCourseBinding.point.layoutParams = params
			}
		})
		PagerSnapHelper().attachToRecyclerView(dialogShowCourseBinding.recyclerView)
		val viewSize = DensityTools.getScreenWidth() - DensityTools.dp2px(96F)
		popupWindow = PopupWindow(dialogShowCourseBinding.root, ViewGroup.LayoutParams.MATCH_PARENT, viewSize)
		popupWindow.isOutsideTouchable = true
		popupWindow.isFocusable = true
		popupWindow.animationStyle = R.style.ShowCourseAnimation
		popupWindow.setOnDismissListener {
			AnimationUtil.setWindowAlpha(this, 0.5F, 1F, 200)
		}
		popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
	}

	private fun showPopupWindow() {
		generatePoint()
		val params = dialogShowCourseBinding.point.layoutParams as ConstraintLayout.LayoutParams
		params.leftMargin = 0
		dialogShowCourseBinding.point.layoutParams = params
		dialogShowCourseBinding.recyclerView.scrollToPosition(0)
		AnimationUtil.setWindowAlpha(this, 1F, 0.5F, 200)
		if (!isFinishing && !isDestroyed)
			popupWindow.showAtLocation(weekView, Gravity.CENTER, 0, 0)
	}

	private fun generatePoint() {
		dialogShowCourseBinding.pointLayout.removeAllViews()
		val grayPointDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_point, null)!!
		grayPointDrawable.setBounds(0, 0, 20, 20)
		grayPointDrawable.setTint(Color.LTGRAY)
		for (i in 0 until showAdapter.items.size) {
			val view = View(applicationContext)
			view.background = grayPointDrawable
			val params = LinearLayout.LayoutParams(20, 20)
			if (i != 0)
				params.leftMargin = 20
			view.layoutParams = params
			dialogShowCourseBinding.pointLayout.addView(view)
		}
		val pointParams = dialogShowCourseBinding.point.layoutParams
		pointParams.height = 20
		pointParams.width = 20
		dialogShowCourseBinding.point.layoutParams = pointParams
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			ADD_ACCOUNT_CODE -> {
				if (resultCode == Activity.RESULT_OK) {
					BottomNavigationRepository.queryStudentList(bottomNavigationViewModel)
				} else {
					finish()
				}
			}
		}
	}
}
