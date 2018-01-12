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

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Exam
import vip.mystery0.tools.flexibleCardView.FlexibleCardView

class ExamAdapter(private val context: Context,
                  private val list: ArrayList<Exam>) : RecyclerView.Adapter<ExamAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exam = list[position]
        holder.examNameTextView.text = exam.name
        holder.examDateTextView.text = exam.date
        holder.examTestNoTextView.text = exam.testno
        holder.examNoTextView.text = context.getString(R.string.exam_no, exam.no)
        holder.examSnameTextView.text = context.getString(R.string.exam_sname, exam.sname)
        holder.examLocationTextView.text = context.getString(R.string.exam_location, exam.location)
        holder.examTimeTextView.text = context.getString(R.string.exam_time, exam.time)
        holder.examTestTypeTextView.text = context.getString(R.string.exam_testtype, exam.testtype)
        holder.examRegionTextView.text = context.getString(R.string.exam_region, exam.region)
        holder.flexibleCardView.setShowState(exam.isExpand)
        holder.flexibleCardView.setOnClickListener {
            holder.flexibleCardView.showAnime({ isExpand ->
                exam.isExpand = isExpand
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_exam, parent, false))
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var flexibleCardView = itemView as FlexibleCardView
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