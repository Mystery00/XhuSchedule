/*
 * Created by Mystery0 on 18-2-21 下午9:12.
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
 * Last modified 18-2-21 下午9:11
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
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.listener.ColorPickerChangeListener
import com.weilylab.xhuschedule.listener.InfoChangeListener
import android.graphics.Bitmap
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.graphics.drawable.VectorDrawableCompat
import android.view.LayoutInflater
import android.widget.*
import com.weilylab.xhuschedule.classes.baseClass.Exam
import com.weilylab.xhuschedule.classes.baseClass.ExpScore
import com.weilylab.xhuschedule.classes.baseClass.Score


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
        val text = course.name + if (course.type == Constants.COURSE_TYPE_NOT) "(非本周)" else ""
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
            val colorSharedPreference = context.getSharedPreferences(Constants.SHARED_PREFERENCE_COURSE_COLOR, Context.MODE_PRIVATE)
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

    fun buildNoDataView(context: Context, message: String): View {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_data_empty, ConstraintLayout(context), false)
        val textView: TextView = itemView.findViewById(R.id.textView)
        textView.text = message
        return itemView
    }

    fun buildExamItem(context: Context, exam: Exam, pointDrawable: VectorDrawableCompat): View {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_exam, ConstraintLayout(context), false)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val examNameTextView: TextView = itemView.findViewById(R.id.textView_exam_name)
        val examDateTextView: TextView = itemView.findViewById(R.id.textView_exam_date)
        val examTestNoTextView: TextView = itemView.findViewById(R.id.textView_exam_testno)
        val detailsTextView: TextView = itemView.findViewById(R.id.textView_details)

        examNameTextView.text = exam.name
        examDateTextView.text = exam.date
        examTestNoTextView.text = exam.testno
        val text = "\n" + context.getString(R.string.exam_no, exam.no) + "\n\n" +
                context.getString(R.string.exam_sname, exam.sname) + "\n\n" +
                context.getString(R.string.exam_location, exam.location) + "\n\n" +
                context.getString(R.string.exam_time, exam.time) + "\n\n" +
                context.getString(R.string.exam_testtype, exam.testtype) + "\n\n" +
                context.getString(R.string.exam_region, exam.region)
        detailsTextView.text = text
        imageView.setImageDrawable(pointDrawable)
        imageView.alpha = 0F
        return itemView
    }

    fun buildScoreItem(context: Context, score: Score, pointDrawable: VectorDrawableCompat): View {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_score, ConstraintLayout(context), false)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val scoreNameTextView: TextView = itemView.findViewById(R.id.textView_score_name)
        val scoreScoreTextView: TextView = itemView.findViewById(R.id.textView_score_score)
        val detailsTextView: TextView = itemView.findViewById(R.id.textView_details)

        scoreNameTextView.text = score.name
        scoreScoreTextView.text = score.score
        val text = "\n" + context.getString(R.string.score_no, score.no) + "\n\n" +
                context.getString(R.string.score_coursetype, score.coursetype) + "\n\n" +
                context.getString(R.string.score_credit, score.credit) + "\n\n" +
                context.getString(R.string.score_gpa, score.gpa)
        detailsTextView.text = text
        imageView.setImageDrawable(pointDrawable)
        imageView.alpha = 0F
        return itemView
    }

    fun buildExpScoreItem(context: Context, score: ExpScore, pointDrawable: VectorDrawableCompat): View {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_score, ConstraintLayout(context), false)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val scoreNameTextView: TextView = itemView.findViewById(R.id.textView_score_name)
        val scoreScoreTextView: TextView = itemView.findViewById(R.id.textView_score_score)
        val detailsTextView: TextView = itemView.findViewById(R.id.textView_details)

        scoreNameTextView.text = score.name
        scoreScoreTextView.text = score.score
        val text = "\n" + context.getString(R.string.score_no, score.no) + "\n\n" +
                context.getString(R.string.expscore_exptype, score.exptype) + "\n\n" +
                context.getString(R.string.score_credit, score.credit) + "\n\n" +
                context.getString(R.string.expscore_coursename, score.coursename)
        detailsTextView.text = text
        imageView.setImageDrawable(pointDrawable)
        imageView.alpha = 0F
        return itemView
    }

    /**
     * 根据指定的view截图
     *
     * @param view 要截图的view
     * @return Bitmap
     */
    fun getViewBitmap(view: View?): Bitmap? {
        if (null == view) {
            return null
        }
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        view.measure(View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY))
        view.layout(view.x.toInt(), view.y.toInt(), view.x.toInt() + view.measuredWidth, view.y.toInt() + view.measuredHeight)

        val bitmap = Bitmap.createBitmap(view.drawingCache, 0, 0, view.measuredWidth, view.measuredHeight)
        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return bitmap
    }
}