/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.base

import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.weilylab.xhuschedule.R
import vip.mystery0.tools.base.binding.BaseBindingFragment

abstract class BaseBottomNavigationFragment<B : ViewDataBinding>(layoutId: Int) : BaseBindingFragment<B>(layoutId) {
    private var snackbar: Snackbar? = null
    abstract fun updateTitle()

    fun toast(throwable: Throwable?) = toast(throwable?.message)
    fun toastLong(throwable: Throwable?) = toastLong(throwable?.message)

    fun snackbar(@StringRes stringRes: Int) = snackBarMessage(stringRes, {}, Snackbar.LENGTH_SHORT)
    fun snackbar(message: String) = snackBarMessage(message, {}, Snackbar.LENGTH_SHORT)
    fun snackbarLong(@StringRes stringRes: Int) = snackBarMessage(stringRes, {}, Snackbar.LENGTH_LONG)
    fun snackbarLong(message: String) = snackBarMessage(message, {}, Snackbar.LENGTH_LONG)
    fun snackBarMessage(@StringRes stringId: Int, doOther: Snackbar.() -> Unit, @BaseTransientBottomBar.Duration duration: Int) = snackBarMessage(getString(stringId), doOther, duration)
    fun snackBarMessage(message: String, doOther: Snackbar.() -> Unit, @BaseTransientBottomBar.Duration duration: Int) {
        snackbar?.dismiss()
        snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), message, duration)
        doOther.invoke(snackbar!!)
        snackbar!!.show()
    }
}