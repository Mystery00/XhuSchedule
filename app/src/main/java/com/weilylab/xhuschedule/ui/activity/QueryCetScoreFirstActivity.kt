/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.android.setupwizardlib.view.NavigationBar
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.viewmodel.QueryCetScoreViewModelHelper
import kotlinx.android.synthetic.main.activity_query_cet_score_first.*
import org.koin.android.ext.android.inject
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver

class QueryCetScoreFirstActivity : XhuBaseActivity(R.layout.activity_query_cet_score_first) {
    private val dialog: Dialog by lazy { buildDialog(R.string.hint_dialog_get_cet_vcode) }
    private val shortcutManager: ShortcutManager by inject()

    private val studentObserver = Observer<Student> {
        if (it == null) {
            toastLong(R.string.hint_action_not_login)
            finish()
        }
        cetNameEditText.setText(it.studentName)
    }

    private val cetVCodeObserver = object : DataObserver<Bitmap> {
        override fun contentNoEmpty(data: Bitmap) {
            hideDialog()
            startActivity(Intent(this@QueryCetScoreFirstActivity, QueryCetScoreSecondActivity::class.java))
        }

        override fun loading() {
            showDialog()
        }

        override fun empty() {
            hideDialog()
            toastLong(R.string.hint_data_null)
        }

        override fun error(e: Throwable?) {
            Logs.w(e)
            hideDialog()
            toastLong(e)
        }
    }

    override fun initView() {
        super.initView()
        setupLayout.setIllustration(ContextCompat.getDrawable(this, R.mipmap.background_cet))
        setupLayout.setIllustrationAspectRatio(2F)
        setupLayout.navigationBar.moreButton.setText(R.string.action_more)
        setupLayout.navigationBar.nextButton.setText(R.string.action_next)
    }

    override fun initData() {
        super.initData()
        initViewModel()
        QueryCetScoreViewModelHelper.init(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val intent = Intent(this, QueryCetScoreFirstActivity::class.java)
            intent.action = "com.weilylab.xhuschedule.QUERY_CET_SCORE"
            val shortcutInfoBuilder = ShortcutInfo.Builder(this, "shortcut_title_query_cet_score")
                    .setShortLabel(getString(R.string.shortcut_title_query_cet_score))
                    .setLongLabel(getString(R.string.shortcut_title_query_cet_score))
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_short_query_cet))
                    .setIntent(intent)
            shortcutManager.addDynamicShortcuts(listOf(shortcutInfoBuilder.build()))
        }
    }

    private fun initViewModel() {
        QueryCetScoreViewModelHelper.student.observe(this, studentObserver)
        QueryCetScoreViewModelHelper.cetVCodeLiveData.observe(this, cetVCodeObserver)
    }

    private fun removeObserver() {
        QueryCetScoreViewModelHelper.student.removeObserver(studentObserver)
        QueryCetScoreViewModelHelper.cetVCodeLiveData.removeObserver(cetVCodeObserver)
    }

    override fun monitor() {
        super.monitor()
        setupLayout.navigationBar.setNavigationBarListener(object : NavigationBar.NavigationBarListener {
            override fun onNavigateBack() {
                finish()
            }

            override fun onNavigateNext() {
                val scrollView = setupLayout.scrollView
                if (scrollView != null) {
                    if (scrollView.getChildAt(0).bottom <= scrollView.height + scrollView.scrollY) {
                        requestVCode()
                    } else {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObserver()
    }

    private fun requestVCode() {
        val student = QueryCetScoreViewModelHelper.student.value
        if (student == null) {
            toastLong(R.string.hint_action_not_login)
            return
        }
        cetNoEditText.error = null
        cetNameEditText.error = null

        val cetNoStr = cetNoEditText.text.toString()
        val cetNameStr = cetNameEditText.text.toString()

        var cancel = false
        var focusView: View? = null

        when {
            TextUtils.isEmpty(cetNoStr) -> {
                cetNoEditText.error = getString(R.string.error_field_required)
                focusView = cetNoEditText
                cancel = true
            }
            TextUtils.isEmpty(cetNameStr) -> {
                cetNameEditText.error = getString(R.string.error_field_required)
                focusView = cetNameEditText
                cancel = true
            }
        }

        if (cancel) {
            focusView?.requestFocus()
            return
        }
        QueryCetScoreViewModelHelper.no.postValue(cetNoStr)
        QueryCetScoreViewModelHelper.name.postValue(cetNameStr)
        QueryCetScoreViewModelHelper.getCetVCode(this)
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
