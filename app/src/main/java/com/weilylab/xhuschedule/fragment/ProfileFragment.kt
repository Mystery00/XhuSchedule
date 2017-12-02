/*
 * Created by Mystery0 on 17-11-30 下午9:43.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-30 下午9:43
 */

package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.OperationAdapter
import com.weilylab.xhuschedule.classes.Profile
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.weilylab.xhuschedule.util.ViewUtil
import vip.mystery0.tools.logs.Logs


/**
 * Created by myste.
 */
class ProfileFragment : Fragment() {
    companion object {
        private val TAG = "ProfileFragment"
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
            val headerImg = rootView!!.findViewById<ImageView>(R.id.header_img)
            val profileImg = rootView!!.findViewById<ImageView>(R.id.profile_img)
            val textViewLayout = rootView!!.findViewById<View>(R.id.textViewLayout)
            profileImg.post {
                Logs.i(TAG, "onCreateView: post")
                val height = profileImg.measuredHeight
                val params = profileImg.layoutParams
                params.width = height
                profileImg.layoutParams = params

            }
            headerImg.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    headerImg.viewTreeObserver.removeOnPreDrawListener(this)
                    headerImg.buildDrawingCache()
                    ViewUtil.blur(activity, headerImg.drawingCache, textViewLayout)
                    return true
                }
            })
            val recyclerView = rootView!!.findViewById<RecyclerView>(R.id.recycler_view)
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
            recyclerView.adapter = OperationAdapter(activity)
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