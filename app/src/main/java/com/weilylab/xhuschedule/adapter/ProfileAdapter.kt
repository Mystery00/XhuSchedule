/*
 * Created by Mystery0 on 17-12-1 下午1:52.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-1 下午1:52
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Profile
import com.weilylab.xhuschedule.util.DensityUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs

/**
 * Created by mystery0.
 */
class ProfileAdapter(private val context: Context,
                     private val list: ArrayList<Profile>) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {
    companion object {
        private val TAG = "ProfileAdapter"
    }

    private var isAnimShow = false
    private var isExpand = false
    private var maxHeight = 0
    private var minHeight = DensityUtil.dip2px(context, 82F)

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profile = list[position]
        holder.header.text = context.getString(R.string.profile_title, profile.no, profile.name)
//        holder.sex.text = context.getString(R.string.profile_sex, profile.sex)
//        holder.institute.text = context.getString(R.string.profile_institute, profile.institute)
//        holder.professional.text = context.getString(R.string.profile_professional, profile.profession)
//        holder.classname.text = context.getString(R.string.profile_classname, profile.classname)
//        holder.grade.text = context.getString(R.string.profile_grade, profile.grade)
//        holder.direction.text = context.getString(R.string.profile_direction, profile.direction)
        holder.foldLayout.post {
            if (maxHeight == 0) {
                maxHeight = holder.foldLayout.measuredHeight
                val layoutParams = holder.foldLayout.layoutParams
                layoutParams.height = minHeight
                holder.foldLayout.layoutParams = layoutParams
                Logs.i(TAG, "onBindViewHolder: " + maxHeight)
            }
        }
        holder.foldLayout.setOnClickListener {
            val layoutParams = holder.foldLayout.layoutParams
            Observable.create<Int> { subscriber ->
                val showDistanceArray = Array(31, { i -> ((maxHeight - minHeight) / 30F) * i + minHeight })
                if (isExpand)
                    showDistanceArray.reverse()
                showDistanceArray.forEach {
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
                            holder.foldLayout.layoutParams = layoutParams
                        }

                        override fun onSubscribe(d: Disposable) {
                            isAnimShow = true
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            isAnimShow = false
                        }

                        override fun onComplete() {
                            isAnimShow = false
                            isExpand = !isExpand
                        }
                    })
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var foldLayout: ConstraintLayout = itemView.findViewById(R.id.fold_layout)
        var header: TextView = itemView.findViewById(R.id.textView_student_header)
        var sex: TextView = itemView.findViewById(R.id.textView_student_sex)
        var institute: TextView = itemView.findViewById(R.id.textView_student_institute)
        var professional: TextView = itemView.findViewById(R.id.textView_professional)
        var classname: TextView = itemView.findViewById(R.id.textView_classname)
        var grade: TextView = itemView.findViewById(R.id.textView_student_grade)
        var direction: TextView = itemView.findViewById(R.id.textView_student_direction)
    }
}