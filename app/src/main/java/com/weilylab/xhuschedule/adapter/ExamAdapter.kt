/*
 * Created by Mystery0 on 17-12-3 上午2:04.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-3 上午2:04
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Exam
import com.weilylab.xhuschedule.util.DensityUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.math.max

class ExamAdapter(private val context: Context,
                  private val list: ArrayList<Exam>) : RecyclerView.Adapter<ExamAdapter.ViewHolder>() {

    private var isAnimShowList = ArrayList<Boolean>()
    private var isExpandList = ArrayList<Boolean>()
    private var maxHeight = 0
    private var minHeight = DensityUtil.dip2px(context, 72F)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exam = list[position]
        if (isAnimShowList.size == position)
            isAnimShowList.add(false)
        if (isExpandList.size == position)
            isExpandList.add(false)
        holder.examNameTextView.text = exam.name
        holder.examDateTextView.text = exam.date
        holder.examNoTextView.text = context.getString(R.string.exam_no, exam.no)
        holder.examSnameTextView.text = context.getString(R.string.exam_sname, exam.sname)
        holder.examLocationTextView.text = context.getString(R.string.exam_location, exam.location)
        holder.examTimeTextView.text = context.getString(R.string.exam_time, exam.time)
        holder.examTestNoTextView.text = context.getString(R.string.exam_testno, exam.testno)
        holder.examTestTypeTextView.text = context.getString(R.string.exam_testtype, exam.testtype)
        holder.examRegionTextView.text = context.getString(R.string.exam_region, exam.region)
        if (maxHeight <= minHeight)
            holder.itemView.post {
                maxHeight = max(maxHeight, holder.itemView.measuredHeight)
                val params = holder.itemView.layoutParams
                if (isExpandList[holder.adapterPosition]) {
                    params.height = maxHeight
                } else {
                    params.height = minHeight
                }
                holder.itemView.layoutParams = params
            }
        else {
            val params = holder.itemView.layoutParams
            if (isExpandList[holder.adapterPosition]) {
                params.height = maxHeight
            } else {
                params.height = minHeight
            }
            holder.itemView.layoutParams = params
        }
        holder.itemView.setOnClickListener {
            val layoutParams = holder.itemView.layoutParams
            Observable.create<Int> { subscriber ->
                val showArray = Array(31, { i -> ((maxHeight - minHeight) / 30F) * i + minHeight })
                if (isExpandList[holder.adapterPosition])
                    showArray.reverse()
                showArray.forEach {
                    subscriber.onNext(it.toInt())
                    Thread.sleep(8)
                }
                subscriber.onComplete()
            }
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Int> {
                        override fun onNext(t: Int) {
                            layoutParams.height = t
                            holder.itemView.layoutParams = layoutParams
                        }

                        override fun onSubscribe(d: Disposable) {
                            isAnimShowList[holder.adapterPosition] = true
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            isAnimShowList[holder.adapterPosition] = false
                        }

                        override fun onComplete() {
                            isAnimShowList[holder.adapterPosition] = false
                            isExpandList[holder.adapterPosition] = !isExpandList[holder.adapterPosition]
                        }
                    })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_exam, parent, false))
    }

    override fun getItemCount(): Int = list.size

    fun clearList() {
        isAnimShowList.clear()
        isExpandList.clear()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var examNameTextView: TextView = itemView.findViewById(R.id.textView_exam_name)
        var examDateTextView: TextView = itemView.findViewById(R.id.textView_exam_date)
        var examNoTextView: TextView = itemView.findViewById(R.id.textView_exam_no)
        var examSnameTextView: TextView = itemView.findViewById(R.id.textView_exam_sname)
        var examLocationTextView: TextView = itemView.findViewById(R.id.textView_exam_location)
        var examTimeTextView: TextView = itemView.findViewById(R.id.textView_exam_time)
        var examTestNoTextView: TextView = itemView.findViewById(R.id.textView_exam_testno)
        var examTestTypeTextView: TextView = itemView.findViewById(R.id.textView_exam_testtype)
        var examRegionTextView: TextView = itemView.findViewById(R.id.textView_exam_region)
    }
}