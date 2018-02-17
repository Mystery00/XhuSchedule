/*
 * Created by Mystery0 on 18-1-12 下午8:52.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                  =====================================================
 *                  =                                                   =
 *                  =                       _oo0oo_                     =
 *                  =                      o8888888o                    =
 *                  =                      88" . "88                    =
 *                  =                      (| -_- |)                    =
 *                  =                      0\  =  /0                    =
 *                  =                    ___/`---'\___                  =
 *                  =                  .' \\|     |# '.                 =
 *                  =                 / \\|||  :  |||# \                =
 *                  =                / _||||| -:- |||||- \              =
 *                  =               |   | \\\  -  #/ |   |              =
 *                  =               | \_|  ''\---/''  |_/ |             =
 *                  =               \  .-\__  '-'  ___/-. /             =
 *                  =             ___'. .'  /--.--\  `. .'___           =
 *                  =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                  =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                  =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                  =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                  =                       `=---='                     =
 *                  =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                  =                                                   =
 *                  =               佛祖保佑         永无BUG              =
 *                  =                                                   =
 *                  =====================================================
 *
 * Last modified 18-1-12 下午8:51
 */

package com.weilylab.xhuschedule.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Exam
import com.weilylab.xhuschedule.view.TextViewUtils
import vip.mystery0.tools.logs.Logs

class ExamAdapter(private val context: Context,
                  private val list: ArrayList<Exam>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = "ExamAdapter"
    private var valueAnimator: ValueAnimator? = null
    private var openedHolder: ViewHolder? = null
    private var currentIndex = -1
    private val drawable = VectorDrawableCompat.create(context.resources, R.drawable.ic_point, null)

    companion object {
        private const val NO_DATA = 1
        private const val NORMAL_DATA = 2
    }

    init {
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        drawable?.setTint(ContextCompat.getColor(context, R.color.colorAccent))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.itemView.tag = position
                val exam = list[position]
                holder.examNameTextView.text = exam.name
                holder.examDateTextView.text = exam.date
                holder.examTestNoTextView.text = exam.testno
                val text = "\n" + context.getString(R.string.exam_no, exam.no) + "\n\n" +
                        context.getString(R.string.exam_sname, exam.sname) + "\n\n" +
                        context.getString(R.string.exam_location, exam.location) + "\n\n" +
                        context.getString(R.string.exam_time, exam.time) + "\n\n" +
                        context.getString(R.string.exam_testtype, exam.testtype) + "\n\n" +
                        context.getString(R.string.exam_region, exam.region)
                holder.detailsTextView.text = text

                //没有动画的展开伸缩
                if (currentIndex == holder.itemView.tag) {
                    holder.detailsTextView.maxLines = Int.MAX_VALUE
                    holder.imageView.setImageDrawable(drawable)
                } else {
                    holder.detailsTextView.maxLines = 1
                    holder.imageView.setImageDrawable(null)
                }
                holder.itemView.setOnClickListener {
                    Logs.i(TAG, "onBindViewHolder: 点击事件")
                    valueAnimator?.cancel()
                    //带动画的展开收缩
                    when (currentIndex) {
                        -1 -> {
                            Logs.i(TAG, "onBindViewHolder: 没有条目被选中")
                            valueAnimator = TextViewUtils.setMaxLinesWithAnimation(holder.detailsTextView, Int.MAX_VALUE)
                            holder.imageView.setImageDrawable(drawable)
                            currentIndex = holder.adapterPosition
                            openedHolder = holder
                        }
                        holder.adapterPosition -> {
                            Logs.i(TAG, "onBindViewHolder: 选中的是当前条目")
                            valueAnimator = TextViewUtils.setMaxLinesWithAnimation(holder.detailsTextView, 1)
                            holder.imageView.setImageDrawable(null)
                            currentIndex = -1
                            openedHolder = null
                        }
                        else -> {
                            Logs.i(TAG, "onBindViewHolder: 选中的其他条目")
                            if (openedHolder != null) {
                                valueAnimator = TextViewUtils.setMaxLinesWithAnimation(openedHolder!!.detailsTextView, 1)
                                openedHolder!!.imageView.setImageDrawable(null)
                            }
                            valueAnimator = TextViewUtils.setMaxLinesWithAnimation(holder.detailsTextView, Int.MAX_VALUE)
                            holder.imageView.setImageDrawable(drawable)
                            currentIndex = holder.adapterPosition
                            openedHolder = holder
                        }
                    }
                }
            }
            is EmptyViewHolder -> {
                holder.textView.text = context.getString(R.string.hint_data_empty)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NO_DATA -> EmptyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_data_empty, parent, false))
            else -> ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_exam, parent, false))
        }
    }

    override fun getItemCount(): Int = if (list.size != 0) list.size else 1

    override fun getItemViewType(position: Int): Int {
        return when {
            list.size == 0 -> NO_DATA
            else -> NORMAL_DATA
        }
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.textView)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var examNameTextView: TextView = itemView.findViewById(R.id.textView_exam_name)
        var examDateTextView: TextView = itemView.findViewById(R.id.textView_exam_date)
        var examTestNoTextView: TextView = itemView.findViewById(R.id.textView_exam_testno)
        var detailsTextView: TextView = itemView.findViewById(R.id.textView_details)
    }
}