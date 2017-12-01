/*
 * Created by Mystery0 on 17-11-30 下午9:43.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-30 下午9:43
 */

package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
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
        fun newInstance(profile: Profile): ProfileFragment {
            val bundle = Bundle()
            bundle.putSerializable("profile", profile)
            val fragment = ProfileFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var profile: Profile? = null
    private var isReady = false
    private var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profile = arguments.getSerializable("profile") as Profile
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        }
        isReady = true
        return rootView
    }

    fun setProfile(profile: Profile) {
        Observable.create<Boolean> { subscriber ->
            while (true) {
                if (isReady)
                    break
                Thread.sleep(200)
            }
            subscriber.onComplete()
        }
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Boolean>() {
                    override fun onComplete() {
                        rootView?.findViewById<TextView>(R.id.textView_title)?.text = getString(R.string.profile_title, profile.no, profile.name)
                        rootView?.findViewById<TextView>(R.id.textView_professional)?.text = getString(R.string.profile_professional, profile.profession)
                        rootView?.findViewById<TextView>(R.id.textView_classname)?.text = getString(R.string.profile_classname, profile.classname)
                    }

                    override fun onNext(t: Boolean) {
                    }

                    override fun onError(e: Throwable) {
                    }
                })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (rootView != null)
            (rootView!!.parent as ViewGroup).removeView(rootView)
    }
}