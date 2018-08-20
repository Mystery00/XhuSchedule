package com.weilylab.xhuschedule.ui.fragment

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.DialogShareWithFriendsBinding
import com.weilylab.xhuschedule.databinding.FragmentProfileBinding
import com.weilylab.xhuschedule.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.ui.activity.*
import com.weilylab.xhuschedule.utils.AnimationUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.utils.ShareUtil
import com.weilylab.xhuschedule.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import com.weilylab.xhuschedule.viewModel.BottomNavigationViewModel
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.content_bottom_navigation.*
import vip.mystery0.logs.Logs
import java.io.File

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
		showUserImage()
		initViewModel()
		initShareMenu()
	}

	private fun showUserImage() {
		val path = ConfigurationUtil.customUserImage
		if (path == "" || !File(path).exists()) {
			fragmentProfileBinding.studentProfileImage.setImageResource(R.mipmap.image_profile)
		} else {
			val options = RequestOptions()
					.signature(MediaStoreSignature("image/*", File(path).lastModified(), 0))
					.diskCacheStrategy(DiskCacheStrategy.NONE)
			Glide.with(this)
					.load(path)
					.apply(options)
					.into(fragmentProfileBinding.studentProfileImage)
		}
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
		fragmentProfileBinding.queryScoreLayout.setOnClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_QUERY_SCORE)
		}
		fragmentProfileBinding.accountSettingsLayout.setOnClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_ACCOUNT)
		}
		fragmentProfileBinding.classSettingsLayout.setOnClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_CLASS)
		}
		fragmentProfileBinding.softwareSettingsLayout.setOnClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_SETTINGS)
		}
		fragmentProfileBinding.noticeLayout.setOnClickListener {
			startActivity(Intent(activity, NoticeActivity::class.java))
		}
		fragmentProfileBinding.feedbackLayout.setOnClickListener {
			startActivity(Intent(activity, FeedbackActivity::class.java))
		}
		fragmentProfileBinding.shareWithFriendsLayout.setOnClickListener {
			showShareMenu()
		}
	}

	override fun onResume() {
		super.onResume()
		if (LayoutRefreshConfigUtil.isChangeUserImage) {
			showUserImage()
			LayoutRefreshConfigUtil.isChangeUserImage = false
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
