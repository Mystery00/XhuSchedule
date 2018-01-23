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

package com.weilylab.xhuschedule.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.NoticeAdapter
import com.weilylab.xhuschedule.classes.baseClass.Notice
import com.weilylab.xhuschedule.classes.rt.GetNoticesRT
import com.weilylab.xhuschedule.interfaces.CommonService
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_notice.*
import java.io.InputStreamReader

class NoticeActivity : BaseActivity() {
    private val list = ArrayList<Notice>()
    private var adapter: NoticeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "notice")
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params)
        setContentView(R.layout.activity_notice)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NoticeAdapter(this, list)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        swipeRefreshLayout.isRefreshing = true
        refresh()
    }

    private fun refresh() {
        ScheduleHelper.tomcatRetrofit
                .create(CommonService::class.java)
                .getNotices("Android")
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), GetNoticesRT::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<GetNoticesRT> {
                    private var getNoticeRT: GetNoticesRT? = null
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        swipeRefreshLayout.isRefreshing = false
                        Snackbar.make(coordinatorLayout, e.message.toString(), Snackbar.LENGTH_SHORT)
                                .show()
                    }

                    override fun onNext(t: GetNoticesRT) {
                        getNoticeRT = t
                    }

                    override fun onComplete() {
                        swipeRefreshLayout.isRefreshing = false
                        if (getNoticeRT != null)
                            when (getNoticeRT!!.rt) {
                                "0" -> {
                                    list.clear()
                                    list.addAll(getNoticeRT!!.notices)
                                    adapter?.notifyDataSetChanged()
                                }
                                else -> Snackbar.make(coordinatorLayout, getNoticeRT!!.msg, Snackbar.LENGTH_SHORT)
                                        .show()
                            }
                    }
                })
    }

    override fun onDestroy() {
        val shownNoticeID = Settings.shownNoticeID.split('|')
        val tempList = ArrayList<String>()
        shownNoticeID.forEach {
            tempList.add(it)
        }
        list.forEach {
            if (!shownNoticeID.contains(it.id.toString()))
                tempList.add(it.id.toString())
        }
        var value = ""
        tempList.forEachIndexed { index, s ->
            value += if (index == tempList.size - 1) s else "$s|"
        }
        Settings.shownNoticeID = value
        super.onDestroy()
    }
}
