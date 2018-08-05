package com.weilylab.xhuschedule.newPackage.ui.fragment

import android.widget.Toast
import vip.mystery0.tools.base.BaseFragment

abstract class BaseBottomNavigationFragment(layoutId: Int) : BaseFragment(layoutId) {
	private var toast: Toast? = null

	abstract fun updateTitle()

	fun toastMessage(message: String?, isShowLong: Boolean = false) {
		toast?.cancel()
		toast = Toast.makeText(activity!!, message, if (isShowLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
		toast?.show()
	}
}