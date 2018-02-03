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
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.FirebaseConstant
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.TestUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import vip.mystery0.tools.logs.Logs

class DebugFragment : BasePreferenceFragment() {
    private lateinit var debugModePreference: SwitchPreference
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
                .setHintText(getString(R.string.hint_dialog_connect_firebase))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(activity, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
                .create()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        debugModePreference = findPreference(R.string.key_debug_enable) as SwitchPreference
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

        debugModePreference.setOnPreferenceChangeListener { _, _ ->
            val debugMode = !debugModePreference.isChecked
            Settings.debugMode = debugMode
            debugCrashlyticsPreference.isEnabled = debugMode
            debugRealtimeDatabasePreference.isEnabled = debugMode
            debugStoragePreference.isEnabled = debugMode
            debugFCMPreference.isEnabled = debugMode
            true
        }
        debugCrashlyticsPreference.setOnPreferenceClickListener {
            Crashlytics.getInstance().crash()
            true
        }
        debugRealtimeDatabasePreference.setOnPreferenceClickListener {
            debugFirebaseDialog.show()
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val testReference = firebaseDatabase.getReference(FirebaseConstant.TEST)
            testReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError?) {
                    debugFirebaseDialog.dismiss()
                    Toast.makeText(activity, "Firebase Realtime Database在您的设备上可能不可用！", Toast.LENGTH_LONG)
                            .show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    debugFirebaseDialog.dismiss()
                    val code = dataSnapshot.child("code").value
                    val value = dataSnapshot.child("message").value
                    Toast.makeText(activity, "接收到数据：code=>$code message=>$value", Toast.LENGTH_SHORT)
                            .show()
                    if (code != 233)
                        Toast.makeText(activity, "Firebase Realtime Database在您的设备上可能不可用！", Toast.LENGTH_LONG)
                                .show()
                    else
                        Toast.makeText(activity, "恭喜您，Firebase Realtime Database在您的设备上可用！", Toast.LENGTH_LONG)
                                .show()
                }
            })
            true
        }
        debugStoragePreference.setOnPreferenceClickListener {
            debugFirebaseDialog.show()
            val firebaseStorage = FirebaseStorage.getInstance()
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