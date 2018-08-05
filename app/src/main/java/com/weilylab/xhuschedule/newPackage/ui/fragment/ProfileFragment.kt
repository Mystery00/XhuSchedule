package com.weilylab.xhuschedule.newPackage.ui.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.FragmentProfileBinding
import com.weilylab.xhuschedule.newPackage.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.newPackage.config.Status.*
import com.weilylab.xhuschedule.newPackage.ui.activity.NoticeActivity
import com.weilylab.xhuschedule.newPackage.ui.activity.QueryTestActivity
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import vip.mystery0.logs.Logs

class ProfileFragment : BaseBottomNavigationFragment(R.layout.fragment_profile) {
	private lateinit var fragmentProfileBinding: FragmentProfileBinding
	private lateinit var bottomNavigationViewModel: BottomNavigationViewModel

	companion object {
		fun newInstance() = ProfileFragment()
	}

	override fun inflateView(layoutId: Int, inflater: LayoutInflater, container: ViewGroup?): View {
		fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
		return fragmentProfileBinding.root
	}

	override fun initView() {
		initViewModel()
	}

	private fun initViewModel() {
		bottomNavigationViewModel = ViewModelProviders.of(activity!!).get(BottomNavigationViewModel::class.java)
		bottomNavigationViewModel.studentInfo.observe(activity!!, Observer {
			when (it.status) {
				Content -> fragmentProfileBinding.studentInfo = it.data
				Error -> {
					toastMessage(it.error?.message)
				}
			}
		})
		bottomNavigationViewModel.noticeList.observe(activity!!, Observer { packageData ->
			when (packageData.status) {
				Content -> {
					if (packageData.data == null || packageData.data.isEmpty())
						fragmentProfileBinding.redDotView.visibility = View.GONE
					else {
						packageData.data.forEach {
							if (!it.isRead) {
								fragmentProfileBinding.redDotView.visibility = View.VISIBLE
								return@Observer
							}
						}
						fragmentProfileBinding.redDotView.visibility = View.GONE
					}
				}
				Loading, Empty, Error ->
					fragmentProfileBinding.redDotView.visibility = View.GONE
			}
		})
	}

	override fun monitor() {
		super.monitor()
		fragmentProfileBinding.queryTestLayout.setOnClickListener {
			startActivity(Intent(activity, QueryTestActivity::class.java))
		}
		fragmentProfileBinding.noticeLayout.setOnClickListener {
			startActivity(Intent(activity, NoticeActivity::class.java))
		}
	}

	override fun updateTitle() {
		RxObservable<Boolean>()
				.doThings {
					var num = 0
					while (true) {
						when {
							::bottomNavigationViewModel.isInitialized -> it.onFinish(true)
							num >= 10 -> it.onFinish(false)
						}
						Thread.sleep(200)
						num++
					}
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onFinish(data: Boolean?) {
						if (data != null && data)
							bottomNavigationViewModel.title.value = "我的"
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}
}
