/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-20 下午9:43
 */

package com.weilylab.xhuschedule.fragment

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.OperationAdapter
import com.weilylab.xhuschedule.classes.baseClass.Profile
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import com.weilylab.xhuschedule.util.DensityUtil
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.ViewUtil
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
            if (Settings.customHeaderImg != "") {
                val options = RequestOptions()
                        .signature(MediaStoreSignature("image/*", Calendar.getInstance().timeInMillis, 0))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                Glide.with(this)
                        .load(Settings.customHeaderImg)
                        .apply(options)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                return false
                            }

                            override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                ViewUtil.blur(activity, (resource as BitmapDrawable).bitmap, textViewLayout)
                                val light = ViewUtil.getLight(resource.bitmap, resource.intrinsicWidth, resource.intrinsicHeight)
                                if (light > 128) {
                                    rootView!!.findViewById<TextView>(R.id.textView_title).setTextColor(Color.parseColor("#555555"))
                                    rootView!!.findViewById<TextView>(R.id.textView_score_gpa).setTextColor(Color.parseColor("#555555"))
                                    rootView!!.findViewById<TextView>(R.id.textView_score_no).setTextColor(Color.parseColor("#555555"))
                                } else {
                                    rootView!!.findViewById<TextView>(R.id.textView_title).setTextColor(Color.WHITE)
                                    rootView!!.findViewById<TextView>(R.id.textView_score_gpa).setTextColor(Color.WHITE)
                                    rootView!!.findViewById<TextView>(R.id.textView_score_no).setTextColor(Color.WHITE)
                                }
                                return false
                            }
                        })
                        .into(headerImg)
            }
            profileImg.post {
                val options = RequestOptions()
                        .signature(MediaStoreSignature("image/*", Calendar.getInstance().timeInMillis, 0))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                var height = profileImg.measuredHeight
                val params = profileImg.layoutParams
                if (DensityUtil.px2dip(activity, height.toFloat()) > 120) {
                    height = DensityUtil.dip2px(activity, 120F)
                    params.height = height
                }
                params.width = height
                profileImg.layoutParams = params
                if (Settings.userImg != "")
                    Glide.with(this)
                            .load(Settings.userImg)
                            .apply(options)
                            .into(profileImg)
            }
            if (Settings.customHeaderImg == "")
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

    fun setHeaderImg() {
        val headerImg = rootView!!.findViewById<ImageView>(R.id.header_img)
        val textViewLayout = rootView!!.findViewById<View>(R.id.textViewLayout)
        if (Settings.customHeaderImg != "") {
            val options = RequestOptions()
                    .signature(MediaStoreSignature("image/*", Calendar.getInstance().timeInMillis, 0))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            Glide.with(this)
                    .load(Settings.customHeaderImg)
                    .apply(options)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            ViewUtil.blur(activity, (resource as BitmapDrawable).bitmap, textViewLayout)
                            val light = ViewUtil.getLight(resource.bitmap, resource.intrinsicWidth, resource.intrinsicHeight)
                            if (light > 128) {
                                rootView!!.findViewById<TextView>(R.id.textView_title).setTextColor(Color.parseColor("#555555"))
                                rootView!!.findViewById<TextView>(R.id.textView_score_gpa).setTextColor(Color.parseColor("#555555"))
                                rootView!!.findViewById<TextView>(R.id.textView_score_no).setTextColor(Color.parseColor("#555555"))
                            } else {
                                rootView!!.findViewById<TextView>(R.id.textView_title).setTextColor(Color.WHITE)
                                rootView!!.findViewById<TextView>(R.id.textView_score_gpa).setTextColor(Color.WHITE)
                                rootView!!.findViewById<TextView>(R.id.textView_score_no).setTextColor(Color.WHITE)
                            }
                            return false
                        }
                    })
                    .into(headerImg)
        } else {
            rootView!!.findViewById<TextView>(R.id.textView_title).setTextColor(Color.WHITE)
            rootView!!.findViewById<TextView>(R.id.textView_score_gpa).setTextColor(Color.WHITE)
            rootView!!.findViewById<TextView>(R.id.textView_score_no).setTextColor(Color.WHITE)
            headerImg.setImageResource(R.mipmap.header_img)
            headerImg.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    headerImg.viewTreeObserver.removeOnPreDrawListener(this)
                    headerImg.buildDrawingCache()
                    ViewUtil.blur(activity, headerImg.drawingCache, textViewLayout)
                    return true
                }
            })
        }
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
                        rootView?.findViewById<TextView>(R.id.textView_score_gpa)?.text = getString(R.string.profile_professional, profile.profession)
                        rootView?.findViewById<TextView>(R.id.textView_score_no)?.text = getString(R.string.profile_classname, profile.classname)
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
                            AlertDialog.Builder(activity)
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (rootView != null)
            (rootView!!.parent as ViewGroup).removeView(rootView)
    }
}