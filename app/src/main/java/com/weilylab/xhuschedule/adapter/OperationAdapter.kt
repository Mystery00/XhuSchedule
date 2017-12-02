/*
 * Created by Mystery0 on 17-12-1 下午9:57.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-1 下午9:57
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.LoginActivity
import com.weilylab.xhuschedule.activity.MainActivity
import com.weilylab.xhuschedule.activity.SettingsActivity
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
                R.drawable.ic_week,
                R.drawable.ic_week,
                R.drawable.ic_week,
                R.drawable.ic_week,
                R.drawable.ic_week,
                R.drawable.ic_week
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
                0 -> {

                }
                1 -> {

                }
                2 -> {

                }
                3 -> {
                    val stringBuilder = StringBuilder()
                    stringBuilder.appendln("App Version: " + context.getString(R.string.app_version_name) + "-" + context.getString(R.string.app_version_code))
                    stringBuilder.appendln("OS Version: " + Build.VERSION.RELEASE + "-" + Build.VERSION.SDK_INT)
                    stringBuilder.appendln("Vendor: " + Build.MANUFACTURER)
                    stringBuilder.appendln("Model: " + Build.MODEL)
                    stringBuilder.appendln("Manufacture: " + Build.MANUFACTURER)
                    stringBuilder.appendln("Brand: " + Build.BRAND)
                    stringBuilder.appendln("Display: " + Build.DISPLAY)
                    val data = Intent(Intent.ACTION_SENDTO)
                    data.data = Uri.parse("mailto:mystery0dyl520@gmail.com")
                    data.putExtra(Intent.EXTRA_SUBJECT, "西瓜课表意见反馈")
                    data.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString())
                    context.startActivity(data)
                }
                4 -> {
                    val file = File(context.filesDir.absolutePath + File.separator + "data" + File.separator)
                    if (file.exists())
                        file.listFiles()
                                .forEach {
                                    it.delete()
                                }
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    (context as MainActivity).finish()
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