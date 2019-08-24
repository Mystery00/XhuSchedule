package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.widget.ScrollView
import androidx.core.content.ContextCompat
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
import vip.mystery0.rx.PackageDataObserver
import vip.mystery0.tools.toastLong

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

	private val cetVCodeObserver = object : PackageDataObserver<Bitmap> {
		override fun content(data: Bitmap?) {
			hideVCodeDialog()
			imageView.setImageBitmap(data)
		}

		override fun loading() {
			showVCodeDialog()
		}

		override fun empty(data: Bitmap?) {
			hideVCodeDialog()
			toastMessage(R.string.hint_data_null, true)
		}

		override fun error(data: Bitmap?, e: Throwable?) {
			Logs.wtfm("cetVCodeObserver: ", e)
			hideVCodeDialog()
			e.toastLong(this@QueryCetScoreSecondActivity)
		}
	}

	private val cetScoreObserver = object : PackageDataObserver<CetScore> {
		override fun content(data: CetScore?) {
			hideDialog()
			startActivity(Intent(this@QueryCetScoreSecondActivity, QueryCetScoreShowActivity::class.java))
			finish()
		}

		override fun loading() {
			showDialog()
		}

		override fun empty(data: CetScore?) {
			hideDialog()
			toastMessage(R.string.hint_data_null, true)
		}

		override fun error(data: CetScore?, e: Throwable?) {
			Logs.wtfm("cetScoreObserver: ", e)
			hideDialog()
			e.toastLong(this@QueryCetScoreSecondActivity)
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