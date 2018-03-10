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

package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.TodayAdapter
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.ViewUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by myste.
 */
class TodayFragment : Fragment() {
	companion object {

		fun newInstance(list: ArrayList<Course>): TodayFragment {
			val bundle = Bundle()
			bundle.putSerializable(Constants.INTENT_TAG_NAME_LIST, list)
			val fragment = TodayFragment()
			fragment.arguments = bundle
			return fragment
		}
	}

	private lateinit var list: ArrayList<Course>
	private lateinit var adapter: TodayAdapter
	private var isReady = false
	private var rootView: View? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		@Suppress("UNCHECKED_CAST")
		list = arguments?.getSerializable(Constants.INTENT_TAG_NAME_LIST) as ArrayList<Course>
		adapter = TodayAdapter(activity!!, list)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_today, container, false)
			val recyclerView: RecyclerView = rootView!!.findViewById(R.id.recycler_view)
			recyclerView.layoutManager = LinearLayoutManager(activity)
			recyclerView.adapter = adapter
			isReady = true
		}
		return rootView
	}

	fun setBackground() {
		Observable.create<Boolean> { subscriber ->
			while (true)
				if (rootView != null)
					break
			subscriber.onComplete()
		}
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : DisposableObserver<Boolean>() {
					override fun onError(e: Throwable) {
						e.printStackTrace()
					}

					override fun onNext(t: Boolean) {
					}

					override fun onComplete() {
						ViewUtil.setBackground(activity!!, rootView!!.findViewById(R.id.background))
					}
				})
	}

	fun refreshData() {
		val observer = object : Observer<Boolean> {
			override fun onComplete() {
				adapter.notifyDataSetChanged()
			}

			override fun onSubscribe(d: Disposable) {
			}

			override fun onError(e: Throwable) {
			}

			override fun onNext(t: Boolean) {
			}
		}
		val observable = Observable.create<Boolean> { subscriber ->
			while (true) {
				if (isReady)
					break
				Thread.sleep(200)
			}
			subscriber.onComplete()
		}

		observable.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		if (rootView != null)
			(rootView!!.parent as ViewGroup).removeView(rootView)
	}
}