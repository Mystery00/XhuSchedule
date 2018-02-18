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

package com.weilylab.xhuschedule.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.OperationAdapter
import com.weilylab.xhuschedule.classes.baseClass.Profile
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.weilylab.xhuschedule.activity.LoginActivity
import com.weilylab.xhuschedule.util.Settings
import java.io.File
import java.util.*

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
    private var adapter: OperationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profile = arguments?.getSerializable("profile") as Profile
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_profile, container, false)
            setProfileImg()
            val recyclerView = rootView!!.findViewById<RecyclerView>(R.id.recycler_view)
            val logoutButton = rootView!!.findViewById<Button>(R.id.button_logout)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            divider.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.lines)!!)
            recyclerView.addItemDecoration(divider)
            adapter = OperationAdapter(activity!!)
            recyclerView.adapter = adapter
            logoutButton.setOnClickListener {
                AlertDialog.Builder(activity!!)
                        .setTitle(R.string.hint_logout_title)
                        .setMessage(R.string.hint_logout_content)
                        .setPositiveButton(android.R.string.ok, { _, _ ->
                            val file = File(activity!!.filesDir.absolutePath + File.separator + "data" + File.separator)
                            if (file.exists())
                                file.listFiles()
                                        .forEach {
                                            it.delete()
                                        }
                            startActivity(Intent(context, LoginActivity::class.java))
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
            }
        }
        isReady = true
        return rootView
    }

    fun setProfileImg() {
        if (Settings.userImg != "") {
            val options = RequestOptions()
                    .signature(MediaStoreSignature("image/*", Calendar.getInstance().timeInMillis, 0))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            Glide.with(this)
                    .load(Settings.userImg)
                    .apply(options)
                    .into(rootView!!.findViewById(R.id.profile_img))
        } else
            rootView!!.findViewById<ImageView>(R.id.profile_img).setImageResource(R.mipmap.profile_img)
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
                        rootView?.findViewById<TextView>(R.id.textView_title)?.setOnClickListener {
                            val stringBuilder = StringBuilder()
                                    .appendln(getString(R.string.profile_no, profile.no))
                                    .appendln(getString(R.string.profile_name, profile.name))
                                    .appendln(getString(R.string.profile_sex, profile.sex))
                                    .appendln(getString(R.string.profile_grade, profile.grade))
                                    .appendln(getString(R.string.profile_institute, profile.institute))
                                    .appendln(getString(R.string.profile_professional, profile.profession))
                                    .appendln(getString(R.string.profile_classname, profile.classname))
                                    .appendln(getString(R.string.profile_direction, profile.direction))
                            AlertDialog.Builder(activity!!)
                                    .setTitle(" ")
                                    .setMessage(stringBuilder.toString())
                                    .setNegativeButton(android.R.string.ok, null)
                                    .show()
                        }
                    }

                    override fun onNext(t: Boolean) {
                    }

                    override fun onError(e: Throwable) {
                    }
                })
    }

    fun updateNoticeBadge() {
        if (adapter != null)
            adapter!!.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (rootView != null)
            (rootView!!.parent as ViewGroup).removeView(rootView)
    }
}