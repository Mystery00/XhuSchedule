/*
 * Created by Mystery0 on 18-2-15 上午1:02.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-2-15 上午1:02
 */

package com.weilylab.xhuschedule.view

import android.content.Context
import android.text.Spannable
import android.text.method.PasswordTransformationMethod
import android.text.method.HideReturnsTransformationMethod
import android.view.MotionEvent
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.Selection
import android.util.AttributeSet
import android.widget.EditText
import com.weilylab.xhuschedule.R


class TogglePasswordVisibilityEditText : EditText {

    //切换drawable的引用
    private var visibilityDrawable: Drawable? = null
    private var visibility = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.editTextStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {
        //获得该EditText的left ,top ,right,bottom四个方向的drawable
        val compoundDrawables = compoundDrawables
        visibilityDrawable = compoundDrawables[2]
        if (visibilityDrawable == null) {
            visibilityDrawable=ContextCompat.getDrawable(context,R.drawable.design_ic_visibility_off)
        }
    }

    /**
     * 用按下的位置来模拟点击事件
     * 当按下的点的位置 在  EditText的宽度 - (图标到控件右边的间距 + 图标的宽度)  和
     * EditText的宽度 - 图标到控件右边的间距 之间就模拟点击事件，
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_UP) {

            if (compoundDrawables[2] != null) {
                var xFlag = false
                val yFlag = false
                //得到用户的点击位置，模拟点击事件
                xFlag = event.x > width - (visibilityDrawable!!.intrinsicWidth + compoundPaddingRight) && event.x < width - (totalPaddingRight - compoundPaddingRight)

                if (xFlag) {
                    visibility = !visibility
                    if (visibility) {
                        visibilityDrawable = resources.getDrawable(R.drawable.ic_visibility_on)
                        /*this.setInputType(EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);*/

                        this.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    } else {
                        //隐藏密码
                        visibilityDrawable = resources.getDrawable(R.drawable.ic_visibility_off)
                        //this.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                        this.transformationMethod = PasswordTransformationMethod.getInstance()
                    }

                    //将光标定位到指定的位置
                    val text = this.text
                    if (text is Spannable) {
                        val spanText = text as Spannable
                        Selection.setSelection(spanText, text.length)
                    }
                    //调用setCompoundDrawables方法时，必须要为drawable指定大小，不然不会显示在界面上
                    visibilityDrawable!!.setBounds(0, 0, visibilityDrawable!!.minimumWidth,
                            visibilityDrawable!!.minimumHeight)
                    setCompoundDrawables(compoundDrawables[0],
                            compoundDrawables[1], visibilityDrawable, compoundDrawables[3])
                }
            }
        }
        return super.onTouchEvent(event)
    }
}//指定了默认的style属性