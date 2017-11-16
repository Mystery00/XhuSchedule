package com.weilylab.xhuschedule.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.fragment.SettingsPreferenceFragment
import kotlinx.android.synthetic.main.activity_settings.*
import vip.mystery0.tools.logs.Logs

class SettingsActivity : AppCompatActivity()
{
	companion object
	{
		private val TAG = "SettingsActivity"
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		Logs.i(TAG, "onCreate: ")
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_settings)
		fragmentManager.beginTransaction().replace(R.id.content_wrapper, SettingsPreferenceFragment()).commit()
		titleTextView.title = title
		titleTextView.setNavigationOnClickListener { finish() }
	}
}
