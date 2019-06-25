package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.android.setupwizardlib.view.NavigationBar
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.CetScore
import com.weilylab.xhuschedule.repository.ScoreRepository
import com.weilylab.xhuschedule.viewmodel.QueryCetScoreViewModelHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_query_cet_score_second.*
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.Status.*

class QueryCetScoreSecondActivity : XhuBaseActivity(R.layout.activity_query_cet_score_second) {
	private val vCodeDialog: Dialog by lazy {
		ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_get_cet_vcode))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(this, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}
	private val dialog: Dialog by lazy {
		ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_query_cet_score))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(this, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}

	private val cetVCodeObserver = Observer<PackageData<Bitmap>> {
		when (it.status) {
			Content -> {
				hideVCodeDialog()
				imageView.setImageBitmap(it.data)
			}
			Loading -> showVCodeDialog()
			Empty -> {
				hideVCodeDialog()
				toastMessage(R.string.hint_data_null, true)
			}
			Error -> {
				Logs.wtfm("cetVCodeObserver: ", it.error)
				hideVCodeDialog()
				toastMessage(it.error?.message, true)
			}
		}
	}

	private val cetScoreObserver = Observer<PackageData<CetScore>> {
		when (it.status) {
			Content -> {
				hideDialog()
				startActivity(Intent(this, QueryCetScoreShowActivity::class.java))
				finish()
			}
			Loading -> showDialog()
			Empty -> {
				hideDialog()
				toastMessage(R.string.hint_data_null, true)
			}
			Error -> {
				Logs.wtfm("cetScoreObserver: ", it.error)
				hideDialog()
				toastMessage(it.error?.message, true)
			}
		}
	}

	override fun initView() {
		super.initView()
		setupLayout.setIllustrationAspectRatio(3F)
		setupLayout.setIllustration(ContextCompat.getDrawable(this, R.mipmap.header_cet))
		setupLayout.navigationBar.backButton.setText(R.string.action_last)
		setupLayout.navigationBar.moreButton.setText(R.string.action_more)
		setupLayout.navigationBar.nextButton.setText(R.string.action_query)
	}

	override fun initData() {
		super.initData()
		initViewModel()
	}

	private fun initViewModel() {
		QueryCetScoreViewModelHelper.cetVCodeLiveData.observe(this, cetVCodeObserver)
		QueryCetScoreViewModelHelper.cetScoreLiveData.observe(this, cetScoreObserver)
	}

	private fun removeObserver() {
		QueryCetScoreViewModelHelper.cetVCodeLiveData.removeObserver(cetVCodeObserver)
		QueryCetScoreViewModelHelper.cetScoreLiveData.removeObserver(cetScoreObserver)
	}

	override fun monitor() {
		super.monitor()
		imageView.setOnClickListener {
			ScoreRepository.getCetVCode()
		}
		setupLayout.navigationBar.setNavigationBarListener(object : NavigationBar.NavigationBarListener {
			override fun onNavigateBack() {
				finish()
			}

			override fun onNavigateNext() {
				val scrollView = setupLayout.scrollView
				if (scrollView != null) {
					if (scrollView.getChildAt(0).bottom <= scrollView.height + scrollView.scrollY)
						queryCetScore()
					else
						scrollView.fullScroll(ScrollView.FOCUS_DOWN)

				}
			}
		})
	}

	override fun onDestroy() {
		super.onDestroy()
		removeObserver()
	}

	private fun queryCetScore() {
		cetVCodeEditText.error = null

		val cetVCodeStr = cetVCodeEditText.text.toString()

		if (TextUtils.isEmpty(cetVCodeStr)) {
			cetVCodeEditText.error = getString(R.string.error_field_required)
			cetVCodeEditText.requestFocus()
			return
		}
		ScoreRepository.getCetScore(cetVCodeStr)
	}

	private fun showVCodeDialog() {
		if (!vCodeDialog.isShowing)
			vCodeDialog.show()
	}

	private fun hideVCodeDialog() {
		if (vCodeDialog.isShowing)
			vCodeDialog.dismiss()
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