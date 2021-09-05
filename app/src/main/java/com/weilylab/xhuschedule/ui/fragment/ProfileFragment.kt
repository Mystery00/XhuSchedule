/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import coil.load
import coil.request.CachePolicy
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.databinding.DialogShareWithFriendsBinding
import com.weilylab.xhuschedule.databinding.FragmentProfileBinding
import com.weilylab.xhuschedule.model.event.UI
import com.weilylab.xhuschedule.model.event.UIConfigEvent
import com.weilylab.xhuschedule.ui.activity.*
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.ShareUtil
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vip.mystery0.rx.DataObserver
import java.io.File

class ProfileFragment : BaseBottomNavigationFragment<FragmentProfileBinding>(R.layout.fragment_profile) {
    companion object {
        private const val TAG = "ProfileFragment"
        fun newInstance() = ProfileFragment()
    }

    private val bottomNavigationViewModel: BottomNavigationViewModel by sharedViewModel()
    private val eventBus: EventBus by inject()
    private val bottomSheetDialog: BottomSheetDialog by lazy { BottomSheetDialog(requireActivity()) }

    override fun initView() {
        showUserImage()
        initViewModel()
        initShareMenu()
        bottomNavigationViewModel.studentInfo.value?.let {
            bottomNavigationViewModel.studentInfo.postValue(it)
        }
    }

    private fun showUserImage() {
        val path = ConfigurationUtil.customUserImage
        if (path == "" || !File(path).exists()) {
            binding.studentProfileImage.load(R.mipmap.share_launcher)
        } else {
            binding.studentProfileImage.load(File(path)) {
                diskCachePolicy(CachePolicy.DISABLED)
            }
        }
    }

    private fun initViewModel() {
        bottomNavigationViewModel.studentInfo.observe(requireActivity(), Observer {
            binding.studentInfo = it
        })
        bottomNavigationViewModel.newNotice.observe(this, object : DataObserver<Boolean> {
            override fun contentNoEmpty(data: Boolean) {
                super.contentNoEmpty(data)
                binding.redDotView.visibility = if (data) View.VISIBLE else View.GONE
            }

            override fun error(e: Throwable?) {
                super.error(e)
                Log.e(TAG, "error: ", e)
                binding.redDotView.visibility = View.GONE
            }

            override fun empty() {
                super.empty()
                binding.redDotView.visibility = View.GONE
            }
        })
        bottomNavigationViewModel.newFeedback.observe(this, object : DataObserver<Boolean> {
            override fun contentNoEmpty(data: Boolean) {
                super.contentNoEmpty(data)
                binding.feedBackRedDotView.visibility = if (data) View.VISIBLE else View.GONE
            }

            override fun error(e: Throwable?) {
                super.error(e)
                Log.e(TAG, "error: ", e)
                binding.feedBackRedDotView.visibility = View.GONE
            }

            override fun empty() {
                super.empty()
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
        binding.queryClassroomLayout.setOnClickListener {
            startActivity(Intent(activity, QueryClassroomActivity::class.java))
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

    private fun initShareMenu() {
        val binding = DialogShareWithFriendsBinding.inflate(LayoutInflater.from(activity))
        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        binding.textViewCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        binding.qqShareLayout.setOnClickListener {
            ShareUtil.shareApplication(requireActivity(), ShareUtil.ShareType.QQ)
            bottomSheetDialog.dismiss()
        }
        binding.qzoneShareLayout.setOnClickListener {
            ShareUtil.shareApplication(requireActivity(), ShareUtil.ShareType.QZONE)
            bottomSheetDialog.dismiss()
        }
        binding.weiboShareLayout.setOnClickListener {
            ShareUtil.shareApplication(requireActivity(), ShareUtil.ShareType.WEIBO)
            bottomSheetDialog.dismiss()
        }
        binding.wxShareLayout.setOnClickListener {
            ShareUtil.shareApplication(requireActivity(), ShareUtil.ShareType.WEIXIN)
            bottomSheetDialog.dismiss()
        }
        binding.friendShareLayout.setOnClickListener {
            ShareUtil.shareApplication(requireActivity(), ShareUtil.ShareType.FRIEND)
            bottomSheetDialog.dismiss()
        }
        binding.systemShareLayout.setOnClickListener {
            ShareUtil.shareApplication(requireActivity(), ShareUtil.ShareType.SYSTEM)
            bottomSheetDialog.dismiss()
        }
    }

    private fun showShareMenu() {
        bottomSheetDialog.show()
    }

    override fun updateTitle() {
        bottomNavigationViewModel.title.postValue(Pair(javaClass, getString(R.string.main_title_mine)))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        eventBus.register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        eventBus.unregister(this)
        super.onDestroyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateUIFromConfig(uiConfigEvent: UIConfigEvent) {
        if (uiConfigEvent.refreshUI.contains(UI.USER_IMG)) {
            showUserImage()
        }
    }
}
