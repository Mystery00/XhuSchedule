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
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Operation
import com.weilylab.xhuschedule.classes.rt.GetNoticesRT
import com.weilylab.xhuschedule.databinding.ItemOperationBinding
import com.weilylab.xhuschedule.handler.ItemOperationHandler
import com.weilylab.xhuschedule.interfaces.CommonService
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.ConstantsCode
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.tools.base.BaseRecyclerViewAdapter
import java.io.InputStreamReader

class OperationAdapter(private val context: Context) : BaseRecyclerViewAdapter<OperationAdapter.ViewHolder, Operation>(R.layout.item_operation) {

	init {
		val titleArray = arrayOf(
				R.string.operation_notice,
				R.string.operation_schedule,
				R.string.operation_exam,
				R.string.operation_score,
				R.string.operation_score_cet,
				R.string.operation_feedback,
				R.string.operation_share
		)
		val imgArray = arrayOf(
				R.mipmap.ic_operation_notice,
				R.mipmap.ic_operation_classes,
				R.mipmap.ic_operation_exam,
				R.mipmap.ic_operation_score,
				R.mipmap.ic_operation_cet,
				R.mipmap.ic_operation_feedback,
				R.mipmap.ic_operation_share
		)
		for (i in 0 until titleArray.size)
			list.add(Operation(false, imgArray[i], context.getString(titleArray[i])))
	}

	override fun setItemView(holder: ViewHolder, position: Int, data: Operation) {
		holder.binding.operation = data
		holder.binding.imageView.setImageResource(data.iconRes)
		holder.binding.handler = ItemOperationHandler(context)
	}

	fun updateBadge() {
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
						Logs.wtf("onError: ", e)
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
									list[0].isShowBadge = isNotice
									notifyItemChanged(0)
								}
							}
					}
				})
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = ItemOperationBinding.inflate(LayoutInflater.from(context), parent, false)
		return ViewHolder(binding)
	}

	class ViewHolder(val binding: ItemOperationBinding) : RecyclerView.ViewHolder(binding.root)
}