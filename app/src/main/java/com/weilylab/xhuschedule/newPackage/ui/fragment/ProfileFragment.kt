package com.weilylab.xhuschedule.newPackage.ui.fragment

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.DialogShareWithFriendsBinding
import com.weilylab.xhuschedule.newPackage.ui.activity.SettingsActivity
import com.weilylab.xhuschedule.databinding.FragmentProfileBinding
import com.weilylab.xhuschedule.newPackage.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.newPackage.config.Status.*
import com.weilylab.xhuschedule.newPackage.ui.activity.BottomNavigationActivity
import com.weilylab.xhuschedule.newPackage.ui.activity.NoticeActivity
import com.weilylab.xhuschedule.newPackage.ui.activity.QueryTestActivity
import com.weilylab.xhuschedule.newPackage.utils.AnimationUtil
import com.weilylab.xhuschedule.newPackage.utils.ShareUtil
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import kotlinx.android.synthetic.main.content_bottom_navigation.*
import vip.mystery0.logs.Logs

class ProfileFragment : BaseBottomNavigationFragment(R.layout.fragment_profile) {
	private lateinit var fragmentProfileBinding: FragmentProfileBinding
	private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
	private lateinit var shareView: PopupWindow

	companion object {
		fun newInstance() = ProfileFragment()
	}

	override fun inflateView(layoutId: Int, inflater: LayoutInflater, container: ViewGroup?): View {
		fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
		return fragmentProfileBinding.root
	}

	override fun initView() {
		initViewModel()
		initShareMenu()
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
		fragmentProfileBinding.accountSettingsLayout.setOnClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_ACCOUNT)
		}
		fragmentProfileBinding.classSettingsLayout.setOnClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_CLASS)
		}
		fragmentProfileBinding.noticeLayout.setOnClickListener {
			startActivity(Intent(activity, NoticeActivity::class.java))
		}
		fragmentProfileBinding.shareWithFriendsLayout.setOnClickListener {
			showShareMenu()
		}
	}

	private fun initShareMenu() {
		val binding = DialogShareWithFriendsBinding.inflate(LayoutInflater.from(activity))
		shareView = PopupWindow(binding.root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		shareView.isOutsideTouchable = true
		shareView.isFocusable = true
		shareView.animationStyle = R.style.Animation
		shareView.setBackgroundDrawable(ColorDrawable(0x00000000))
		shareView.setOnDismissListener {
			AnimationUtil.setWindowAlpha(activity, 1F)
		}
		binding.textViewCancel.setOnClickListener {
			shareView.dismiss()
		}
		binding.qqShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.QQ)
			shareView.dismiss()
		}
		binding.qzoneShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.QZONE)
			shareView.dismiss()
		}
		binding.weiboShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.WEIBO)
			shareView.dismiss()
		}
		binding.wxShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.WEIXIN)
			shareView.dismiss()
		}
		binding.friendShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.FRIEND)
			shareView.dismiss()
		}
		binding.systemShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.SYSTEM)
			shareView.dismiss()
		}
	}

	private fun showShareMenu() {
		shareView.showAtLocation((activity as BottomNavigationActivity).bottomNavigationView, Gravity.BOTTOM, 0, 0)
		AnimationUtil.setWindowAlpha(activity, 0.6F)
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
