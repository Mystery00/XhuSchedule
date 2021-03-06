/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.activity

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.viewpager.widget.ViewPager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.ui.fragment.PlaceholderFragment
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import kotlinx.android.synthetic.main.activity_guide.*
import kotlin.math.roundToInt


@Suppress("DEPRECATION")
class GuideActivity : XhuBaseActivity(R.layout.activity_guide) {
    private val imageArray = arrayOf(R.mipmap.welcome1, R.mipmap.welcome2, R.mipmap.welcome3, R.mipmap.welcome4)
    private val sectionsPagerAdapter: SectionsPagerAdapter by lazy { SectionsPagerAdapter(supportFragmentManager) }
    private val grayPointDrawable: VectorDrawableCompat by lazy { VectorDrawableCompat.create(resources, R.drawable.ic_point, null)!! }
    private var distance = 0
    private var currentIndex = 0

    override fun inflateView(layoutId: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            super.inflateView(layoutId)
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            super.inflateView(layoutId)
        }
    }

    override fun initView() {
        super.initView()
        changeBackground(0)
    }

    override fun initData() {
        super.initData()
        grayPointDrawable.setBounds(0, 0, 20, 20)
        grayPointDrawable.setTint(Color.LTGRAY)

        container.adapter = sectionsPagerAdapter
        for (i in imageArray.indices) {
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
                val index = position % imageArray.size
                val leftMargin = distance * (index + positionOffset)
                val params = point.layoutParams as ConstraintLayout.LayoutParams

                params.leftMargin = leftMargin.roundToInt()
                point.layoutParams = params
            }

            override fun onPageSelected(position: Int) {
                changeBackground(position)
                currentIndex = position
                imageButtonPre.visibility = if (position == 0) View.GONE else View.VISIBLE
                imageButtonNext.visibility = if (position == imageArray.size - 1) View.GONE else View.VISIBLE
                buttonFinish.visibility = if (position == imageArray.size - 1) View.VISIBLE else View.GONE
            }
        })
    }

    private fun changeBackground(position: Int) {
        val resId = resources.getIdentifier("welcome${position + 1}_bg", "mipmap", packageName)
        imageViewBackground.setImageResource(resId)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(imageArray[position])
        }

        override fun getCount(): Int {
            return imageArray.size
        }
    }

    private fun go() {
        ConfigurationUtil.firstEnter = false
        startActivity(Intent(this, BottomNavigationActivity::class.java))
        finish()
    }
}
