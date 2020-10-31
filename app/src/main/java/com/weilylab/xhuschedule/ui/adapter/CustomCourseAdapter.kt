/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemListCustomCourseBinding
import com.weilylab.xhuschedule.databinding.ItemListCustomCourseGroupBinding
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.utils.userDo.CourseUtil
import vip.mystery0.tools.base.binding.BaseMultiBindingRecyclerViewAdapter

class CustomCourseAdapter(private val context: Context) : BaseMultiBindingRecyclerViewAdapter() {
    private var clickListener: ((Course) -> Unit)? = null
    private val expandList = ArrayList<String>()
    private val map = HashMap<String, ArrayList<Course>>()
    private var arrowAnimation: ObjectAnimator? = null

    companion object {
        private const val VIEW_GROUP = 1
        private const val VIEW_ITEM = 2
    }

    @Suppress("UNCHECKED_CAST")
    fun updateMap() {
        map.clear()
        val key = items.filterIsInstance<String>()
        expandList.clear()
        expandList.addAll(key)
        key.forEach { k ->
            val list = map[k]
            if (list != null)
                list.clear()
            else
                map[k] = ArrayList()
            map[k]?.addAll(items.filter { (it is Course) && (it.studentID == k) } as List<Course>)
            map[k]?.sortBy { it.name }
        }
    }

    override fun setItemView(binding: ViewDataBinding, position: Int, data: Any) {
        when {
            (binding is ItemListCustomCourseGroupBinding) && (data is String) -> {
                binding.textViewUsername.text = data
                arrowAnimation?.cancel()
                arrowAnimation = if (!expandList.contains(data))
                    ObjectAnimator.ofFloat(binding.imageView, "rotation", 180F, 0F).setDuration(10)
                else
                    ObjectAnimator.ofFloat(binding.imageView, "rotation", 0F, 180F).setDuration(10)
                arrowAnimation?.start()
                binding.root.setOnClickListener {
                    arrowAnimation?.cancel()
                    if (expandList.contains(data)) {//收起
                        arrowAnimation = ObjectAnimator.ofFloat(binding.imageView, "rotation", 180F, 0F)
                        arrowAnimation?.start()
                        expandList.remove(data)
                        map[data]!!.forEach {
                            notifyItemRemoved(items.indexOf(it))
                        }
                        items.removeAll(map[data]!!)
                    } else {//展开
                        arrowAnimation = ObjectAnimator.ofFloat(binding.imageView, "rotation", 0F, 180F)
                        arrowAnimation?.start()
                        expandList.add(data)
                        items.addAll(position + 1, map[data]!!)
                        map[data]!!.forEach {
                            notifyItemInserted(items.indexOf(it))
                        }
                    }
                }
            }
            (binding is ItemListCustomCourseBinding) && (data is Course) -> {
                binding.course = data
                binding.root.backgroundTintList = ColorStateList.valueOf(Color.parseColor(data.color))
                val weekText = context.getString(R.string.prompt_custom_course_week_, CourseUtil.splitWeekString(data.week.split(",").map { it.toInt() }))
                binding.textViewWeek.text = weekText
                val timeText = context.getString(R.string.prompt_custom_course_time_, data.time)
                binding.textViewTime.text = timeText
                binding.root.setOnClickListener { clickListener?.invoke(data) }
            }
        }
    }

    override fun bindViewType(position: Int, data: Any): Int = if (data is String) VIEW_GROUP else VIEW_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        return when (viewType) {
            VIEW_GROUP -> BaseBindingViewHolder(DataBindingUtil.inflate<ItemListCustomCourseGroupBinding>(LayoutInflater.from(context), R.layout.item_list_custom_course_group, parent, false).root)
            VIEW_ITEM -> BaseBindingViewHolder(DataBindingUtil.inflate<ItemListCustomCourseBinding>(LayoutInflater.from(context), R.layout.item_list_custom_course, parent, false).root)
            else -> throw Exception("what't fuck for this")
        }
    }

    override fun createBinding(viewType: Int): ViewDataBinding = DataBindingUtil.bind(View(context))!!


    fun setOnClickListener(listener: (Course) -> Unit) {
        this.clickListener = listener
    }

    fun release() {
        arrowAnimation?.cancel()
        map.keys.forEach {
            map[it]!!.clear()
        }
        map.clear()
    }
}