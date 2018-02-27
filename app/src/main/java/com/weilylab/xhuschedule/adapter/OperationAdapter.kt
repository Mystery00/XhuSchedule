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

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.*
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.classes.rt.GetNoticesRT
import com.weilylab.xhuschedule.interfaces.CommonService
import com.weilylab.xhuschedule.listener.FeedBackListener
import com.weilylab.xhuschedule.util.*
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.InputStreamReader

class OperationAdapter(private val context: Context) : RecyclerView.Adapter<OperationAdapter.ViewHolder>() {
    private val list = ArrayList<HashMap<String, Int>>()
    private val dialogView = View.inflate(context, R.layout.dialog_share_with_friends, null)
    private val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)
    private val cancel = dialogView.findViewById<TextView>(R.id.textView_cancel)
    private val shareWithFriendsAdapter = ShareWithFriendsAdapter(context)

    init {
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = shareWithFriendsAdapter
        val titleArray = arrayOf(
                R.string.operation_notice,
                R.string.operation_schedule,
                R.string.operation_exam,
                R.string.operation_score,
                R.string.operation_feedback,
                R.string.operation_share
        )
        val imgArray = arrayOf(
                R.mipmap.ic_operation_notice,
                R.mipmap.ic_operation_classes,
                R.mipmap.ic_operation_exam,
                R.mipmap.ic_operation_score,
                R.mipmap.ic_operation_feedback,
                R.mipmap.ic_operation_share
        )
        for (i in 0 until titleArray.size) {
            val map = HashMap<String, Int>()
            map["title"] = titleArray[i]
            map["icon"] = imgArray[i]
            list.add(map)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val map = list[position]
        holder.imageView.setImageResource(map["icon"]!!)
        holder.textView.setText(map["title"]!!)
        if (position == 0) {
            ScheduleHelper.tomcatRetrofit
                    .create(CommonService::class.java)
                    .getNotices(Constants.NOTICE_PLATFORM)
                    .subscribeOn(Schedulers.newThread())
                    .unsubscribeOn(Schedulers.newThread())
                    .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), GetNoticesRT::class.java) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<GetNoticesRT> {
                        private var getNoticeRT: GetNoticesRT? = null
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }

                        override fun onNext(t: GetNoticesRT) {
                            getNoticeRT = t
                        }

                        override fun onComplete() {
                            if (getNoticeRT != null)
                                when (getNoticeRT!!.rt) {
                                    ConstantsCode.DONE -> {
                                        val notices = getNoticeRT!!.notices
                                        var isNotice = false
                                        val shownNoticeID = Settings.shownNoticeID.split('|')
                                        notices.forEach {
                                            if (!shownNoticeID.contains(it.id.toString()))
                                                isNotice = true
                                        }
                                        if (isNotice)
                                            holder.badgeView.visibility = View.VISIBLE
                                    }
                                }
                        }
                    })
        }
        holder.itemView.setOnClickListener {
            when (position) {
                0 -> context.startActivity(Intent(context, NoticeActivity::class.java))
                1 -> context.startActivity(Intent(context, ScheduleActivity::class.java))
                2 -> context.startActivity(Intent(context, ExamActivity::class.java))
                3 -> context.startActivity(Intent(context, ScoreActivity::class.java))
                4 -> {
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
                5 -> {
                    val shareView = PopupWindow(dialogView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    shareView.isOutsideTouchable = true
                    shareView.isFocusable = true
                    shareView.animationStyle = R.style.Animation
                    shareView.setBackgroundDrawable(ColorDrawable(0x00000000))
                    shareView.setOnDismissListener {
                        setWindowAlpha(1F)
                    }
                    cancel.setOnClickListener {
                        shareView.dismiss()
                    }
                    shareWithFriendsAdapter.shareView = shareView
                    shareView.showAtLocation((context as MainActivity).bottomNavigationView, Gravity.BOTTOM, 0, 0)
                    setWindowAlpha(0.6F)
                }
            }
        }
    }

    private fun setWindowAlpha(alpha: Float) {
        val layoutParams = (context as MainActivity).window.attributes
        layoutParams.alpha = alpha
        context.window.attributes = layoutParams
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_operation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var badgeView: View = itemView.findViewById(R.id.badgeView)
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var textView: TextView = itemView.findViewById(R.id.textView)
    }
}