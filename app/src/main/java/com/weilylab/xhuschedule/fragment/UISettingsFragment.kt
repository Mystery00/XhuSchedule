/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午2:56
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
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.jrummyapps.android.colorpicker.ColorPreference
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.util.DensityUtil
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.ViewUtil
import com.yalantis.ucrop.UCrop
import vip.mystery0.tools.logs.Logs
import java.io.File

/**
 * Created by myste.
 */
class UISettingsFragment : PreferenceFragment() {
    companion object {
        private val TAG = "UISettingsFragment"
        private val PERMISSION_REQUEST_CODE = 1
        private val HEADER_REQUEST_CODE = 2
        private val BACKGROUND_REQUEST_CODE = 3
        private val HEADER_CROP_REQUEST_CODE = 4
        private val BACKGROUND_CROP_REQUEST_CODE = 5
    }

    private var requestType = 0
    private lateinit var headerImgPreference: Preference
    private lateinit var backgroundImgPreference: Preference
    private lateinit var customTodayOpacityPreference: Preference
    private lateinit var customTableOpacityPreference: Preference
    private lateinit var customTodayTextColorPreference: ColorPreference
    private lateinit var customTableTextColorPreference: ColorPreference
    private lateinit var customTextSizePreference: Preference
    private lateinit var resetPreference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_ui)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        headerImgPreference = findPreference(getString(R.string.key_header_img))
        backgroundImgPreference = findPreference(getString(R.string.key_background_img))
        customTodayOpacityPreference = findPreference(getString(R.string.key_custom_today_opacity))
        customTableOpacityPreference = findPreference(getString(R.string.key_custom_table_opacity))
        customTodayTextColorPreference = findPreference(getString(R.string.key_custom_today_text_color)) as ColorPreference
        customTableTextColorPreference = findPreference(getString(R.string.key_custom_table_text_color)) as ColorPreference
        customTextSizePreference = findPreference(getString(R.string.key_custom_text_size))
        resetPreference = findPreference(getString(R.string.key_reset))
        headerImgPreference.setOnPreferenceClickListener {
            requestType = HEADER_REQUEST_CODE
            requestPermission()
            true
        }
        backgroundImgPreference.setOnPreferenceClickListener {
            //            val view = View.inflate(activity, R.layout.layout_choose_img, null)
//            val image1: ImageView = view.findViewById(R.id.imageView1)
//            val image2: ImageView = view.findViewById(R.id.imageView2)
//            val image3: ImageView = view.findViewById(R.id.imageView3)
//            val image4: ImageView = view.findViewById(R.id.imageView4)
//            val list = activity.resources.getStringArray(R.array.background_img)
//            Glide.with(activity).load(list[0]).into(image1)
//            Glide.with(activity).load(list[1]).into(image2)
//            Glide.with(activity).load(list[2]).into(image3)
//            Glide.with(activity).load(list[3]).into(image4)
//            AlertDialog.Builder(activity)
//                    .setTitle(getString(R.string.title_background_img))
//                    .setView(view)
//                    .setPositiveButton(android.R.string.cancel, null)
//                    .setNegativeButton("从相册选择", { _, _ ->
            requestType = BACKGROUND_REQUEST_CODE
            requestPermission()
//                    })
//                    .show()
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
                BACKGROUND_CROP_REQUEST_CODE -> {
                    Logs.i(TAG, "onActivityResult: BACKGROUND_CROP_REQUEST_CODE")
                    val saveFile = File(File(activity.filesDir, "CropImg"), "background")
                    Settings.customBackgroundImg = saveFile.absolutePath
                    ScheduleHelper.isImageChange = true
                    Toast.makeText(activity, R.string.hint_custom_img, Toast.LENGTH_SHORT)
                            .show()
                }
                HEADER_CROP_REQUEST_CODE -> {
                    Logs.i(TAG, "onActivityResult: HEADER_CROP_REQUEST_CODE")
                    val saveFile = File(File(activity.filesDir, "CropImg"), "header")
                    Settings.customHeaderImg = saveFile.absolutePath
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
        val savedFile = File(File(activity.filesDir, "CropImg"), if (cropCode == HEADER_CROP_REQUEST_CODE) "header" else "background")
        if (!savedFile.parentFile.exists())
            savedFile.parentFile.mkdirs()
        val destinationUri = Uri.fromFile(savedFile)
        UCrop.of(uri, destinationUri)
                .withAspectRatio(width.toFloat(), height.toFloat())
                .withMaxResultSize(width * 10, height * 10)
                .start(activity, this, cropCode)
    }
}