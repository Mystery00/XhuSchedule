/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.custom

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StringRes

/**
 * 参考了部分代码：http://blog.csdn.net/yanzhenjie1003/article/details/51889239
 * Created by linlongxin on 2016/8/29.
 */

class SkipView : View {

	private val progressHintColor: Int = 0
	private var circleColor = Color.parseColor("#80000000")
	private var circleRadius = 100
	private var textColor = Color.WHITE
	private var progressColor = Color.WHITE
	private val progressWidth = 8f
	private var text: CharSequence = ""
	private var textSize = 35f
	private var totalTime = 1000L
	private var updateTime = 50L

	private var progress = 0
	private var nowTime = 0L
	private var isFinish = false

	private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
	private var mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
	private var mBounds = Rect()
	private var mArcRectF = RectF()

	private var mHandler = object : Handler(Looper.getMainLooper()) {
		override fun handleMessage(msg: Message) {
			super.handleMessage(msg)
			when (msg.what) {
				MSG_UPDATE -> {
					if (isFinish)
						return
					nowTime += updateTime
					progress = (nowTime / updateTime).toInt()
					postInvalidate()
					if (progress >= 100) {
						if (::listener.isInitialized)
							listener.invoke()
					} else
						sendEmptyMessageDelayed(msg.what, updateTime)
				}
			}
		}
	}
	private lateinit var listener: () -> Unit

	override fun setOnClickListener(l: OnClickListener?) {
		super.setOnClickListener {
			isFinish = true
			l?.onClick(it)
		}
	}

	constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val width = measuredWidth
		val height = measuredHeight
		circleRadius = if (width <= 0 || height <= 0) 100 else width.coerceAtMost(height) / 2
		setMeasuredDimension(circleRadius * 2, circleRadius * 2)
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		//找到view的边界
		getDrawingRect(mBounds)

		val centerX = mBounds.centerX()
		val centerY = mBounds.centerY()

		//画大圆
		mPaint.style = Paint.Style.FILL
		mPaint.color = circleColor
		canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), circleRadius.toFloat(), mPaint)

		//画外边框
		mPaint.style = Paint.Style.STROKE
		mPaint.strokeWidth = progressWidth
		mPaint.color = progressHintColor
		canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), circleRadius - progressWidth, mPaint)

		//画字
		mTextPaint.textSize = textSize
		mTextPaint.color = textColor
		mTextPaint.textAlign = Paint.Align.CENTER
		val textY = centerY - (mTextPaint.descent() + mTextPaint.ascent()) / 2
		canvas.drawText(text.toString(), centerX.toFloat(), textY, mTextPaint)

		//画进度条
		mPaint.strokeWidth = progressWidth
		mPaint.color = progressColor
		mPaint.strokeCap = Paint.Cap.ROUND
		mArcRectF.set(mBounds.left + progressWidth, mBounds.top + progressWidth, mBounds.right - progressWidth, mBounds.bottom - progressWidth)
		canvas.drawArc(mArcRectF, -90f, progress * 3.6f, false, mPaint)
	}

	fun setText(text: String): SkipView {
		this.text = text
		return this
	}

	fun setText(@StringRes resId: Int): SkipView = setText(context.getString(resId))

	fun setTotalTime(time: Long): SkipView {
		totalTime = time
		return this
	}

	fun setUpdateTime(updateTime: Long): SkipView {
		this.updateTime = updateTime
		return this
	}

	fun setProgressColor(@ColorInt color: Int): SkipView {
		progressColor = color
		return this
	}

	fun setCircleBackgroundColor(@ColorInt color: Int): SkipView {
		circleColor = color
		return this
	}

	fun setTextColor(@ColorInt color: Int): SkipView {
		textColor = color
		return this
	}

	fun setTextSize(textSize: Float): SkipView {
		this.textSize = textSize
		return this
	}

	fun setFinishAction(listener: () -> Unit): SkipView {
		this.listener = listener
		return this
	}

	fun start() {
		mHandler.sendEmptyMessageDelayed(MSG_UPDATE, updateTime)
	}

	companion object {
		private const val MSG_UPDATE = 10
	}
}