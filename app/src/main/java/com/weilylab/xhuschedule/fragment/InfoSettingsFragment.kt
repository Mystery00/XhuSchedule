package com.weilylab.xhuschedule.fragment

import android.content.Intent
import android.net.Uri
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
import com.weilylab.xhuschedule.util.UpdateNotification
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
    private lateinit var checkUpdatePreference: Preference
    private lateinit var weilyProductPreference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_info)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        loadingDialog = ZLoadingDialog(activity)
                .setLoadingBuilder(Z_TYPE.SEARCH_PATH)
                .setHintText(getString(R.string.hint_dialog_check_update))
                .setHintTextSize(16F)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(activity, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
        checkUpdatePreference = findPreference(getString(R.string.key_check_update))
        weilyProductPreference = findPreference(getString(R.string.key_weily_product))
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
        weilyProductPreference.setOnPreferenceClickListener {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://weilylab.com")))
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}