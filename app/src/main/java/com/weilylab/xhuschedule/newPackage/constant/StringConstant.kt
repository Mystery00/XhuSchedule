package com.weilylab.xhuschedule.newPackage.constant

import androidx.annotation.StringRes
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.config.APP

object StringConstant {
	private val context = APP.instance

	val hint_network_error = getString(R.string.hint_network_error)
	val hint_data_null = getString(R.string.hint_data_null)
	val hint_do_too_many = getString(R.string.hint_do_too_many)

	private fun getString(@StringRes id: Int): String = context.getString(id)
}