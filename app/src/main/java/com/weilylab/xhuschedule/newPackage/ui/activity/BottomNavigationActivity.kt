package com.weilylab.xhuschedule.newPackage.ui.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.newPackage.ui.adapter.ShowCourseRecyclerViewAdapter
import com.weilylab.xhuschedule.newPackage.ui.adapter.ViewPagerAdapter
import com.weilylab.xhuschedule.newPackage.ui.fragment.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.newPackage.ui.fragment.ProfileFragment
import com.weilylab.xhuschedule.newPackage.ui.fragment.TableFragment
import com.weilylab.xhuschedule.newPackage.ui.fragment.TodayFragment
import com.weilylab.xhuschedule.newPackage.utils.layoutManager.EchelonLayoutManager
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import com.zhuangfei.timetable.model.Schedule
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.content_bottom_navigation.*
import vip.mystery0.tools.base.BaseActivity
import vip.mystery0.tools.utils.DensityTools
import java.util.ArrayList

class BottomNavigationActivity : BaseActivity(R.layout.activity_bottom_navigation) {
	companion object {
		private const val ADD_ACCOUNT_CODE = 21
	}

	private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
	private lateinit var viewPagerAdapter: ViewPagerAdapter
	private lateinit var dialog: Dialog
	private var animation: ObjectAnimator? = null
	private var isShowWeekView = false
	private lateinit var showAdapter: ShowCourseRecyclerViewAdapter
	private val showCourseList = ArrayList<Schedule>()

	private val messageObserver = Observer<String> {
		Snackbar.make(coordinatorLayout, it, Snackbar.LENGTH_LONG)
				.show()
	}

	private val requestCodeObserver = Observer<Int> {
		if (it != BottomNavigationRepository.DONE) {
			hideDialog()
		}
	}

	private val courseListObserver = Observer<List<Schedule>> {
		weekView.setData(it).showView()
	}

	private val currentWeekObserver = Observer<Int> {
		val week = when {
			it < 1 -> 1
			it > 20 -> 20
			else -> it
		}
		weekView.setCurWeek(week).showView()
	}

	private val showCourseObserver = Observer<List<Schedule>> {
		if (it.isEmpty())
			return@Observer
		showCourseList.clear()
		showCourseList.addAll(it)
		showAdapter.notifyDataSetChanged()
		showPopupWindow()
	}

	private val titleObserver = Observer<String> {
		titleTextView.text = it
	}

	override fun initView() {
		super.initView()
		titleTextView.text = title
		initDialog()
		showDialog()
		initPopupWindow()
		viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
		viewPagerAdapter.addFragment(TodayFragment.newInstance())
		viewPagerAdapter.addFragment(TableFragment.newInstance())
		viewPagerAdapter.addFragment(ProfileFragment.newInstance())
		viewPager.offscreenPageLimit = 2
		viewPager.adapter = viewPagerAdapter
		weekView.setCurWeek(1)
				.hideLeftLayout()
				.setOnWeekItemClickedListener {
					bottomNavigationViewModel.week.value = it
				}
				.showView()
	}

	override fun initData() {
		super.initData()
		initViewModel()
		BottomNavigationRepository.queryAllStudent(bottomNavigationViewModel)
		BottomNavigationRepository.queryCurrentWeek(bottomNavigationViewModel)
	}

	private fun initViewModel() {
		bottomNavigationViewModel = ViewModelProviders.of(this).get(BottomNavigationViewModel::class.java)
		bottomNavigationViewModel.studentList.observe(this, Observer<List<Student>> {
			if (it.isEmpty()) {
				startActivityForResult(Intent(this, LoginActivity::class.java), ADD_ACCOUNT_CODE)
			}
			hideDialog()
		})
		bottomNavigationViewModel.message.observe(this, messageObserver)
		bottomNavigationViewModel.requestCode.observe(this, requestCodeObserver)
		bottomNavigationViewModel.currentWeek.observe(this, currentWeekObserver)
		bottomNavigationViewModel.courseList.observe(this, courseListObserver)
		bottomNavigationViewModel.showCourse.observe(this, showCourseObserver)
		bottomNavigationViewModel.title.observe(this, titleObserver)
	}

	private fun initDialog() {
		dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.STAR_LOADING)
				.setHintText(getString(R.string.hint_dialog_init))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}

	override fun monitor() {
		super.monitor()
		bottomNavigationView.setOnNavigationItemSelectedListener {
			when (it.itemId) {
				R.id.bottom_nav_today -> viewPager.setCurrentItem(0, true)
				R.id.bottom_nav_week -> viewPager.setCurrentItem(1, true)
				R.id.bottom_nav_profile -> viewPager.setCurrentItem(2, true)
			}
			true
		}
		viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
			override fun onPageScrollStateChanged(state: Int) {
			}

			override fun onPageScrolled(position: Int, positionOffset: Float,
										positionOffsetPixels: Int) {
			}

			override fun onPageSelected(position: Int) {
				bottomNavigationView.menu.getItem(position).isChecked = true
				(viewPagerAdapter.getItem(position) as BaseBottomNavigationFragment).updateTitle()
			}
		})
		titleTextView.setOnClickListener {
			if (isShowWeekView)
				hideWeekView()
			else
				showWeekView()
			isShowWeekView = !isShowWeekView
		}
	}

	private fun showWeekView() {
		animation?.cancel()
		animation = ObjectAnimator.ofFloat(weekView, "translationY", 0F, DensityTools.dp2px(this, 90F).toFloat())
		animation!!.addListener(object : Animator.AnimatorListener {
			override fun onAnimationRepeat(p0: Animator?) {
			}

			override fun onAnimationEnd(p0: Animator?) {
			}

			override fun onAnimationCancel(p0: Animator?) {
			}

			override fun onAnimationStart(p0: Animator?) {
				weekView.isShow(true)
			}
		})
		animation!!.start()
	}

	private fun hideWeekView() {
		animation?.cancel()
		animation = ObjectAnimator.ofFloat(weekView, "translationY", DensityTools.dp2px(this, 90F).toFloat(), 0F)
		animation!!.addListener(object : Animator.AnimatorListener {
			override fun onAnimationRepeat(p0: Animator?) {
			}

			override fun onAnimationEnd(p0: Animator?) {
				weekView.isShow(false)
			}

			override fun onAnimationCancel(p0: Animator?) {
			}

			override fun onAnimationStart(p0: Animator?) {
			}
		})
		animation!!.start()
	}

	private fun showDialog() {
		if (!dialog.isShowing)
			dialog.show()
	}

	private fun hideDialog() {
		if (dialog.isShowing)
			dialog.dismiss()
	}

	private lateinit var popupWindow: PopupWindow
	private lateinit var recyclerView: RecyclerView

	private fun initPopupWindow() {
		recyclerView = RecyclerView(this)
		recyclerView.layoutManager = EchelonLayoutManager(this)
		showAdapter = ShowCourseRecyclerViewAdapter(this, showCourseList)
		recyclerView.adapter = showAdapter
		popupWindow = PopupWindow(recyclerView, DensityTools.dp2px(this, 320F), DensityTools.dp2px(this, 480F))
		popupWindow.isOutsideTouchable = true
		popupWindow.isFocusable = true
		popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
	}

	private fun showPopupWindow() {
		popupWindow.showAtLocation(weekView, Gravity.CENTER, 0, 0)
		val week = bottomNavigationViewModel.week.value
		var position = 0
		showCourseList.forEachIndexed { index, it ->
			if (it.weekList.contains(week))
				position = index
		}
		recyclerView.scrollToPosition(position)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			ADD_ACCOUNT_CODE -> {
				if (resultCode == Activity.RESULT_OK) {
					BottomNavigationRepository.queryAllStudent(bottomNavigationViewModel)
				} else {
					finish()
				}
			}
		}
	}
}
