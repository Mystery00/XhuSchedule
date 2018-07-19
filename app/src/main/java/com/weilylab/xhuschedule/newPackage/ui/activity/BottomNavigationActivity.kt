package com.weilylab.xhuschedule.newPackage.ui.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.newPackage.ui.adapter.ViewPagerAdapter
import com.weilylab.xhuschedule.newPackage.ui.fragment.ProfileFragment
import com.weilylab.xhuschedule.newPackage.ui.fragment.TableFragment
import com.weilylab.xhuschedule.newPackage.ui.fragment.TodayFragment
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.content_bottom_navigation.*
import vip.mystery0.logs.Logs
import vip.mystery0.tools.base.BaseActivity

class BottomNavigationActivity : BaseActivity(R.layout.activity_bottom_navigation) {
	companion object {
		private const val ADD_ACCOUNT_CODE = 21
	}

	private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
	private lateinit var dialog: Dialog
	private var animation: ObjectAnimator? = null
	private var isShowWeekView = false

	private val messageObserver = Observer<String> {
		Snackbar.make(coordinatorLayout, it, Snackbar.LENGTH_LONG)
				.show()
	}
	private val requestCodeObserver = Observer<Int> {
		if (it != BottomNavigationRepository.DONE) {
			hideDialog()
		}
	}

	private val courseListObserver = Observer<List<Course>> {
		weekView.setSource(it).updateView()
	}

	override fun initView() {
		super.initView()
		initDialog()
		showDialog()
		val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
		viewPagerAdapter.addFragment(TodayFragment.newInstance())
		viewPagerAdapter.addFragment(TableFragment.newInstance())
		viewPagerAdapter.addFragment(ProfileFragment.newInstance())
		viewPager.offscreenPageLimit = 2
		viewPager.adapter = viewPagerAdapter
	}

	override fun initData() {
		super.initData()
		initViewModel()
		BottomNavigationRepository.queryAllStudent(bottomNavigationViewModel)
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
		bottomNavigationViewModel.courseList.observe(this, courseListObserver)
	}

	private fun initDialog() {
		dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.STAR_LOADING)
				.setHintText(getString(R.string.hint_dialog_init))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorPrimary))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
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
			}
		})
		weekView.setCurWeek(1)
				.setOnWeekItemClickedListener {
					Logs.i("monitor: setOnWeekItemClickedListener")
				}
				.setOnWeekLeftClickedListener {
					Logs.i("monitor: setOnWeekLeftClickedListener")
				}
		titleTextView.setOnClickListener {
			Logs.i("monitor: ")
			if (isShowWeekView)
				hideWeekView()
			else
				showWeekView()
			isShowWeekView = !isShowWeekView
		}
	}

	private fun showWeekView() {
		animation?.cancel()
		animation = ObjectAnimator.ofFloat(weekView, "translationY", 0F, 200F)
		animation!!.start()
	}

	private fun hideWeekView() {
		animation?.cancel()
		animation = ObjectAnimator.ofFloat(weekView, "translationY", 200F, 0F)
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
