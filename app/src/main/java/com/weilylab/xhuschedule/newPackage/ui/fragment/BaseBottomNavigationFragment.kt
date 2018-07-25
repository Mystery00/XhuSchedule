package com.weilylab.xhuschedule.newPackage.ui.fragment

import vip.mystery0.tools.base.BaseFragment

abstract class BaseBottomNavigationFragment(layoutId: Int) : BaseFragment(layoutId) {
	abstract fun updateTitle()
}