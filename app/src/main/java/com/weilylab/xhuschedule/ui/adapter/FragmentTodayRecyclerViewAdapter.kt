/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.databinding.DialogShowJrscBinding
import com.weilylab.xhuschedule.databinding.ItemFragmentTodayBinding
import com.weilylab.xhuschedule.databinding.ItemFragmentTodayJrscBinding
import com.weilylab.xhuschedule.databinding.ItemFragmentTodayThingBinding
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.model.jrsc.PoetySentence
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.tools.base.binding.BaseMultiBindingRecyclerViewAdapter

class FragmentTodayRecyclerViewAdapter(private val context: Context) : BaseMultiBindingRecyclerViewAdapter() {
    val tempList = ArrayList<Any>()

    override fun setItemView(binding: ViewDataBinding, position: Int, data: Any) {
        when {
            (binding is ItemFragmentTodayJrscBinding) && (data is PoetySentence) -> {
                binding.point.setColorFilter(ColorPoolHelper.colorPool.poolInstance.random())
                binding.jrscTextView.text = data.data.content
                val text = "——${data.data.origin.author}《${data.data.origin.title}》"
                binding.jrscAuthorTextView.text = text
                binding.cardView.setOnClickListener {
                    val stringBuilder = StringBuilder()
                    data.data.origin.content.forEach { s -> stringBuilder.appendLine(s) }
                    val dialogShowJrscBinding = DialogShowJrscBinding.inflate(LayoutInflater.from(context))
                    val title = "《${data.data.origin.title}》"
                    dialogShowJrscBinding.title.text = title
                    val author = "[${data.data.origin.dynasty}] ${data.data.origin.author}"
                    dialogShowJrscBinding.author.text = author
                    dialogShowJrscBinding.content.text = stringBuilder.toString()
                    val translationStringBuilder = StringBuilder()
                    translationStringBuilder.append("诗词大意：")

                    if (data.data.origin.translate != null)
                        data.data.origin.translate?.let {
                            it.forEach { s -> translationStringBuilder.append(s) }
                        }
                    else
                        translationStringBuilder.append("暂无")
                    dialogShowJrscBinding.translation.text = translationStringBuilder.toString()
                    dialogShowJrscBinding.showTranslation = ConfigurationUtil.showJRSCTranslation
                    AlertDialog.Builder(context)
                            .setView(dialogShowJrscBinding.root)
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                }
            }
            (binding is ItemFragmentTodayBinding) && (data is Schedule) -> {
                binding.course = data
                val color = data.extras["colorInt"] as Int
                binding.point.setColorFilter(color)
                binding.imageView.setColorFilter(color)
                val startTimeArray = context.resources.getStringArray(R.array.start_time)
                val endTimeArray = context.resources.getStringArray(R.array.end_time)
                val courseTimeText = "${startTimeArray[data.start - 1]} - ${endTimeArray[data.start + data.step - 2]}"
                binding.textViewCourseTime.text = courseTimeText
                val time = "${data.start} - ${data.start + data.step - 1}节"
                binding.textViewTime.text = time
            }
            (binding is ItemFragmentTodayThingBinding) && (data is CustomThing) -> {
                binding.thing = data
                val color = Color.parseColor(data.color)
                binding.point.setColorFilter(color)
                binding.imageView.setColorFilter(color)
                val text = "${data.startTime} - ${data.endTime}"
                binding.textViewTime.text = text
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        return when (viewType) {
            VIEW_TYPE_JRSC -> BaseBindingViewHolder(DataBindingUtil.inflate<ItemFragmentTodayJrscBinding>(LayoutInflater.from(context), R.layout.item_fragment_today_jrsc, parent, false).root)
            VIEW_TYPE_COURSE -> BaseBindingViewHolder(DataBindingUtil.inflate<ItemFragmentTodayBinding>(LayoutInflater.from(context), R.layout.item_fragment_today, parent, false).root)
            VIEW_TYPE_THING -> BaseBindingViewHolder(DataBindingUtil.inflate<ItemFragmentTodayThingBinding>(LayoutInflater.from(context), R.layout.item_fragment_today_thing, parent, false).root)
            else -> throw Exception("what't fuck for this")
        }
    }

    override fun createBinding(viewType: Int): ViewDataBinding = DataBindingUtil.bind(View(context))!!

    override fun bindViewType(position: Int, data: Any): Int = when (items[position]) {
        is PoetySentence -> VIEW_TYPE_JRSC
        is Schedule -> VIEW_TYPE_COURSE
        is CustomThing -> VIEW_TYPE_THING
        else -> throw Exception("what't fuck for this")
    }

    companion object {
        const val VIEW_TYPE_JRSC = 1//今日诗词
        const val VIEW_TYPE_COURSE = 2//课程
        const val VIEW_TYPE_THING = 3//自定义事项
    }
}