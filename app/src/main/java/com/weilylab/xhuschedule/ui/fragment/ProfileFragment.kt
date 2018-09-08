package com.weilylab.xhuschedule.ui.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.DialogShareWithFriendsBinding
import com.weilylab.xhuschedule.databinding.FragmentProfileBinding
import com.weilylab.xhuschedule.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.ui.activity.*
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.utils.ShareUtil
import com.weilylab.xhuschedule.viewModel.BottomNavigationViewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.Status.*
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver
import java.io.File

class ProfileFragment : BaseBottomNavigationFragment<FragmentProfileBinding>(R.layout.fragment_profile) {
	companion object {
		fun newInstance() = ProfileFragment()
	}

	private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
	private lateinit var bottomSheetDialog: BottomSheetDialog

	override fun initView() {
		showUserImage()
		initViewModel()
		initShareMenu()
	}

	private fun showUserImage() {
		val path = ConfigurationUtil.customUserImage
		if (path == "" || !File(path).exists()) {
			binding.studentProfileImage.setImageResource(R.mipmap.image_profile)
		} else {
			val options = RequestOptions()
					.signature(MediaStoreSignature("image/*", File(path).lastModified(), 0))
					.diskCacheStrategy(DiskCacheStrategy.NONE)
			Glide.with(this)
					.load(path)
					.apply(options)
					.into(binding.studentProfileImage)
		}
	}

	private fun initViewModel() {
		bottomNavigationViewModel = ViewModelProviders.of(activity!!).get(BottomNavigationViewModel::class.java)
		bottomNavigationViewModel.studentInfo.observe(activity!!, Observer {
			when (it.status) {
				Content -> binding.studentInfo = it.data
				Error -> {
					Logs.wtf("initViewModel: ", it.error)
					toastMessage(it.error?.message)
				}
			}
		})
		bottomNavigationViewModel.noticeList.observe(activity!!, Observer { packageData ->
			when (packageData.status) {
				Content -> {
					LayoutRefreshConfigUtil.isRefreshNoticeDone = true
					if (packageData.data == null || packageData.data!!.isEmpty())
						binding.redDotView.visibility = View.GONE
					else {
						packageData.data!!.forEach {
							if (!it.isRead) {
								binding.redDotView.visibility = View.VISIBLE
								return@Observer
							}
						}
						binding.redDotView.visibility = View.GONE
					}
				}
				Loading, Empty, Error ->
					binding.redDotView.visibility = View.GONE
			}
		})
		bottomNavigationViewModel.newFeedBackMessageList.observe(activity!!, Observer { packageData ->
			when (packageData.status) {
				Content -> {
					if (packageData.data != null && packageData.data!!.isNotEmpty())
						binding.feedBackRedDotView.visibility = View.VISIBLE
					else
						binding.feedBackRedDotView.visibility = View.GONE
				}
				Loading, Empty, Error ->
					binding.feedBackRedDotView.visibility = View.GONE
			}
		})
	}

	override fun monitor() {
		super.monitor()
		binding.queryTestLayout.setOnClickListener {
			startActivity(Intent(activity, QueryTestActivity::class.java))
		}
		binding.queryScoreLayout.setOnClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_QUERY_SCORE)
		}
		binding.accountSettingsLayout.setOnClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_ACCOUNT)
		}
		binding.classSettingsLayout.setOnClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_CLASS)
		}
		binding.softwareSettingsLayout.setOnClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_SETTINGS)
		}
		binding.noticeLayout.setOnClickListener {
			startActivity(Intent(activity, NoticeActivity::class.java))
		}
		binding.feedbackLayout.setOnClickListener {
			startActivity(Intent(activity, FeedbackActivity::class.java))
		}
		binding.shareWithFriendsLayout.setOnClickListener {
			showShareMenu()
		}
	}

	override fun onResume() {
		super.onResume()
		if (LayoutRefreshConfigUtil.isChangeUserImage) {
			showUserImage()
			LayoutRefreshConfigUtil.isChangeUserImage = false
		}
		if (LayoutRefreshConfigUtil.isRefreshFeedBackDot) {
			binding.feedBackRedDotView.visibility = View.GONE
			LayoutRefreshConfigUtil.isRefreshFeedBackDot = false
		}
	}

	private fun initShareMenu() {
		val binding = DialogShareWithFriendsBinding.inflate(LayoutInflater.from(activity))
		bottomSheetDialog = BottomSheetDialog(activity!!)
		bottomSheetDialog.setContentView(binding.root)
		bottomSheetDialog.setCancelable(true)
		bottomSheetDialog.setCanceledOnTouchOutside(true)
		binding.textViewCancel.setOnClickListener {
			bottomSheetDialog.dismiss()
		}
		binding.qqShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.QQ)
			bottomSheetDialog.dismiss()
		}
		binding.qzoneShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.QZONE)
			bottomSheetDialog.dismiss()
		}
		binding.weiboShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.WEIBO)
			bottomSheetDialog.dismiss()
		}
		binding.wxShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.WEIXIN)
			bottomSheetDialog.dismiss()
		}
		binding.friendShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.FRIEND)
			bottomSheetDialog.dismiss()
		}
		binding.systemShareLayout.setOnClickListener {
			ShareUtil.shareApplication(activity!!, ShareUtil.ShareType.SYSTEM)
			bottomSheetDialog.dismiss()
		}
	}

	private fun showShareMenu() {
		bottomSheetDialog.show()
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
