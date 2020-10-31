/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

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
    val hint_null_student = getString(R.string.hint_null_student)

    private fun getString(@StringRes id: Int): String = context.getString(id)
}