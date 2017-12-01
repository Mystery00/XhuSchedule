/*
 * Created by Mystery0 on 17-11-30 下午9:43.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-30 下午9:43
 */

package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ProfileAdapter
import com.weilylab.xhuschedule.classes.Profile
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Created by myste.
 */
class ProfileFragment : Fragment() {
    companion object {
        fun newInstance(list: ArrayList<Profile>): ProfileFragment {
            val bundle = Bundle()
            bundle.putSerializable("list", list)
            val fragment = ProfileFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var list: ArrayList<Profile>
    private lateinit var adapter: ProfileAdapter
    private var isReady = false
    private var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        list = arguments.getSerializable("list") as ArrayList<Profile>
        adapter = ProfileAdapter(activity, list)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_profile, container, false)
            val recyclerView: RecyclerView = rootView!!.findViewById(R.id.recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
        }
        isReady = true
        return rootView
    }

    fun refreshData() {
        Observable.create<Boolean> { subscriber ->
            while (true) {
                if (isReady)
                    break
                Thread.sleep(200)
            }
            subscriber.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Boolean>() {
                    override fun onComplete() {
                        adapter.notifyDataSetChanged()
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onNext(t: Boolean) {
                    }
                })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (rootView != null)
            (rootView!!.parent as ViewGroup).removeView(rootView)
    }
}