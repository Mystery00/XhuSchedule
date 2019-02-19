package com.weilylab.xhuschedule.ui.custom

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FloatingActionButtonScrollBehavior : FloatingActionButton.Behavior() {
	override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton,
									 directTargetChild: View, target: View, nestedScrollAxes: Int, type: Int): Boolean {
		// Ensure we react to vertical scrolling
		return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes, type)
	}

	override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton,
								target: View, dxConsumed: Int, dyConsumed: Int,
								dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
		if (dyConsumed > 0 && child.visibility == View.VISIBLE) {
			// User scrolled down and the FAB is currently visible -> hide the FAB
			child.hide()
		} else if (dyConsumed < 0 && child.visibility != View.VISIBLE) {
			// User scrolled up and the FAB is currently not visible -> show the FAB
			child.show()
		}
	}
}