/*
 * Created by Mystery0 on 6/20/18 7:33 PM.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 6/20/18 7:33 PM
 */

package com.weilylab.xhuschedule.newPackage.ui.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.viewpager.widget.ViewPager
import android.view.*
import android.view.animation.Animation
import android.widget.LinearLayout

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.ui.fragment.PlaceholderFragment
import com.weilylab.xhuschedule.newPackage.base.XhuBaseActivity
import com.weilylab.xhuschedule.newPackage.utils.ConfigurationUtil
import kotlinx.android.synthetic.main.activity_guide.*
import vip.mystery0.tools.utils.DensityTools

class GuideActivity : XhuBaseActivity(R.layout.activity_guide) {
	private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
	private lateinit var grayPointDrawable: VectorDrawableCompat
	private lateinit var gestureDetector: GestureDetector
	private var distance = 0
	private var flaggingWidth = 0
	private var currentIndex = 0
	private var leftAnimation: ObjectAnimator? = null
	private var rightAnimation: ObjectAnimator? = null
	private var buttonAnimation: ObjectAnimator? = null

	override fun inflateView(layoutId: Int) {
		window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
		super.inflateView(layoutId)
	}

	override fun initData() {
		super.initData()
		gestureDetector = GestureDetector(this, GuideViewTouch())
		flaggingWidth = DensityTools.getScreenWidth(this) / 3

		grayPointDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_point, null)!!
		grayPointDrawable.setBounds(0, 0, 20, 20)
		grayPointDrawable.setTint(Color.GRAY)

		mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

		container.adapter = mSectionsPagerAdapter
		for (i in 0..2) {
			val view = View(applicationContext)
			view.background = grayPointDrawable
			val params = LinearLayout.LayoutParams(20, 20)
			if (i != 0)
				params.leftMargin = 20
			view.layoutParams = params
			pointLayout.addView(view)
		}
		val pointParams = point.layoutParams
		pointParams.height = 20
		pointParams.width = 20
		point.layoutParams = pointParams
	}

	override fun monitor() {
		super.monitor()
		imageButtonPre.setOnClickListener {
			currentIndex--
			container.setCurrentItem(currentIndex, true)
		}
		imageButtonNext.setOnClickListener {
			currentIndex++
			container.setCurrentItem(currentIndex, true)
		}
		buttonFinish.setOnClickListener {
			go()
		}

		point.viewTreeObserver.addOnGlobalLayoutListener {
			distance = pointLayout.getChildAt(1).left - pointLayout.getChildAt(0).left
		}

		container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
			override fun onPageScrollStateChanged(state: Int) {
			}

			override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
				val index = position % 3
				val leftMargin = distance * (index + positionOffset)
				val params = point.layoutParams as ConstraintLayout.LayoutParams

				params.leftMargin = Math.round(leftMargin)
				point.layoutParams = params
			}

			override fun onPageSelected(position: Int) {
				currentIndex = position
//				when (position) {
//					0 -> {
//						if ()
//						leftAnimation?.cancel()
//						leftAnimation = ObjectAnimator.ofFloat(imageButtonPre, "alpha", 1F, 0F)
//						leftAnimation!!.addListener(object : Animator.AnimatorListener {
//							override fun onAnimationRepeat(p0: Animator?) {
//							}
//
//							override fun onAnimationEnd(p0: Animator?) {
//								imageButtonPre.visibility = View.GONE
//							}
//
//							override fun onAnimationCancel(p0: Animator?) {
//							}
//
//							override fun onAnimationStart(p0: Animator?) {
//							}
//						})
//						leftAnimation!!.start()
//					}
//					1 -> {
//
//					}
//					2 -> {
//
//					}
//				}
				imageButtonPre.visibility = if (position == 0) View.GONE else View.VISIBLE
				imageButtonNext.visibility = if (position == 2) View.GONE else View.VISIBLE
				buttonFinish.visibility = if (position == 2) View.VISIBLE else View.GONE
			}
		})
	}

	override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
		if (gestureDetector.onTouchEvent(ev))
			ev.action = MotionEvent.ACTION_CANCEL
		return super.dispatchTouchEvent(ev)
	}

	inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
		private val imageArray = arrayOf(R.mipmap.welcome1, R.mipmap.welcome2, R.mipmap.welcome3)

		override fun getItem(position: Int): Fragment {
			return PlaceholderFragment.newInstance(imageArray[position])
		}

		override fun getCount(): Int {
			return 3
		}
	}

	inner class GuideViewTouch : GestureDetector.SimpleOnGestureListener() {
		override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
			if (container.currentItem == 2) {
				if ((Math.abs(e1.x - e2.x) > Math.abs(e1.y - e2.y)) &&
						(e1.x - e2.x <= (-flaggingWidth) || (e1.x - e2.x >= flaggingWidth)))
					if (e1.x - e2.x >= flaggingWidth) {
						go()
						return true
					}
			}
			return false
		}
	}

	private fun go() {
		ConfigurationUtil.firstEnter = false
		startActivity(Intent(this, BottomNavigationActivity::class.java))
		finish()
	}
}
