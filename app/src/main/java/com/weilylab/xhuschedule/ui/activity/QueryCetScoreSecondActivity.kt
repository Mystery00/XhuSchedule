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
import com.weilylab.xhuschedule.viewmodel.QueryCetScoreViewModelHelper
import kotlinx.android.synthetic.main.activity_query_cet_score_second.*
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver

class QueryCetScoreSecondActivity : XhuBaseActivity(R.layout.activity_query_cet_score_second) {
	private val vCodeDialog: Dialog by lazy { buildDialog(R.string.hint_dialog_get_cet_vcode) }
	private val dialog: Dialog by lazy { buildDialog(R.string.hint_dialog_query_cet_score) }

	private val cetVCodeObserver = object : DataObserver<Bitmap> {
		override fun contentNoEmpty(data: Bitmap) {
			hideVCodeDialog()
			imageView.setImageBitmap(data)
		}

		override fun loading() {
			showVCodeDialog()
		}

		override fun empty() {
			hideVCodeDialog()
			toastLong(R.string.hint_data_null)
		}

		override fun error(e: Throwable?) {
			Logs.wtfm("cetVCodeObserver: ", e)
			hideVCodeDialog()
			toastLong(e)
		}
	}

	private val cetScoreObserver = object : DataObserver<CetScore> {
		override fun contentNoEmpty(data: CetScore) {
			hideDialog()
			startActivity(Intent(this@QueryCetScoreSecondActivity, QueryCetScoreShowActivity::class.java))
			finish()
		}

		override fun loading() {
			showDialog()
		}

		override fun empty() {
			hideDialog()
			toastLong(R.string.hint_data_null)
		}

		override fun error(e: Throwable?) {
			Logs.wtfm("cetScoreObserver: ", e)
			hideDialog()
			toastLong(e)
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
			QueryCetScoreViewModelHelper.getCetVCode(this)
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
		QueryCetScoreViewModelHelper.getCetScore(this, cetVCodeStr)
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