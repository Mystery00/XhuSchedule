/*
 * Created by Mystery0 on 18-2-2 下午10:16.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-2-2 下午10:16
 */

package com.weilylab.xhuschedule.fragment

import android.app.Dialog
import android.os.Bundle
import android.preference.Preference
import android.preference.SwitchPreference
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.FirebaseConstant
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.TempSharedPreferenceUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs
import java.util.*

class DebugFragment : BasePreferenceFragment() {
    companion object {
        private const val TAG = "DebugFragment"
    }

    private lateinit var debugModePreference: SwitchPreference
    private lateinit var disableFirebasePreference: SwitchPreference
    private lateinit var debugCrashlyticsPreference: Preference
    private lateinit var debugRealtimeDatabasePreference: Preference
    private lateinit var debugStoragePreference: Preference
    private lateinit var debugFCMPreference: Preference
    private lateinit var debugFirebaseDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_debug)
        debugFirebaseDialog = ZLoadingDialog(activity)
                .setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
                .setHintText(getString(R.string.hint_dialog_debug_firebase))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(activity, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
                .create()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        debugModePreference = findPreference(R.string.key_debug_enable) as SwitchPreference
        disableFirebasePreference = findPreference(R.string.key_disable_firebase) as SwitchPreference
        debugCrashlyticsPreference = findPreference(R.string.key_debug_crash)
        debugRealtimeDatabasePreference = findPreference(R.string.key_debug_real_time_database)
        debugStoragePreference = findPreference(R.string.key_debug_storage)
        debugFCMPreference = findPreference(R.string.key_debug_fcm)

        debugModePreference.isChecked = Settings.debugMode
        if (!Settings.debugMode) {
            debugCrashlyticsPreference.isEnabled = false
            debugRealtimeDatabasePreference.isEnabled = false
            debugStoragePreference.isEnabled = false
            debugFCMPreference.isEnabled = false
        }
        disableFirebasePreference.isChecked = TempSharedPreferenceUtil.disableFirebase


        debugModePreference.setOnPreferenceChangeListener { _, _ ->
            val debugMode = !debugModePreference.isChecked
            Settings.debugMode = debugMode
            debugCrashlyticsPreference.isEnabled = debugMode
            debugRealtimeDatabasePreference.isEnabled = debugMode
            debugStoragePreference.isEnabled = debugMode
            debugFCMPreference.isEnabled = debugMode
            true
        }
        disableFirebasePreference.setOnPreferenceChangeListener { _, _ ->
            TempSharedPreferenceUtil.disableFirebase = !disableFirebasePreference.isChecked
            true
        }
        debugCrashlyticsPreference.setOnPreferenceClickListener {
            Crashlytics.getInstance().crash()
            true
        }
        debugRealtimeDatabasePreference.setOnPreferenceClickListener {
            Observable.create<Boolean> {
                var isFinish = false
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        val firebaseDatabase = if (APP.getFirebaseApp() != null)
                            FirebaseDatabase.getInstance(APP.getFirebaseApp())
                        else
                            FirebaseDatabase.getInstance()
                        val testReference = firebaseDatabase.getReference(FirebaseConstant.TEST)
                        testReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(databaseError: DatabaseError?) {
                                it.onNext(false)
                                it.onComplete()
                                isFinish = true
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val code = dataSnapshot.child("code").value as Int
                                val message = dataSnapshot.child("message").value as String
                                Logs.i(TAG, "debugRealtimeDatabasePreference: code=>$code message=>$message")
                                it.onNext(code == 233)
                                it.onComplete()
                                isFinish = true
                            }
                        })
                    }
                }, 0)
                Thread.sleep(20000)
                if (!isFinish) {
                    timer.cancel()
                    it.onNext(false)
                    it.onComplete()
                }
            }
                    .subscribeOn(Schedulers.newThread())
                    .unsubscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Boolean> {
                        private var canUse = false
                        override fun onComplete() {
                            debugFirebaseDialog.dismiss()
                            if (canUse)
                                Toast.makeText(activity, "恭喜您，Firebase Realtime Database在您的设备上可用！", Toast.LENGTH_LONG)
                                        .show()
                            else
                                Toast.makeText(activity, "Firebase Realtime Database在您的设备上可能不可用！", Toast.LENGTH_LONG)
                                        .show()
                        }

                        override fun onSubscribe(d: Disposable) {
                            debugFirebaseDialog.show()
                        }

                        override fun onNext(t: Boolean) {
                            canUse = t
                        }

                        override fun onError(e: Throwable) {
                            debugFirebaseDialog.dismiss()
                            Logs.wtf(TAG, "onError: ", e)
                            Toast.makeText(activity, "Firebase Realtime Database在您的设备上可能不可用！", Toast.LENGTH_LONG)
                                    .show()
                        }
                    })
            true
        }
        debugStoragePreference.setOnPreferenceClickListener {
            debugFirebaseDialog.show()
            val firebaseStorage = if (APP.getFirebaseApp() != null)
                FirebaseStorage.getInstance(APP.getFirebaseApp()!!)
            else
                FirebaseStorage.getInstance()
            val testFile = firebaseStorage.getReference("test/ThisIsTestFile")
            testFile.getBytes(1024 * 1024).addOnCompleteListener { bytes ->
                Logs.i("TAG", "Byte[]: $bytes")
            }.addOnFailureListener { exception ->
                        Logs.i("TAG", "Exception: $exception")
                    }
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}