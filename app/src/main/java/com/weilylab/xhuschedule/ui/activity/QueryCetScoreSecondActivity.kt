package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.android.setupwizardlib.view.NavigationBar
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.model.CetScore
import com.weilylab.xhuschedule.repository.ScoreRepository
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.viewModel.QueryCetScoreViewModelHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_query_cet_score_second.*

class QueryCetScoreSecondActivity : XhuBaseActivity(R.layout.activity_query_cet_score_second) {
	private lateinit var vCodeDialog: Dialog
	private lateinit var dialog: Dialog

	private val cetVCodeObserver = Observer<PackageData<Bitmap>> {
		when (it.status) {
			Content -> {
				hideVCodeDialog()
				imageView.setImageBitmap(it.data)
			}
			Loading -> showVCodeDialog()
			Empty -> {
				hideVCodeDialog()
				Toast.makeText(this, "获取数据为空！", Toast.LENGTH_LONG).show()
			}
			Error -> {
				hideVCodeDialog()
				Toast.makeText(this, it.error?.message, Toast.LENGTH_LONG).show()
			}
		}
	}

	private val cetScoreObserver = Observer<PackageData<CetScore>> {
		when (it.status) {
			Content -> {
				hideDialog()
				startActivity(Intent(this, QueryCetScoreShowActivity::class.java))
			}
			Loading -> showDialog()
			Empty -> {
				hideDialog()
				Toast.makeText(this, "获取数据为空！", Toast.LENGTH_LONG).show()
			}
			Error -> {
				hideDialog()
				Toast.makeText(this, it.error?.message, Toast.LENGTH_LONG).show()
			}
		}
	}

	override fun initView() {
		super.initView()
		initDialog()
		setupLayout.setIllustrationAspectRatio(3F)
		setupLayout.setIllustration(ContextCompat.getDrawable(this, R.mipmap.header_cet))
		setupLayout.navigationBar.backButton.setText(R.string.action_last)
		setupLayout.navigationBar.moreButton.setText(R.string.action_more)
		setupLayout.navigationBar.nextButton.setText(R.string.action_query)
	}

	private fun initDialog() {
		vCodeDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_get_cet_vcode))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
		dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_query_cet_score))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}

	override fun initData() {
		super.initData()
		initViewModel()
	}

	private fun initViewModel() {
		QueryCetScoreViewModelHelper.cetVCodeLiveData.observe(this, cetVCodeObserver)
		QueryCetScoreViewModelHelper.cetScoreLiveData.observe(this, cetScoreObserver)
	}

	override fun monitor() {
		super.monitor()
		imageView.setOnClickListener {
			ScoreRepository.getCetVCode()
		}
		setupLayout.navigationBar.setNavigationBarListener(object : NavigationBar.NavigationBarListener {
			override fun onNavigateBack() {
				onBackPressed()
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

	private fun queryCetScore() {
		cetVCodeEditText.error = null

		val cetVCodeStr = cetVCodeEditText.text.toString()

		if (TextUtils.isEmpty(cetVCodeStr)) {
			cetVCodeEditText.error = getString(R.string.error_field_required)
			cetVCodeEditText.requestFocus()
			return
		}
		//ScoreRepository.getCetScore(cetVCodeStr)
		QueryCetScoreViewModelHelper.cetScoreLiveData.value = PackageData.content(CetScore())
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
		if (!vCodeDialog.isShowing)
			vCodeDialog.show()
	}

	private fun hideDialog() {
		if (vCodeDialog.isShowing)
			vCodeDialog.dismiss()
	}
}