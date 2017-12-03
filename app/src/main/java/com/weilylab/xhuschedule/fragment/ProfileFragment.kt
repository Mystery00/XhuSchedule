/*
 * Created by Mystery0 on 17-11-30 下午9:43.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-30 下午9:43
 */

package com.weilylab.xhuschedule.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
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
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import com.weilylab.xhuschedule.activity.MainActivity
import com.weilylab.xhuschedule.util.DensityUtil
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.ViewUtil
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_main.*
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.util.*


/**
 * Created by myste.
 */
class ProfileFragment : Fragment() {
    companion object {
        private val TAG = "ProfileFragment"
        private val PERMISSION_REQUEST_CODE = 1
        private val HEADER_REQUEST_CODE = 2
        private val PROFILE_REQUEST_CODE = 3
        private val HEADER_CROP_REQUEST_CODE = 4
        private val PROFILE_CROP_REQUEST_CODE = 5
        fun newInstance(profile: Profile): ProfileFragment {
            val bundle = Bundle()
            bundle.putSerializable("profile", profile)
            val fragment = ProfileFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var requestType = 0
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
                                return false
                            }
                        })
                        .into(headerImg)
            }
            profileImg.setOnClickListener {
                requestType = PROFILE_REQUEST_CODE
                requestPermission()
            }
            headerImg.setOnClickListener {
                requestType = HEADER_REQUEST_CODE
                requestPermission()
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
                            return false
                        }
                    })
                    .into(headerImg)
        } else {
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

    private fun chooseImg() {
        startActivityForResult(Intent(Intent.ACTION_PICK)
                .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"),
                requestType)
    }

    private fun cropImg(uri: Uri, cropCode: Int, width: Int, height: Int) {
        val savedFile = File(File(activity.filesDir, "CropImg"), when (cropCode) {
            HEADER_CROP_REQUEST_CODE -> "header"
            PROFILE_CROP_REQUEST_CODE -> "user_img"
            else -> throw NullPointerException("裁剪图片请求码错误")
        })
        if (!savedFile.parentFile.exists())
            savedFile.parentFile.mkdirs()
        val destinationUri = Uri.fromFile(savedFile)
        UCrop.of(uri, destinationUri)
                .withAspectRatio(width.toFloat(), height.toFloat())
                .withMaxResultSize(width, height)
                .start(activity, this, cropCode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (rootView != null)
            (rootView!!.parent as ViewGroup).removeView(rootView)
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE)
        } else {
            chooseImg()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null)
            when (requestCode) {
                HEADER_REQUEST_CODE -> {
                    cropImg(data.data, HEADER_CROP_REQUEST_CODE, 320, 176)
                }
                PROFILE_REQUEST_CODE -> {
                    cropImg(data.data, PROFILE_CROP_REQUEST_CODE, 500, 500)
                }
                HEADER_CROP_REQUEST_CODE -> {
                    val saveFile = File(File(activity.filesDir, "CropImg"), "header")
                    Settings.customHeaderImg = saveFile.absolutePath
                    setHeaderImg()
                    Snackbar.make((activity as MainActivity).coordinatorLayout, R.string.hint_custom_img, Snackbar.LENGTH_SHORT)
                            .show()
                }
                PROFILE_CROP_REQUEST_CODE -> {
                    val saveFile = File(File(activity.filesDir, "CropImg"), "user_img")
                    Settings.userImg = saveFile.absolutePath
                    setProfileImg()
                    Snackbar.make((activity as MainActivity).coordinatorLayout, R.string.hint_custom_img, Snackbar.LENGTH_SHORT)
                            .show()
                }
                UCrop.RESULT_ERROR ->
                    Snackbar.make((activity as MainActivity).coordinatorLayout, R.string.error_custom_img, Snackbar.LENGTH_SHORT)
                            .show()
            }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                chooseImg()
            } else {
                Logs.i(TAG, "onRequestPermissionsResult: 权限拒绝")
                Toast.makeText(activity, R.string.hint_permission, Toast.LENGTH_SHORT)
                        .show()
            }
    }
}