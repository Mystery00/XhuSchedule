/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-21 上午3:25
 */

package com.weilylab.xhuschedule.util

import android.content.Context
import android.graphics.*
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ColorPickerAdapter
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.listener.ColorPickerChangeListener
import com.weilylab.xhuschedule.listener.InfoChangeListener
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.content.Intent
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.widget.*
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.listener.InitProfileListener
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import java.util.*


/**
 * Created by myste.
 */
object ViewUtil {
    fun showAlertDialog(context: Context, course: Course, infoChangeListener: InfoChangeListener) {
        val view = View.inflate(context, R.layout.dialog_edit, null)
        val textView: TextView = view.findViewById(R.id.toolbar)
        val editTeacherLayout: TextView = view.findViewById(R.id.edit_teacher_layout)
        val editTimeLayout: TextView = view.findViewById(R.id.edit_time_layout)
        val editLocationLayout: LinearLayout = view.findViewById(R.id.edit_location_layout)
        val colorChooser: RecyclerView = view.findViewById(R.id.color_chooser)
        val floatingActionButton: FloatingActionButton = view.findViewById(R.id.floatingActionButton)
        val floatingActionButtonShare: FloatingActionButton = view.findViewById(R.id.floatingActionButtonShare)
        colorChooser.layoutManager = GridLayoutManager(context, 6)
        val adapter = ColorPickerAdapter(course.color, context)
        adapter.colorPickerChangeListener = object : ColorPickerChangeListener {
            override fun onColorChanged(color: String) {
                textView.setBackgroundColor(Color.parseColor(color))
                course.color = color
            }
        }
        colorChooser.adapter = adapter
        val text = course.name + if (course.type == "not") "(非本周)" else ""
        textView.text = text
        textView.setBackgroundColor(Color.parseColor(course.color))
        editTeacherLayout.text = course.teacher
        try {
            val startTime = context.resources.getStringArray(R.array.start_time)
            val endTime = context.resources.getStringArray(R.array.end_time)
            val time = course.time.trim().split("-")
            editTimeLayout.text = context.getString(R.string.course_time_format, startTime[time[0].toInt() - 1], endTime[time[1].toInt() - 1])
        } catch (e: Exception) {
            e.printStackTrace()
            editTimeLayout.text = course.time
        }
        CourseUtil.splitInfo(course).forEach {
            val child = TextView(context)
            child.text = context.getString(R.string.course_location_format, it.week, it.location)
            child.paint.isFakeBoldText = true
            editLocationLayout.addView(child)
        }
        val dialog = AlertDialog.Builder(context)
                .setView(view)
                .create()
        floatingActionButton.setOnClickListener {
            val colorSharedPreference = context.getSharedPreferences("course_color", Context.MODE_PRIVATE)
            val md5 = ScheduleHelper.getMD5(course.name)
            colorSharedPreference.edit()
                    .putString(md5, course.color)
                    .apply()
            infoChangeListener.onChange()
            dialog.dismiss()
        }
        floatingActionButtonShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, course.toString())
            shareIntent.type = "text/plain"
            //设置分享列表的标题，并且每次都显示分享列表
            context.startActivity(Intent.createChooser(shareIntent, "分享到"))
        }
        dialog.show()
    }

    fun setPopupView(context: Context, array: Array<String>, textView: TextView, listener: (position: Int) -> Unit) = setPopupView(context, array, textView, null, listener)

    fun setPopupView(context: Context, array: Array<String>, textView: TextView, @LayoutRes layout: Int?, listener: (position: Int) -> Unit) {
        val itemLayout = layout ?: R.layout.item_popup_view
        val termArrayAdapter = ArrayAdapter<String>(context, itemLayout, array)
        val termListView = ListView(context)
        termListView.setBackgroundColor(Color.WHITE)
        val termPopupWindow = PopupWindow(termListView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true)
        termPopupWindow.isFocusable = true
        termPopupWindow.isOutsideTouchable = true
        termPopupWindow.setOnDismissListener {
            termPopupWindow.dismiss()
        }
        termListView.adapter = termArrayAdapter
        termListView.setOnItemClickListener { _, _, position, _ ->
            if (textView.text.toString() != array[position]) {
                textView.text = array[position]
                listener(position)
            }
            termPopupWindow.dismiss()
        }
        textView.setOnClickListener {
            if (!termPopupWindow.isShowing)
                termPopupWindow.showAsDropDown(textView, 0, 10)
        }
    }

    fun initProfile(context: Context, student: Student, textViewYear: TextView, listener: InitProfileListener) {
        val initDialog = ZLoadingDialog(context)
                .setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE)
                .setHintText(context.getString(R.string.hint_dialog_init))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                .create()
        initDialog.show()
        if (student.profile != null) {
            try {
                val start = student.profile!!.grade.toInt()//进校年份
                val calendar = Calendar.getInstance()
                val end = when (calendar.get(Calendar.MONTH) + 1) {
                    in 1 until 3 -> calendar.get(Calendar.YEAR) - 1
                    in 3 until 9 -> calendar.get(Calendar.YEAR)
                    in 9 until 13 -> calendar.get(Calendar.YEAR) + 1
                    else -> 0
                }
                val yearArray = Array(end - start, { i -> (start + i).toString() + '-' + (start + i + 1).toString() })
                ViewUtil.setPopupView(context, yearArray, textViewYear, { position ->
                    listener.done(position, yearArray[position])
                })
                initDialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
                listener.error(initDialog)
            }
        } else {
            listener.error(initDialog)
        }
    }

    fun drawImg(course: Course): Bitmap {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        try {
            paint.color = Color.parseColor(course.color)
        } catch (e: Exception) {
            paint.color = Color.parseColor('#' + ScheduleHelper.getRandomColor())
        }
        canvas.drawCircle(100F, 100F, 100F, paint)
        return bitmap
    }

    fun blur(context: Context, bkg: Bitmap, view: View) {
        val radius = 20f
        val overlay = Bitmap.createBitmap(view.measuredWidth,
                view.measuredHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(overlay)
        canvas.translate(-view.left.toFloat(), -view.top.toFloat())
        canvas.drawBitmap(bkg, 0F, 0F, null)

        val rs = RenderScript.create(context)

        val overlayAlloc = Allocation.createFromBitmap(rs, overlay)
        val blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.element)
        blur.setInput(overlayAlloc)
        blur.setRadius(radius)
        blur.forEach(overlayAlloc)
        overlayAlloc.copyTo(overlay)
        view.background = BitmapDrawable(context.resources, overlay)
        rs.destroy()
    }

    fun getLight(bitmap: Bitmap, width: Int, height: Int): Int {
        var r: Int
        var g: Int
        var b: Int
        var number = 0
        var bright = 0.0
        var localTemp: Int?
        val x = arrayOf(0.27 * width, 0.36 * width, 0.5 * width, 0.65 * width, 0.75 * width)
        val y = arrayOf(0.82 * height, 0.94 * height)
        for (i in x.indices)
            for (j in y.indices) {
                number++
                localTemp = bitmap.getPixel(x[i].toInt(), y[j].toInt())
                r = localTemp or -0xff0001 shr 16 and 0x00ff
                g = localTemp or -0xff01 shr 8 and 0x0000ff
                b = localTemp or -0x100 and 0x0000ff

                bright += 0.299 * r + 0.587 * g + 0.114 * b
            }
        return (bright / number).toInt()
    }
}