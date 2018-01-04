/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-21 上午1:53
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.content.Intent
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.*
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.listener.FeedBackListener
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import java.io.File

class OperationAdapter(private val context: Context) : RecyclerView.Adapter<OperationAdapter.ViewHolder>() {
    private val list = ArrayList<HashMap<String, Int>>()

    init {
        val titleArray = arrayOf(
                R.string.operation_schedule,
                R.string.operation_exam,
                R.string.operation_score,
                R.string.operation_feedback,
                R.string.operation_logout,
                R.string.operation_settings
        )
        val imgArray = arrayOf(
                R.drawable.ic_schedule,
                R.drawable.ic_exam,
                R.drawable.ic_score,
                R.drawable.ic_feedback,
                R.drawable.ic_logout,
                R.drawable.ic_settings
        )
        for (i in 0 until titleArray.size) {
            val map = HashMap<String, Int>()
            map.put("title", titleArray[i])
            map.put("icon", imgArray[i])
            list.add(map)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val map = list[position]
        holder.imageView.setImageResource(map["icon"]!!)
        holder.textView.setText(map["title"]!!)
        holder.itemView.setOnClickListener {
            when (position) {
                0 -> context.startActivity(Intent(context, ScheduleActivity::class.java))
                1 -> context.startActivity(Intent(context, ExamActivity::class.java))
                2 -> context.startActivity(Intent(context, ScoreActivity::class.java))
                3 -> {
                    val loadingDialog = ZLoadingDialog(context)
                            .setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
                            .setHintText(context.getString(R.string.hint_dialog_feedback))
                            .setHintTextSize(16F)
                            .setCanceledOnTouchOutside(false)
                            .setLoadingColor(ContextCompat.getColor(context, R.color.colorAccent))
                            .setHintTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                            .create()
                    val layout = View.inflate(context, R.layout.dialog_feedback, null)
                    val emailInput: TextInputLayout = layout.findViewById(R.id.input_email)
                    val textInput: TextInputLayout = layout.findViewById(R.id.input_text)
                    val dialog = AlertDialog.Builder(context)
                            .setTitle(R.string.operation_feedback)
                            .setView(layout)
                            .setPositiveButton(android.R.string.ok, null)
                            .setNegativeButton(android.R.string.cancel, null)
                            .create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (emailInput.editText!!.text.toString().isEmpty() || textInput.editText!!.text.toString().isEmpty()) {
                            Toast.makeText(context, R.string.hint_feedback_empty, Toast.LENGTH_SHORT)
                                    .show()
                        } else {
                            loadingDialog.show()
                            val studentList = XhuFileUtil.getArrayFromFile(File(context.filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java)
                            var mainStudent: Student? = (0 until studentList.size)
                                    .firstOrNull { studentList[it].isMain }
                                    ?.let { studentList[it] }
                            if (mainStudent == null)
                                mainStudent = studentList[0]
                            mainStudent.feedback(context, emailInput.editText!!.text.toString(), textInput.editText!!.text.toString(), object : FeedBackListener {
                                override fun error(rt: Int, e: Throwable) {
                                    e.printStackTrace()
                                    loadingDialog.dismiss()
                                    Toast.makeText(context, context.getString(R.string.hint_feedback_error, rt, e.message), Toast.LENGTH_LONG)
                                            .show()
                                }

                                override fun done(rt: Int) {
                                    loadingDialog.dismiss()
                                    dialog.dismiss()
                                    Toast.makeText(context, R.string.hint_feedback, Toast.LENGTH_SHORT)
                                            .show()
                                }
                            })
                        }
                    }
                }
                4 -> {
                    AlertDialog.Builder(context)
                            .setTitle(R.string.hint_logout_title)
                            .setMessage(R.string.hint_logout_content)
                            .setPositiveButton(android.R.string.ok, { _, _ ->
                                val file = File(context.filesDir.absolutePath + File.separator + "data" + File.separator)
                                if (file.exists())
                                    file.listFiles()
                                            .forEach {
                                                it.delete()
                                            }
                                context.startActivity(Intent(context, LoginActivity::class.java))
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show()
                }
                5 -> context.startActivity(Intent(context, SettingsActivity::class.java))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_operation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var textView: TextView = itemView.findViewById(R.id.textView)
    }
}