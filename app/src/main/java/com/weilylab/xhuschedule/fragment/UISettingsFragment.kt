/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-20 下午9:50
 */

package com.weilylab.xhuschedule.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jrummyapps.android.colorpicker.ColorPreference
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.HeaderAdapter
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.interfaces.PhpService
import com.weilylab.xhuschedule.util.*
import com.yalantis.ucrop.UCrop
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.io.InputStream

/**
 * Created by myste.
 */
class UISettingsFragment : PreferenceFragment() {
    companion object {
        private val TAG = "UISettingsFragment"
        private val PERMISSION_REQUEST_CODE = 1
        private val PROFILE_REQUEST_CODE = 2
        private val HEADER_REQUEST_CODE = 3
        private val BACKGROUND_REQUEST_CODE = 4
        private val PROFILE_CROP_REQUEST_CODE = 5
        private val HEADER_CROP_REQUEST_CODE = 6
        private val BACKGROUND_CROP_REQUEST_CODE = 7
    }

    private var requestType = 0
    private lateinit var userImgPreference: Preference
    private lateinit var headerImgPreference: Preference
    private lateinit var backgroundImgPreference: Preference
    private lateinit var customTodayOpacityPreference: Preference
    private lateinit var customTableOpacityPreference: Preference
    private lateinit var customTodayTextColorPreference: ColorPreference
    private lateinit var customTableTextColorPreference: ColorPreference
    private lateinit var customTextSizePreference: Preference
    private lateinit var customTextHeightPreference: Preference
    private lateinit var resetPreference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_ui)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        userImgPreference = findPreference(getString(R.string.key_user_img))
        headerImgPreference = findPreference(getString(R.string.key_header_img))
        backgroundImgPreference = findPreference(getString(R.string.key_background_img))
        customTodayOpacityPreference = findPreference(getString(R.string.key_custom_today_opacity))
        customTableOpacityPreference = findPreference(getString(R.string.key_custom_table_opacity))
        customTodayTextColorPreference = findPreference(getString(R.string.key_custom_today_text_color)) as ColorPreference
        customTableTextColorPreference = findPreference(getString(R.string.key_custom_table_text_color)) as ColorPreference
        customTextSizePreference = findPreference(getString(R.string.key_custom_text_size))
        customTextHeightPreference = findPreference(getString(R.string.key_custom_text_height))
        resetPreference = findPreference(getString(R.string.key_reset))
        userImgPreference.setOnPreferenceClickListener {
            requestType = PROFILE_REQUEST_CODE
            requestPermission()
            true
        }
        headerImgPreference.setOnPreferenceClickListener {
            val view = View.inflate(activity, R.layout.dialog_choose_img_header, null)
            val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            val adapter = HeaderAdapter(activity)
            recyclerView.adapter = adapter
            val dialog = AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.title_header_img))
                    .setView(view)
                    .setPositiveButton(android.R.string.cancel, null)
                    .setNegativeButton("从相册选择", { _, _ ->
                        requestType = HEADER_REQUEST_CODE
                        requestPermission()
                    })
                    .create()
            dialog.show()
            adapter.listener = object : HeaderAdapter.ItemSelectedListener {
                override fun onChecked(link: String, position: Int) {
                    dialog.dismiss()
                    downloadImg(link.substring(link.lastIndexOf('/') + 1), "header")
                }
            }
            true
        }
        backgroundImgPreference.setOnPreferenceClickListener {
            val view = View.inflate(activity, R.layout.dialog_choose_img_background, null)
            val image1: ImageView = view.findViewById(R.id.imageView1)
            val image2: ImageView = view.findViewById(R.id.imageView2)
            val image3: ImageView = view.findViewById(R.id.imageView3)
            val image4: ImageView = view.findViewById(R.id.imageView4)
            val thumbnailList = activity.resources.getStringArray(R.array.thumbnail_background_img)
            val list = activity.resources.getStringArray(R.array.background_img)
            val option = RequestOptions()
                    .override(270, 480)
            Glide.with(activity).load(thumbnailList[0]).apply(option).into(image1)
            Glide.with(activity).load(thumbnailList[1]).apply(option).into(image2)
            Glide.with(activity).load(thumbnailList[2]).apply(option).into(image3)
            Glide.with(activity).load(thumbnailList[3]).apply(option).into(image4)
            val dialog = AlertDialog.Builder(activity)
                    .setTitle("${getString(R.string.title_background_img)}，感谢提供者@BigDingDing")
                    .setView(view)
                    .setPositiveButton(android.R.string.cancel, null)
                    .setNegativeButton("从相册选择", { _, _ ->
                        requestType = BACKGROUND_REQUEST_CODE
                        requestPermission()
                    })
                    .create()
            dialog.show()
            image1.setOnClickListener {
                val link = list[0]
                dialog.dismiss()
                downloadImg(link.substring(link.lastIndexOf('/') + 1), "background")
            }
            image2.setOnClickListener {
                val link = list[1]
                dialog.dismiss()
                downloadImg(link.substring(link.lastIndexOf('/') + 1), "background")
            }
            image3.setOnClickListener {
                val link = list[2]
                dialog.dismiss()
                downloadImg(link.substring(link.lastIndexOf('/') + 1), "background")
            }
            image4.setOnClickListener {
                val link = list[3]
                dialog.dismiss()
                downloadImg(link.substring(link.lastIndexOf('/') + 1), "background")
            }
            true
        }
        customTodayOpacityPreference.setOnPreferenceClickListener {
            val color = '#' + ScheduleHelper.getRandomColor()
            var currentProgress = Settings.customTableOpacity
            val view = View.inflate(activity, R.layout.dialog_custom_today_opacity, null)
            val cardCourseLayout: CardView = view.findViewById(R.id.cardView)
            val img: ImageView = view.findViewById(R.id.img)
            val courseTimeTextView: TextView = view.findViewById(R.id.courseTimeTextView)
            val courseNameAndTeacherTextView: TextView = view.findViewById(R.id.courseNameAndTeacherTextView)
            val courseLocationTextView: TextView = view.findViewById(R.id.courseLocationTextView)
            val seekBar: SeekBar = view.findViewById(R.id.seekBar)
            val textView: TextView = view.findViewById(R.id.textView)
            val course = Course()
            course.name = "测试课程"
            course.teacher = "测试教师"
            course.time = "测试时间"
            course.location = "测试地点"
            course.color = color
            img.setImageBitmap(ViewUtil.drawImg(course))
            courseTimeTextView.text = course.time
            val temp = course.name + " - " + course.teacher
            courseNameAndTeacherTextView.text = temp
            courseLocationTextView.text = course.location
            textView.text = getString(R.string.test_course_current_progress_trans, currentProgress * 100 / 255F)
            cardCourseLayout.setBackgroundColor(Color.parseColor('#' + (if (currentProgress < 16) "0" else "") + Integer.toHexString(currentProgress) + "FFFFFF"))
            seekBar.progress = currentProgress
            textView.text = getString(R.string.test_course_current_progress_trans, currentProgress * 100 / 255F)
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    currentProgress = progress
                    val opacity = (if (progress < 16) "0" else "") + Integer.toHexString(progress)
                    textView.text = getString(R.string.test_course_current_progress_trans, currentProgress * 100 / 255F)
                    cardCourseLayout.setBackgroundColor(Color.parseColor('#' + opacity + "FFFFFF"))
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
            AlertDialog.Builder(activity)
                    .setTitle(" ")
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                        if (currentProgress != Settings.customTableOpacity)
                            ScheduleHelper.isUIChange = true
                        Settings.customTableOpacity = currentProgress
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            true
        }
        customTableOpacityPreference.setOnPreferenceClickListener {
            var color = ScheduleHelper.getRandomColor()
            var currentProgress = Settings.customTodayOpacity
            val view = View.inflate(activity, R.layout.dialog_custom_table_opacity, null)
            val testCourseLayout: ConstraintLayout = view.findViewById(R.id.test_course_layout)
            val seekBar: SeekBar = view.findViewById(R.id.seekBar)
            val textView: TextView = view.findViewById(R.id.textView)
            testCourseLayout.setOnClickListener {
                val opacity = (if (currentProgress < 16) "0" else "") + Integer.toHexString(currentProgress)
                color = ScheduleHelper.getRandomColor()
                testCourseLayout.setBackgroundColor(Color.parseColor('#' + opacity + color))
            }
            testCourseLayout.setBackgroundColor(Color.parseColor('#' + (if (currentProgress < 16) "0" else "") + Integer.toHexString(currentProgress) + color))
            seekBar.progress = currentProgress
            textView.text = getString(R.string.test_course_current_progress_trans, currentProgress * 100 / 255F)
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    currentProgress = progress
                    val opacity = (if (progress < 16) "0" else "") + Integer.toHexString(progress)
                    textView.text = getString(R.string.test_course_current_progress_trans, currentProgress * 100 / 255F)
                    testCourseLayout.setBackgroundColor(Color.parseColor('#' + opacity + color))
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
            AlertDialog.Builder(activity)
                    .setTitle(" ")
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                        if (currentProgress != Settings.customTodayOpacity)
                            ScheduleHelper.isUIChange = true
                        Settings.customTodayOpacity = currentProgress
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            true
        }
        customTodayTextColorPreference.setOnPreferenceChangeListener { _, newValue ->
            Settings.customTodayTextColor = newValue.toString().toInt()
            ScheduleHelper.isUIChange = true
            true
        }
        customTableTextColorPreference.setOnPreferenceChangeListener { _, newValue ->
            Settings.customTableTextColor = newValue.toString().toInt()
            ScheduleHelper.isUIChange = true
            true
        }
        customTextSizePreference.setOnPreferenceClickListener {
            val color = ScheduleHelper.getRandomColor()
            var currentProgress = Settings.customTextSize - 4
            val view = View.inflate(activity, R.layout.dialog_custom_text_size, null)
            val testCourseLayout: ConstraintLayout = view.findViewById(R.id.test_course_layout)
            val textViewName: TextView = view.findViewById(R.id.textView_name)
            val textViewTeacher: TextView = view.findViewById(R.id.textView_teacher)
            val textViewLocation: TextView = view.findViewById(R.id.textView_location)
            val seekBar: SeekBar = view.findViewById(R.id.seekBar)
            val textView: TextView = view.findViewById(R.id.textView)
            val width = (resources.displayMetrics.widthPixels - DensityUtil.dip2px(activity, 32F)) / 7
            val layoutParams = testCourseLayout.layoutParams
            layoutParams.width = width
            layoutParams.height = DensityUtil.dip2px(activity, 144F)
            testCourseLayout.layoutParams = layoutParams
            testCourseLayout.setBackgroundColor(Color.parseColor('#' + color))
            seekBar.progress = currentProgress
            textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, (currentProgress + 4).toFloat())
            textViewTeacher.setTextSize(TypedValue.COMPLEX_UNIT_SP, (currentProgress + 4).toFloat())
            textViewLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, (currentProgress + 4).toFloat())
            textView.text = getString(R.string.test_course_current_progress_text_size, currentProgress + 4)
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    currentProgress = progress
                    textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, (currentProgress + 4).toFloat())
                    textViewTeacher.setTextSize(TypedValue.COMPLEX_UNIT_SP, (currentProgress + 4).toFloat())
                    textViewLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, (currentProgress + 4).toFloat())
                    textView.text = getString(R.string.test_course_current_progress_text_size, progress + 4)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
            AlertDialog.Builder(activity)
                    .setTitle(" ")
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                        if (currentProgress + 4 != Settings.customTextSize)
                            ScheduleHelper.isUIChange = true
                        Settings.customTextSize = currentProgress + 4
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            true
        }
        customTextHeightPreference.setOnPreferenceClickListener {
            val color = ScheduleHelper.getRandomColor()
            var currentProgress = Settings.customTextHeight
            val view = View.inflate(activity, R.layout.dialog_custom_text_height, null)
            val testCourseLayout: ConstraintLayout = view.findViewById(R.id.test_course_layout)
            val textViewName: TextView = view.findViewById(R.id.textView_name)
            val textViewTeacher: TextView = view.findViewById(R.id.textView_teacher)
            val textViewLocation: TextView = view.findViewById(R.id.textView_location)
            val seekBar: SeekBar = view.findViewById(R.id.seekBar)
            val textView: TextView = view.findViewById(R.id.textView)
            val width = (resources.displayMetrics.widthPixels - DensityUtil.dip2px(activity, 32F)) / 7
            val layoutParams = testCourseLayout.layoutParams
            layoutParams.width = width
            layoutParams.height = DensityUtil.dip2px(activity, currentProgress.toFloat())
            testCourseLayout.layoutParams = layoutParams
            testCourseLayout.setBackgroundColor(Color.parseColor('#' + color))
            seekBar.progress = currentProgress
            val textSize = Settings.customTextSize
            textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, (textSize + 4).toFloat())
            textViewTeacher.setTextSize(TypedValue.COMPLEX_UNIT_SP, (textSize + 4).toFloat())
            textViewLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, (textSize + 4).toFloat())
            textView.text = getString(R.string.test_course_current_progress_text_size, currentProgress)
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    currentProgress = progress
                    layoutParams.height = DensityUtil.dip2px(activity, progress.toFloat())
                    testCourseLayout.layoutParams = layoutParams
                    textView.text = getString(R.string.test_course_current_progress_text_height, progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
            AlertDialog.Builder(activity)
                    .setTitle(" ")
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                        if (currentProgress != Settings.customTextHeight)
                            ScheduleHelper.isUIChange = true
                        Settings.customTextHeight = currentProgress
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            true
        }
        resetPreference.setOnPreferenceClickListener {
            val sharedPreference = activity.getSharedPreferences("settings", Context.MODE_PRIVATE)
            sharedPreference.edit()
                    .remove("customHeaderImg")
                    .remove("customBackgroundImg")
                    .remove("customTableOpacity")
                    .remove("customTodayOpacity")
                    .remove("customTableTextColor")
                    .remove("customTodayTextColor")
                    .remove("customTextSize")
                    .remove("customTextHeight")
                    .apply()
            ScheduleHelper.isImageChange = true
            ScheduleHelper.isUIChange = true
            Toast.makeText(activity, R.string.hint_reset, Toast.LENGTH_SHORT)
                    .show()
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null)
            when (requestCode) {
                BACKGROUND_REQUEST_CODE -> {
                    val size = Point()
                    activity.windowManager.defaultDisplay.getSize(size)
                    cropImg(data.data, BACKGROUND_CROP_REQUEST_CODE, size.x, size.y)
                }
                HEADER_REQUEST_CODE -> {
                    cropImg(data.data, HEADER_CROP_REQUEST_CODE, 320, 176)
                }
                PROFILE_REQUEST_CODE -> {
                    cropImg(data.data, PROFILE_CROP_REQUEST_CODE, 500, 500)
                }
                BACKGROUND_CROP_REQUEST_CODE -> {
                    val saveFile = File(File(activity.filesDir, "CropImg"), "background")
                    Settings.customBackgroundImg = saveFile.absolutePath
                    ScheduleHelper.isImageChange = true
                    Toast.makeText(activity, R.string.hint_custom_img, Toast.LENGTH_SHORT)
                            .show()
                }
                HEADER_CROP_REQUEST_CODE -> {
                    val saveFile = File(File(activity.filesDir, "CropImg"), "header")
                    Settings.customHeaderImg = saveFile.absolutePath
                    ScheduleHelper.isImageChange = true
                    Toast.makeText(activity, R.string.hint_custom_img, Toast.LENGTH_SHORT)
                            .show()
                }
                PROFILE_CROP_REQUEST_CODE -> {
                    val saveFile = File(File(activity.filesDir, "CropImg"), "user_img")
                    Settings.userImg = saveFile.absolutePath
                    ScheduleHelper.isImageChange = true
                    Toast.makeText(activity, R.string.hint_custom_img, Toast.LENGTH_SHORT)
                            .show()
                }
                UCrop.RESULT_ERROR ->
                    Toast.makeText(activity, R.string.error_custom_img, Toast.LENGTH_SHORT)
                            .show()
            }
        super.onActivityResult(requestCode, resultCode, data)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?,
                                            grantResults: IntArray) {
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

    private fun chooseImg() {
        startActivityForResult(Intent(Intent.ACTION_PICK)
                .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"),
                requestType)
    }

    private fun cropImg(uri: Uri, cropCode: Int, width: Int, height: Int) {
        val savedFile = File(File(activity.filesDir, "CropImg"), when (cropCode) {
            HEADER_CROP_REQUEST_CODE -> "header"
            BACKGROUND_CROP_REQUEST_CODE -> "background"
            PROFILE_CROP_REQUEST_CODE -> "user_img"
            else -> throw NullPointerException("裁剪图片请求码错误")
        })
        if (!savedFile.parentFile.exists())
            savedFile.parentFile.mkdirs()
        val destinationUri = Uri.fromFile(savedFile)
        UCrop.of(uri, destinationUri)
                .withAspectRatio(width.toFloat(), height.toFloat())
                .withMaxResultSize(width * 10, height * 10)
                .start(activity, this, cropCode)
    }

    private fun downloadImg(fileName: String, saveType: String) {
        val loadingDialog = ZLoadingDialog(activity)
                .setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE)
                .setHintText(getString(R.string.hint_dialog_download_img))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(activity, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
                .create()
        loadingDialog.show()
        ScheduleHelper.imgRetrofit.create(PhpService::class.java)
                .downloadImg(fileName)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> responseBody.byteStream() })
                .observeOn(Schedulers.io())
                .doOnNext { inputStream ->
                    try {
                        when (saveType) {
                            "background" -> {
                                val saveFile = File(File(activity.filesDir, "CropImg"), "background")
                                XhuFileUtil.saveFile(inputStream, saveFile)
                                Settings.customBackgroundImg = saveFile.absolutePath
                            }
                            "header" -> {
                                val saveFile = File(activity.cacheDir, "temp")
                                XhuFileUtil.saveFile(inputStream, saveFile)
                                cropImg(Uri.fromFile(saveFile), HEADER_CROP_REQUEST_CODE, 320, 176)
                            }
                            "user_img" -> {
                                val saveFile = File(File(activity.filesDir, "CropImg"), "user_img")
                                XhuFileUtil.saveFile(inputStream, saveFile)
                                Settings.userImg = saveFile.absolutePath
                            }
                        }
                        ScheduleHelper.isImageChange = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<InputStream> {
                    override fun onNext(t: InputStream) {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loadingDialog.dismiss()
                        Toast.makeText(activity, R.string.error_custom_img, Toast.LENGTH_SHORT)
                                .show()
                    }

                    override fun onComplete() {
                        loadingDialog.dismiss()
                        Toast.makeText(activity, R.string.hint_custom_img, Toast.LENGTH_SHORT)
                                .show()
                    }
                })
    }
}