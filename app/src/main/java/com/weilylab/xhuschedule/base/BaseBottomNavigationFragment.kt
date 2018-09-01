package com.weilylab.xhuschedule.base

import android.widget.Toast
import androidx.databinding.ViewDataBinding
import vip.mystery0.tools.base.binding.BaseBindingFragment

abstract class BaseBottomNavigationFragment<B : ViewDataBinding>(layoutId: Int) : BaseBindingFragment<B>(layoutId) {
	private var toast: Toast? = null

	abstract fun updateTitle()

	fun toastMessage(message: String?, isShowLong: Boolean = false) {
		toast?.cancel()
		toast = Toast.makeText(activity!!, message, if (isShowLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
		toast?.show()
	}
}