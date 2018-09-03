package com.weilylab.xhuschedule.ui.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.ui.ZoomOutPageTransformer
import com.weilylab.xhuschedule.ui.adapter.ShowCourseRecyclerViewAdapter
import com.weilylab.xhuschedule.ui.adapter.ViewPagerAdapter
import com.weilylab.xhuschedule.ui.fragment.ProfileFragment
import com.weilylab.xhuschedule.ui.fragment.TableFragment
import com.weilylab.xhuschedule.ui.fragment.TodayFragment
import com.weilylab.xhuschedule.utils.*
import com.weilylab.xhuschedule.utils.layoutManager.EchelonLayoutManager
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.viewModel.BottomNavigationViewModel
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

	private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
	private lateinit var viewPagerAdapter: ViewPagerAdapter
	private lateinit var dialog: Dialog
	private val courseList = ArrayList<Schedule>()
	private var animation: ObjectAnimator? = null
	private var arrowAnimation: ObjectAnimator? = null
	private var isShowWeekView = false
	private lateinit var showAdapter: ShowCourseRecyclerViewAdapter
	private lateinit var loadingAnimation: ObjectAnimator
	private var action = ACTION_NONE

	private val studentListObserver = Observer<PackageData<List<Student>>> {
		when (it.status) {
			Loading -> {
				if (action == ACTION_INIT)
					showDialog()
			}
			Content -> {
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
			}
			Empty -> {
				startActivityForResult(Intent(this, LoginActivity::class.java), ADD_ACCOUNT_CODE)
				hideDialog()
			}
			Error -> {
				toastMessage(it.error?.message, true)
				hideDialog()
			}
		}
	}

	private val courseListObserver = Observer<PackageData<List<Schedule>>> { packageData ->
		when (packageData.status) {
			Content -> {
				cancelLoading()
				hideDialog()
			}
			Loading -> showLoading()
			Error -> {
				toastMessage(packageData.error?.message)
				cancelLoading()
				hideDialog()
			}
			Empty -> {
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
					it.data > 20 -> 20
					else -> it.data
				}
				weekView.curWeek(week).showView()
				bottomNavigationViewModel.week.value = week
				viewPagerAdapter.getItem(viewPager.currentItem).updateTitle()
			}
			Error -> {
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
		try {
			if (getString(R.string.app_version_code).toInt() > ConfigurationUtil.updatedVersion)
				ConfigUtil.showUpdateLog(this)
		} catch (e: Exception) {
		}
		initDialog()
		initPopupWindow()
		viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
		viewPagerAdapter.addFragment(TodayFragment.newInstance())
		viewPagerAdapter.addFragment(TableFragment.newInstance())
		viewPagerAdapter.addFragment(ProfileFragment.newInstance())
		viewPager.offscreenPageLimit = 2
		viewPager.setPageTransformer(true, ZoomOutPageTransformer())
		viewPager.adapter = viewPagerAdapter
		weekView.data(courseList)
				.curWeek(1)
				.callback(IWeekView.OnWeekItemClickedListener {
					bottomNavigationViewModel.week.value = it
					configWeekView(viewPager.currentItem)
					viewPagerAdapter.getItem(viewPager.currentItem).updateTitle()
				})
				.showView()
		bottomNavigationView.config
				.setGradientColors(intArrayOf(Color.parseColor("#0297fe"), Color.parseColor("#0fc8ff")))
		bottomNavigationView.setMenuList(arrayListOf(
				BottomTabItem(getString(R.string.nav_today), R.drawable.ic_today_selected, R.drawable.ic_today),
				BottomTabItem(getString(R.string.nav_week), R.drawable.ic_week_selected, R.drawable.ic_week),
				BottomTabItem(getString(R.string.nav_profile), R.drawable.ic_profile_selected, R.drawable.ic_profile)
		))
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

	private fun initDialog() {
		dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_init))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
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
		bottomNavigationViewModel = ViewModelProviders.of(this).get(BottomNavigationViewModel::class.java)
		bottomNavigationViewModel.studentList.observe(this, studentListObserver)
		bottomNavigationViewModel.currentWeek.observe(this, currentWeekObserver)
		bottomNavigationViewModel.courseList.observe(this, courseListObserver)
		bottomNavigationViewModel.week.observe(this, weekObserver)
		bottomNavigationViewModel.title.observe(this, titleObserver)
		bottomNavigationViewModel.showCourse.observe(this, showCourseObserver)
	}

	override fun monitor() {
		super.monitor()
		bottomNavigationView.setOnItemSelectedListener {
			viewPager.setCurrentItem(bottomNavigationView.indexOf(it), true)
		}
		viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
			override fun onPageScrollStateChanged(state: Int) {
			}

			override fun onPageScrolled(position: Int, positionOffset: Float,
										positionOffsetPixels: Int) {
			}

			override fun onPageSelected(position: Int) {
				bottomNavigationView.setCheckedItem(position)
				viewPagerAdapter.getItem(position).updateTitle()
				configWeekView(position)
			}
		})
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

	override fun onRestart() {
		super.onRestart()
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
		animation = ObjectAnimator.ofFloat(weekView, "translationY", 0F, DensityTools.dp2px(this, 71F).toFloat())
		arrowAnimation = ObjectAnimator.ofFloat(arrowImageView, "rotation", 0F, 180F)
		animation!!.start()
		arrowAnimation!!.start()
		isShowWeekView = true
	}

	private fun hideWeekView() {
		animation?.cancel()
		arrowAnimation?.cancel()
		animation = ObjectAnimator.ofFloat(weekView, "translationY", DensityTools.dp2px(this, 71F).toFloat(), 0F)
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
							if (action == ACTION_REFRESH) {
								toastMessage(R.string.hint_course_sync_done)
								action = ACTION_NONE
							}
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
	private lateinit var recyclerView: RecyclerView

	private fun initPopupWindow() {
		recyclerView = RecyclerView(this)
//		recyclerView.layoutManager = SkidRightLayoutManager(1.5f, 0.85f)
		recyclerView.layoutManager = EchelonLayoutManager(this)
		showAdapter = ShowCourseRecyclerViewAdapter(this)
		recyclerView.adapter = showAdapter
		popupWindow = PopupWindow(recyclerView, DensityTools.getScreenWidth(this), DensityTools.dp2px(this, 240F))
		popupWindow.isOutsideTouchable = true
		popupWindow.isFocusable = true
		popupWindow.animationStyle = R.style.ShowCourseAnimation
		popupWindow.setOnDismissListener {
			AnimationUtil.setWindowAlpha(this, 1F)
		}
		popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
	}

	private fun showPopupWindow() {
		AnimationUtil.setWindowAlpha(this, 0.6F)
		popupWindow.showAtLocation(weekView, Gravity.BOTTOM, 0, 0)
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
