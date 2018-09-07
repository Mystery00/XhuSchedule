package com.weilylab.xhuschedule.constant

import androidx.annotation.StringRes
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.APP

object StringConstant {
	private val context = APP.instance

	val hint_network_error = getString(R.string.hint_network_error)
	val hint_data_null = getString(R.string.hint_data_null)
	val hint_do_too_many = getString(R.string.hint_do_too_many)
	val hint_student_logged = getString(R.string.hint_student_logged)
	val hint_feedback_null_student = getString(R.string.hint_feedback_null_student)

	private fun getString(@StringRes id: Int): String = context.getString(id)
}