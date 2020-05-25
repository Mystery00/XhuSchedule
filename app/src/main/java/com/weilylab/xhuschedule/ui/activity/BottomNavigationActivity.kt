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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.api.load
import coil.request.CachePolicy
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
import com.weilylab.xhuschedule.ui.fragment.settings.AccountSettingsFragment.Companion.ADD_ACCOUNT_CODE
import com.weilylab.xhuschedule.utils.*
import com.weilylab.xhuschedule.utils.userDo.CourseUtil
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.zhuangfei.timetable.listener.IWeekView
import com.zhuangfei.timetable.model.Schedule
import com.zhuangfei.timetable.model.ScheduleSupport
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.content_bottom_navigation.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.bottomTabView.BottomTabItem
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver
import vip.mystery0.rx.PackageDataObserver
import vip.mystery0.tools.toastLong
import vip.mystery0.tools.utils.dpTopx
import vip.mystery0.tools.utils.screenWidth
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class BottomNavigationActivity : XhuBaseActivity(R.layout.activity_bottom_navigation) {
	private val bottomNavigationViewModel: BottomNavigationViewModel by viewModel()

	private val viewPagerAdapter: ViewPagerAdapter by lazy { ViewPagerAdapter(supportFragmentManager) }
	private val dialog: Dialog by lazy { buildDialog(getString(R.string.hint_dialog_init)) }

	private var animation: ObjectAnimator? = null
	private var arrowAnimation: ObjectAnimator? = null

	private val courseList = ArrayList<Schedule>()
	private var isShowWeekView = false
	private val showAdapter: ShowCourseRecyclerViewAdapter by lazy { ShowCourseRecyclerViewAdapter(this) }
	private lateinit var loadingAnimation: ObjectAnimator

	private val studentListObserver = object : DataObserver<List<Student>> {
		override fun loading() {
			super.loading()
			showDialog()
		}

		override fun empty() {
			super.empty()
			startActivityForResult(Intent(this@BottomNavigationActivity, LoginActivity::class.java), ADD_ACCOUNT_CODE)
			hideDialog()
		}

		override fun error(e: Throwable?) {
			super.error(e)
			Logs.wtfm("studentListObserver: ", e)
			toastLong(e)
			hideDialog()
		}
	}

	private val courseListObserver = object : DataObserver<List<Schedule>> {
		override fun contentNoEmpty(data: List<Schedule>) {
			//TODO 正确信息提示
//			if (action == ACTION_REFRESH) {
//				toastMessage(R.string.hint_course_sync_done)
//				action = ACTION_NONE
//			}
			cancelLoading()
			hideDialog()
		}

		override fun loading() {
			showLoading()
		}

		override fun error(e: Throwable?) {
			Logs.wtfm("courseListObserver: ", e)
			toastLong(e)
			cancelLoading()
			hideDialog()
		}

		override fun empty() {
			cancelLoading()
			hideDialog()
		}
	}


	private val currentWeekObserver = object : PackageDataObserver<Int> {
		override fun content(data: Int?) {
			val week = when {
				data!! < 1 -> 1
				data > 20 -> 20
				else -> data
			}
			weekView.curWeek(week).showView()
			bottomNavigationViewModel.week.value = week
			viewPagerAdapter.getItem(viewPager.currentItem).updateTitle()
		}

		override fun error(data: Int?, e: Throwable?) {
			Logs.wtfm("currentWeekObserver: ", e)
			e.toastLong(this@BottomNavigationActivity)
			cancelLoading()
			hideDialog()
		}

		override fun loading() {
			showLoading()
		}

		override fun empty(data: Int?) {
			cancelLoading()
			hideDialog()
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
						BottomTabItem(
								if (CalendarUtil.shouldShowTomorrowInfo()) getString(R.string.nav_tomorrow)
								else getString(R.string.nav_today),
								R.drawable.ic_today_selected,
								R.drawable.ic_today),
						BottomTabItem(getString(R.string.nav_week), R.drawable.ic_week_selected, R.drawable.ic_week),
						BottomTabItem(getString(R.string.nav_profile), R.drawable.ic_profile_selected, R.drawable.ic_profile)
				))
				.init()
	}

	private fun showBackground() {
		val path = ConfigurationUtil.customBackgroundImage
		val file = File(path)
		if (path == "" || !file.exists()) {
			backgroundImageView.load(R.mipmap.bg1)
		} else {
			backgroundImageView.load(file) {
				diskCachePolicy(CachePolicy.DISABLED)
			}
		}
	}

	override fun initData() {
		super.initData()
		initViewModel()
		viewPagerAdapter.getItem(0).updateTitle()
		configWeekView(0)
		bottomNavigationViewModel.init()
		bottomNavigationViewModel.queryCurrentWeek()
		bottomNavigationViewModel.queryNewNotice()
		bottomNavigationViewModel.queryNewFeedbackMessage()
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
		bottomNavigationView.linkViewPager(viewPager, {
			viewPagerAdapter.getItem(it).updateTitle()
			configWeekView(it)
		}, false)
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
			bottomNavigationViewModel.queryOnline()
		}
	}

//	override fun onResume() {
//		super.onResume()
//		if (LayoutRefreshConfigUtil.isChangeBackgroundImage) {
//			showBackground()
//		}
//		if (LayoutRefreshConfigUtil.isRefreshNoticeDot) {
//			BottomNavigationRepository.queryNotice(bottomNavigationViewModel, false)
//		}
//		if (LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity) {
//			BottomNavigationRepository.queryStudentList(bottomNavigationViewModel)
//		}
//		if (LayoutRefreshConfigUtil.isChangeShowTomorrowAfterOnBottomActivity) {
//			//检测是否修改了显示明日课程的时间
//			bottomNavigationView.findItem(0)
//					.name = if (CalendarUtil.shouldShowTomorrowInfo()) getString(R.string.nav_tomorrow)
//			else getString(R.string.nav_today)
//			bottomNavigationView.init()
//		}
//		LayoutRefreshConfigUtil.isChangeBackgroundImage = false
//		LayoutRefreshConfigUtil.isRefreshNoticeDot = false
//		LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = false
//		LayoutRefreshConfigUtil.isChangeShowTomorrowAfterOnBottomActivity = false
//	}

	override fun onBackPressed() {
		if (ConfigUtil.isTwiceClick())
			super.onBackPressed()
		else
			toastLong(R.string.hint_twice_press_exit)
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
		animation = ObjectAnimator.ofFloat(weekView, "translationY", 0F, dpTopx(71F).toFloat())
		arrowAnimation = ObjectAnimator.ofFloat(arrowImageView, "rotation", 0F, 180F)
		animation?.start()
		arrowAnimation?.start()
		isShowWeekView = true
	}

	private fun hideWeekView() {
		animation?.cancel()
		arrowAnimation?.cancel()
		animation = ObjectAnimator.ofFloat(weekView, "translationY", dpTopx(71F).toFloat(), 0F)
		arrowAnimation = ObjectAnimator.ofFloat(arrowImageView, "rotation", 180F, 0F)
		animation?.start()
		arrowAnimation?.start()
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
					.subscribeOn(Schedulers.single())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : io.reactivex.Observer<Boolean> {
						override fun onComplete() {
							if (bottomNavigationViewModel.courseList.value != null && bottomNavigationViewModel.courseList.value!!.data != null) {
								courseList.clear()
								courseList.addAll(bottomNavigationViewModel.courseList.value!!.data!!)
								if (bottomNavigationViewModel.startDateTime.value != null && bottomNavigationViewModel.startDateTime.value != null) {
									val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
									bottomNavigationViewModel.week.value = ScheduleSupport.timeTransfrom(simpleDateFormat.format(bottomNavigationViewModel.startDateTime.value!!.time))
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
		dialogShowCourseBinding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
		dialogShowCourseBinding.recyclerView.adapter = showAdapter
		dialogShowCourseBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				val horizontalOffset = recyclerView.computeHorizontalScrollOffset().toFloat() / screenWidth
				val params = dialogShowCourseBinding.point.layoutParams as ConstraintLayout.LayoutParams
				super.onScrolled(recyclerView, dx, dy)
				params.leftMargin = (distance * horizontalOffset).roundToInt()
				dialogShowCourseBinding.point.layoutParams = params
			}
		})
		PagerSnapHelper().attachToRecyclerView(dialogShowCourseBinding.recyclerView)
		val viewSize = screenWidth - dpTopx(96F)
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
		weekView.post {
			popupWindow.showAtLocation(weekView, Gravity.CENTER, 0, 0)
		}
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
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode) {
			ADD_ACCOUNT_CODE -> {
				if (resultCode == Activity.RESULT_OK) {
					bottomNavigationViewModel.init()
				} else {
					finish()
				}
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		animation?.cancel()
		arrowAnimation?.cancel()
	}
}
