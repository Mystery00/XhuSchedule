package com.weilylab.xhuschedule.newPackage.ui.activity

import android.app.Dialog
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.content_main.*
import vip.mystery0.tools.base.BaseActivity

class BottomNavigationActivity : BaseActivity(R.layout.activity_bottom_navigation) {
	companion object {
		private const val ADD_ACCOUNT_CODE = 21
	}

	private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
	private lateinit var dialog: Dialog

	private val messageObserver = Observer<String> {
		Snackbar.make(coordinatorLayout, it, Snackbar.LENGTH_SHORT)
				.show()
	}
	private val requestCodeObserver = Observer<Int> {
		if (it != BottomNavigationRepository.DONE) {
			hideDialog()
		}
	}

	override fun initView() {
		super.initView()
		initDialog()
		showDialog()
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
//				R.id.navigation_home -> {
//				}
//				R.id.navigation_dashboard -> {
//				}
//				R.id.navigation_notifications -> {
//				}
			}
			true
		}
	}

	private fun showDialog() {
		if (!dialog.isShowing)
			dialog.show()
	}

	private fun hideDialog() {
		if (dialog.isShowing)
			dialog.dismiss()
	}
}
