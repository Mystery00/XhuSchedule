package com.weilylab.xhuschedule.newPackage.ui.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.FragmentProfileBinding
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.newPackage.ui.activity.QueryTestActivity
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import vip.mystery0.logs.Logs

class ProfileFragment : BaseBottomNavigationFragment(R.layout.fragment_profile) {
	private lateinit var fragmentProfileBinding: FragmentProfileBinding
	private lateinit var bottomNavigationViewModel: BottomNavigationViewModel

	private val studentObserver = Observer<List<Student>> {
		if (it.isNotEmpty())
			BottomNavigationRepository.queryStudentInfo(it[0], bottomNavigationViewModel)
	}

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
			fragmentProfileBinding.studentInfo = it
		})
		bottomNavigationViewModel.studentList.observe(activity!!, studentObserver)
	}

	override fun monitor() {
		super.monitor()
		fragmentProfileBinding.queryTestLayout.setOnClickListener {
			startActivity(Intent(activity, QueryTestActivity::class.java))
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
