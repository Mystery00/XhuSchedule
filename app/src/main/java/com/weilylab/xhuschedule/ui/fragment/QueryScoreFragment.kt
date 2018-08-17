package com.weilylab.xhuschedule.ui.fragment

import android.content.Intent
import android.preference.Preference
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.ui.activity.QueryClassScoreActivity
import com.weilylab.xhuschedule.ui.fragment.settings.BasePreferenceFragment

class QueryScoreFragment : BasePreferenceFragment(R.xml.preference_score) {
	private lateinit var queryClassScorePreference: Preference
	private lateinit var queryExperimentScorePreference: Preference
	private lateinit var queryCETScorePreference: Preference

	override fun initPreference() {
		super.initPreference()
		queryClassScorePreference = findPreferenceById(R.string.key_query_score_class)
		queryExperimentScorePreference = findPreferenceById(R.string.key_query_score_experiment)
		queryCETScorePreference = findPreferenceById(R.string.key_query_score_cet)
	}

	override fun monitor() {
		super.monitor()
		queryClassScorePreference.setOnPreferenceClickListener {
			startActivity(Intent(activity, QueryClassScoreActivity::class.java))
			true
		}
		queryExperimentScorePreference.setOnPreferenceClickListener {
			true
		}
		queryCETScorePreference.setOnPreferenceClickListener {
			true
		}
	}
}