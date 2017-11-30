/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午3:11
 */

package com.weilylab.xhuschedule.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Update
import com.weilylab.xhuschedule.interfaces.UpdateResponse
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.notification.UpdateNotification
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader
import java.net.UnknownHostException

/**
 * Created by myste.
 */
class InfoSettingsFragment : PreferenceFragment() {
    private lateinit var loadingDialog: ZLoadingDialog
    private lateinit var feedbackPreference: Preference
    private lateinit var checkUpdatePreference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_info)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        loadingDialog = ZLoadingDialog(activity)
                .setLoadingBuilder(Z_TYPE.SEARCH_PATH)
                .setHintText(getString(R.string.hint_dialog_check_update))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(activity, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
        feedbackPreference = findPreference(getString(R.string.key_feedback))
        checkUpdatePreference = findPreference(getString(R.string.key_check_update))
        feedbackPreference.setOnPreferenceClickListener {
            val stringBuilder = StringBuilder()
            stringBuilder.appendln("App Version: " + getString(R.string.app_version_name) + "-" + getString(R.string.app_version_code))
            stringBuilder.appendln("OS Version: " + Build.VERSION.RELEASE + "-" + Build.VERSION.SDK_INT)
            stringBuilder.appendln("Vendor: " + Build.MANUFACTURER)
            stringBuilder.appendln("Model: " + Build.MODEL)
            stringBuilder.appendln("Manufacture: " + Build.MANUFACTURER)
            stringBuilder.appendln("Brand: " + Build.BRAND)
            stringBuilder.appendln("Display: " + Build.DISPLAY)
            val data = Intent(Intent.ACTION_SENDTO)
            data.data = Uri.parse("mailto:mystery0dyl520@gmail.com")
            data.putExtra(Intent.EXTRA_SUBJECT, "西瓜课表意见反馈")
            data.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString())
            startActivity(data)
            true
        }
        checkUpdatePreference.setOnPreferenceClickListener {
            loadingDialog.show()
            ScheduleHelper.phpRetrofit
                    .create(UpdateResponse::class.java)
                    .checkUpdateCall(getString(R.string.app_version_code).toInt())
                    .subscribeOn(Schedulers.newThread())
                    .unsubscribeOn(Schedulers.newThread())
                    .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), Update::class.java) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Update> {
                        private lateinit var update: Update

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                            loadingDialog.dismiss()
                            e.printStackTrace()
                            if (e is UnknownHostException)
                                Toast.makeText(activity, R.string.error_network, Toast.LENGTH_SHORT)
                                        .show()
                            else
                                Toast.makeText(activity, "请求出错：" + e.message, Toast.LENGTH_SHORT)
                                        .show()
                        }

                        override fun onComplete() {
                            loadingDialog.dismiss()
                            if (update.code == 1)
                                UpdateNotification.notify(activity, update.version)
                            else
                                Toast.makeText(activity, update.message, Toast.LENGTH_SHORT)
                                        .show()
                        }

                        override fun onNext(update: Update) {
                            this.update = update
                        }
                    })
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}