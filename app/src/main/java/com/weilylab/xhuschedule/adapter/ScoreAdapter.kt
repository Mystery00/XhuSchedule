/*
 * Created by Mystery0 on 17-12-3 上午3:20.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-3 上午3:18
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Score
import com.weilylab.xhuschedule.util.DensityUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.math.max

class ScoreAdapter(private val context: Context,
                   private val list: ArrayList<Score>) : RecyclerView.Adapter<ScoreAdapter.ViewHolder>() {

    private var isAnimShowList = ArrayList<Boolean>()
    private var isExpandList = ArrayList<Boolean>()
    private var maxHeight = 0
    private var minHeight = DensityUtil.dip2px(context, 72F)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val score = list[position]
        if (isAnimShowList.size == position)
            isAnimShowList.add(false)
        if (isExpandList.size == position)
            isExpandList.add(false)
        holder.scoreNameTextView.text = score.name
        holder.scoreScoreTextView.text = score.score
        holder.scoreNoTextView.text = context.getString(R.string.score_no, score.no)
        holder.scoreCourseTypeTextView.text = context.getString(R.string.score_coursetype, score.coursetype)
        holder.scoreCreditTextView.text = context.getString(R.string.score_credit, score.credit)
        holder.scoreGpaTextView.text = context.getString(R.string.score_gpa, score.gpa)
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
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_score, parent, false))
    }

    override fun getItemCount(): Int = list.size

    fun clearList() {
        isAnimShowList.clear()
        isExpandList.clear()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var scoreNameTextView: TextView = itemView.findViewById(R.id.textView_score_name)
        var scoreScoreTextView: TextView = itemView.findViewById(R.id.textView_score_score)
        var scoreNoTextView: TextView = itemView.findViewById(R.id.textView_score_no)
        var scoreCourseTypeTextView: TextView = itemView.findViewById(R.id.textView_score_coursetype)
        var scoreCreditTextView: TextView = itemView.findViewById(R.id.textView_score_credit)
        var scoreGpaTextView: TextView = itemView.findViewById(R.id.textView_score_gpa)
    }
}