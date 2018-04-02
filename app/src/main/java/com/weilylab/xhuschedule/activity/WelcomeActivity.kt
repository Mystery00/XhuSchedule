/*
 * Created by Mystery0 on 18-3-2 上午4:02.
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
 * Last modified 18-3-2 上午4:02
 */

package com.weilylab.xhuschedule.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.constraint.ConstraintLayout
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.view.ViewPager
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.fragment.PlaceholderFragment
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.Settings
import kotlinx.android.synthetic.main.activity_welcome.*
import vip.mystery0.tools.utils.DensityTools

class WelcomeActivity : XhuBaseActivity() {

	private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
	private lateinit var grayPointDrawable: VectorDrawableCompat
	private lateinit var gestureDetector: GestureDetector
	private var distance = 0
	private var flaggingWidth = 0

	override fun initView() {
		super.initView()
		setContentView(R.layout.activity_welcome)
	}

	override fun initData() {
		super.initData()
		gestureDetector = GestureDetector(this, GuideViewTouch())
		flaggingWidth = DensityTools.getScreenWidth(this) / 3

		grayPointDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_point, null)!!
		grayPointDrawable.setBounds(0, 0, 20, 20)
		grayPointDrawable.setTint(Color.GRAY)

		mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

		viewpager.adapter = mSectionsPagerAdapter
		for (i in 0..2) {
			val view = View(applicationContext)
			view.background = grayPointDrawable
			val params = LinearLayout.LayoutParams(20, 20)
			if (i != 0)
				params.leftMargin = 20
			view.layoutParams = params
			pointLayout.addView(view)
		}
		val pointParams = redPoint.layoutParams
		pointParams.height = 20
		pointParams.width = 20
		redPoint.layoutParams = pointParams
	}

	override fun monitor() {
		super.monitor()
		enterButton.setOnClickListener {
			go()
		}

		redPoint.viewTreeObserver.addOnGlobalLayoutListener {
			distance = pointLayout.getChildAt(1).left - pointLayout.getChildAt(0).left
		}

		viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
			override fun onPageScrollStateChanged(state: Int) {
			}

			override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
				val index = position % 3
				val leftMargin = distance * (index + positionOffset)
				val params = redPoint.layoutParams as ConstraintLayout.LayoutParams

				params.leftMargin = Math.round(leftMargin)
				redPoint.layoutParams = params
			}

			override fun onPageSelected(position: Int) {
				if (enterButton.visibility == View.GONE && position == 2) {
					enterButton.visibility = View.VISIBLE
					ObjectAnimator.ofFloat(enterButton, Constants.ANIMATION_ALPHA, 0F, 1F).start()
				} else {
					val animator = ObjectAnimator.ofFloat(enterButton, Constants.ANIMATION_ALPHA, 1F, 0F)
					animator.addListener(object : Animator.AnimatorListener {
						override fun onAnimationRepeat(animation: Animator?) {
						}

						override fun onAnimationEnd(animation: Animator?) {
							enterButton.visibility = View.GONE
						}

						override fun onAnimationCancel(animation: Animator?) {
						}

						override fun onAnimationStart(animation: Animator?) {
						}
					})
					animator.start()
				}
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
			if (viewpager.currentItem == 2) {
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
		Settings.isFirstEnter = false
		startActivity(Intent(this, MainActivity::class.java))
		finish()
	}
}
