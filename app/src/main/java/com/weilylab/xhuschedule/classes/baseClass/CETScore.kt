/*
 * Created by Mystery0 on 18-2-28 下午10:08.
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
 * Last modified 18-2-28 下午10:08
 */

package com.weilylab.xhuschedule.classes.baseClass

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ShareCETAdapter
import com.weilylab.xhuschedule.util.ViewUtil
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs

class CETScore {
    private val TAG = "CETScore"
    var id = ""
    var name = ""
    var school = ""
    var total = ""
    var listen = ""
    var read = ""
    var write = ""
    var type = ""
    var oralId = ""//口语 准考证号
    var oralGrade = ""//口语 等级

    fun showInView(context: Context) {
        val scoreView = View.inflate(context, R.layout.dialog_cet_scores, null)
        val textName: TextView = scoreView.findViewById(R.id.textName)
        val textId: TextView = scoreView.findViewById(R.id.textId)
        val textSchool: TextView = scoreView.findViewById(R.id.textSchool)
        val textTotal: TextView = scoreView.findViewById(R.id.textTotal)
        val textListen: TextView = scoreView.findViewById(R.id.textListen)
        val textRead: TextView = scoreView.findViewById(R.id.textRead)
        val textWrite: TextView = scoreView.findViewById(R.id.textWrite)
        val textOralId: TextView = scoreView.findViewById(R.id.textOralId)
        val textOralGrade: TextView = scoreView.findViewById(R.id.textOralGrade)
        textName.text = name
        textId.text = id
        textSchool.text = school
        textTotal.text = total
        textListen.text = listen
        textRead.text = read
        textWrite.text = write
        textOralId.text = oralId
        textOralGrade.text = oralGrade
        val fileName = "CET-$id.jpg"
        val loadingDialog = ZLoadingDialog(context)
                .setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
                .setHintText(context.getString(R.string.hint_dialog_save_image))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                .create()
        val scoreDialog = AlertDialog.Builder(context)
                .setView(scoreView)
                .setPositiveButton(R.string.action_save, null)
                .setNegativeButton(R.string.action_share, null)
                .create()
        scoreDialog.show()
        scoreDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            Observable.create<Boolean> {
                val bitmap = ViewUtil.getViewBitmap(scoreView)
                val result = XhuFileUtil.saveBitmapToFile(bitmap, XhuFileUtil.getCETImageFile(fileName))
                it.onNext(result)
                it.onComplete()
            }
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Boolean> {
                        private var result = false
                        override fun onComplete() {
                            loadingDialog.dismiss()
                            Toast.makeText(context, if (result) "保存成功！" else "保存失败！", Toast.LENGTH_LONG)
                                    .show()
                        }

                        override fun onSubscribe(d: Disposable) {
                            loadingDialog.show()
                        }

                        override fun onNext(t: Boolean) {
                            result = t
                        }

                        override fun onError(e: Throwable) {
                            loadingDialog.dismiss()
                            Logs.wtf(TAG, "onError: ", e)
                        }
                    })
        }
        scoreDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            Observable.create<Boolean> {
                val bitmap = ViewUtil.getViewBitmap(scoreView)
                val result = XhuFileUtil.saveBitmapToFile(bitmap, XhuFileUtil.getCETImageFile(fileName))
                it.onNext(result)
                it.onComplete()
            }
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Boolean> {
                        private var result = false
                        override fun onComplete() {
                            loadingDialog.dismiss()
                            if (result) {
                                val dialogView = View.inflate(context, R.layout.dialog_share_cet_with_friends, null)
                                val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)
                                val cancel = dialogView.findViewById<TextView>(R.id.textView_cancel)
                                val shareView = PopupWindow(dialogView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                val shareCETAdapter = ShareCETAdapter(context)
                                recyclerView.layoutManager = GridLayoutManager(context, 3)
                                recyclerView.adapter = shareCETAdapter
                                shareView.isOutsideTouchable = true
                                shareView.isFocusable = true
                                shareView.animationStyle = R.style.Animation
                                shareView.setBackgroundDrawable(ColorDrawable(0x00000000))
                                cancel.setOnClickListener {
                                    shareView.dismiss()
                                }
                                shareCETAdapter.shareView = shareView
                                shareCETAdapter.fileName = fileName
                                shareView.showAtLocation(scoreDialog.getButton(AlertDialog.BUTTON_NEGATIVE), Gravity.BOTTOM, 0, 0)
                            } else
                                Toast.makeText(context, "保存失败！", Toast.LENGTH_LONG)
                                        .show()
                        }

                        override fun onSubscribe(d: Disposable) {
                            loadingDialog.show()
                        }

                        override fun onNext(t: Boolean) {
                            result = t
                        }

                        override fun onError(e: Throwable) {
                            loadingDialog.dismiss()
                            Logs.wtf(TAG, "onError: ", e)
                        }
                    })
        }
    }
}