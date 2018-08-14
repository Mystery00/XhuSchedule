package com.weilylab.xhuschedule.ui.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.animation.Animation
import android.widget.PopupWindow
import android.widget.Toast
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
import com.weilylab.xhuschedule.ui.adapter.ShowCourseRecyclerViewAdapter
import com.weilylab.xhuschedule.ui.adapter.ViewPagerAdapter
import com.weilylab.xhuschedule.ui.fragment.ProfileFragment
import com.weilylab.xhuschedule.ui.fragment.TableFragment
import com.weilylab.xhuschedule.ui.fragment.TodayFragment
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.utils.UserUtil
import com.weilylab.xhuschedule.utils.layoutManager.SkidRightLayoutManager
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.viewModel.BottomNavigationViewModel
import com.zhuangfei.timetable.listener.IWeekView
import com.zhuangfei.timetable.model.Schedule
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.content_bottom_navigation.*
import vip.mystery0.bottomTabView.BottomTabItem
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.DensityTools
import java.io.File
import java.util.ArrayList

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
				Toast.makeText(this, it.error?.message, Toast.LENGTH_LONG)
						.show()
				hideDialog()
			}
		}
	}

	private val courseListObserver = Observer<PackageData<List<Schedule>>> {
		when (it.status) {
			Content -> {
				courseList.clear()
				courseList.addAll(it.data!!)
				weekView.showView()
				hideDialog()
				cancelLoading()
			}
			Loading -> showLoading()
			Error -> {
				toastMessage(it.error?.message)
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

	private val titleObserver = Observer<String> {
		titleTextView.text = it
	}

	private val showCourseObserver = Observer<List<Schedule>> {
		showAdapter.items.clear()
		showAdapter.items.addAll(it)
		showAdapter.notifyDataSetChanged()
		showPopupWindow()
	}

	override fun initView() {
		super.initView()
		titleTextView.text = title
		showBackground()
		initDialog()
		initPopupWindow()
		viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
		viewPagerAdapter.addFragment(TodayFragment.newInstance())
		viewPagerAdapter.addFragment(TableFragment.newInstance())
		viewPagerAdapter.addFragment(ProfileFragment.newInstance())
		viewPager.offscreenPageLimit = 2
		viewPager.adapter = viewPagerAdapter
		weekView.data(courseList)!!
				.curWeek(1)
				.callback(IWeekView.OnWeekItemClickedListener {
					bottomNavigationViewModel.week.value = it
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
	}

	private fun initViewModel() {
		bottomNavigationViewModel = ViewModelProviders.of(this).get(BottomNavigationViewModel::class.java)
		bottomNavigationViewModel.studentList.observe(this, studentListObserver)
		bottomNavigationViewModel.currentWeek.observe(this, currentWeekObserver)
		bottomNavigationViewModel.courseList.observe(this, courseListObserver)
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
		appBarLayout.setOnClickListener { Logs.i("monitor: ") }
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
			BottomNavigationRepository.queryNotice(bottomNavigationViewModel)
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
			Toast.makeText(this, R.string.hint_twice_press_exit, Toast.LENGTH_SHORT)
					.show()
	}

	private fun configWeekView(position: Int) {
		when (position) {
			0 -> {
				if (isShowWeekView) hideWeekView()
				titleTextView.isClickable = false
			}
			1 -> titleTextView.isClickable = true
			2 -> {
				if (isShowWeekView) hideWeekView()
				titleTextView.isClickable = false
			}
		}
	}

	private fun showWeekView() {
		animation?.cancel()
		animation = ObjectAnimator.ofFloat(weekView, "translationY", 0F, DensityTools.dp2px(this, 72F).toFloat())
		animation!!.start()
		isShowWeekView = true
	}

	private fun hideWeekView() {
		animation?.cancel()
		animation = ObjectAnimator.ofFloat(weekView, "translationY", DensityTools.dp2px(this, 72F).toFloat(), 0F)
		animation!!.start()
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
		if (!::loadingAnimation.isInitialized)
			loadingAnimation = ObjectAnimator.ofFloat(imageSync, "rotation", 0F, 360F)
					.setDuration(1000)
		loadingAnimation.repeatCount = Animation.INFINITE
		loadingAnimation.repeatMode = ValueAnimator.RESTART
		loadingAnimation.start()
	}

	private fun cancelLoading() {
		if (!::loadingAnimation.isInitialized)
			return
		loadingAnimation.repeatCount = 0
		if (action == ACTION_REFRESH) {
			toastMessage("信息同步完成！")
			action = ACTION_NONE
		}
	}

	private lateinit var popupWindow: PopupWindow
	private lateinit var recyclerView: RecyclerView

	private fun initPopupWindow() {
		recyclerView = RecyclerView(this)
		recyclerView.layoutManager = SkidRightLayoutManager(1.5f, 0.85f)
//		recyclerView.layoutManager =SkidRightLayoutManager (this, 2)
		showAdapter = ShowCourseRecyclerViewAdapter(this)
		recyclerView.adapter = showAdapter
		popupWindow = PopupWindow(recyclerView, DensityTools.getScreenWidth(this), DensityTools.dp2px(this, 480F))
		popupWindow.isOutsideTouchable = true
		popupWindow.isFocusable = true
		popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
	}

	private fun showPopupWindow() {
		popupWindow.showAtLocation(weekView, Gravity.NO_GRAVITY, DensityTools.getScreenWidth(this) - popupWindow.width, DensityTools.getScreenHeight(this) / 2 - popupWindow.height / 2)
		val week = bottomNavigationViewModel.week.value
		var position = 0
		showAdapter.items.forEachIndexed { index, it ->
			if (it.weekList.contains(week))
				position = index
		}
		recyclerView.scrollToPosition(position)
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
