package com.weilylab.xhuschedule.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.provider.MediaStore
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.SettingsActivity
import com.weilylab.xhuschedule.classes.Update
import com.weilylab.xhuschedule.interfaces.UpdateResponse
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.UpdateNotification
import com.weilylab.xhuschedule.view.CustomDatePicker
import com.yalantis.ucrop.UCrop
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_settings.*
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.util.*

/**
 * Created by myste.
 */
class SettingsPreferenceFragment : PreferenceFragment()
{
	companion object
	{
		private val TAG = "SettingsPreferenceFragment"
		private val PERMISSION_REQUEST_CODE = 1
		private val HEADER_REQUEST_CODE = 2
		private val BACKGROUND_REQUEST_CODE = 3
		private val HEADER_CROP_REQUEST_CODE = 4
		private val BACKGROUND_CROP_REQUEST_CODE = 5
	}

	private var requestType = 0
	private lateinit var coordinatorLayout: CoordinatorLayout
	private lateinit var loadingDialog: ZLoadingDialog
	private lateinit var firstDayPreference: Preference
	private lateinit var headerImgPreference: Preference
	private lateinit var backgroundImgPreference: Preference
	private lateinit var checkUpdatePreference: Preference
	private lateinit var weilyProductPreference: Preference

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		addPreferencesFromResource(R.xml.preferences)
		coordinatorLayout = (activity as SettingsActivity).coordinatorLayout
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View
	{
		initialization()
		monitor()
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	private fun initialization()
	{
		loadingDialog = ZLoadingDialog(activity)
				.setLoadingBuilder(Z_TYPE.SEARCH_PATH)
				.setHintText(getString(R.string.hint_dialog_check_update))
				.setHintTextSize(16F)
				.setCancelable(false)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			loadingDialog.setLoadingColor(resources.getColor(R.color.colorAccent, null))
			loadingDialog.setHintTextColor(resources.getColor(R.color.colorAccent, null))
		}
		firstDayPreference = findPreference(getString(R.string.key_first_day))
		headerImgPreference = findPreference(getString(R.string.key_header_img))
		backgroundImgPreference = findPreference(getString(R.string.key_background_img))
		checkUpdatePreference = findPreference(getString(R.string.key_check_update))
		weilyProductPreference = findPreference(getString(R.string.key_weily_product))

		val date = Settings.firstWeekOfTerm.split('-')
		firstDayPreference.summary = date[0] + '-' + (date[1].toInt() + 1) + '-' + date[2]
	}

	private fun monitor()
	{
		firstDayPreference.setOnPreferenceClickListener {
			val calendar = Calendar.getInstance(Locale.CHINA)
			val firstWeekOfTerm = Settings.firstWeekOfTerm
			val date = firstWeekOfTerm.split('-')
			calendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
			val view = LayoutInflater.from(activity).inflate(R.layout.dialog_date_picker, null)
			val datePicker: CustomDatePicker = view.findViewById(R.id.datePicker)
			datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null)
			val dialog = AlertDialog.Builder(activity)
					.setView(view)
					.setPositiveButton(android.R.string.ok, null)
					.setNegativeButton(android.R.string.cancel, null)
					.create()
			dialog.show()
			if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null)
			{
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
					calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth, 0, 0, 0)
					when
					{
						calendar.after(Calendar.getInstance()) -> Snackbar.make(datePicker, R.string.error_time_after, Snackbar.LENGTH_SHORT)
								.show()
						else ->
						{
							val dayWeek = calendar.get(Calendar.DAY_OF_WEEK)
							if (dayWeek == Calendar.SUNDAY)
								calendar.add(Calendar.DAY_OF_MONTH, -1)
							calendar.firstDayOfWeek = Calendar.MONDAY
							val day = calendar.get(Calendar.DAY_OF_WEEK)
							calendar.add(Calendar.DATE, calendar.firstDayOfWeek - day)
							Settings.firstWeekOfTerm = calendar.get(Calendar.YEAR).toString() + '-' + calendar.get(Calendar.MONTH).toString() + '-' + calendar.get(Calendar.DAY_OF_MONTH).toString()
							firstDayPreference.summary = calendar.get(Calendar.YEAR).toString() + '-' + (calendar.get(Calendar.MONTH) + 1).toString() + '-' + calendar.get(Calendar.DAY_OF_MONTH).toString()
							dialog.dismiss()
						}
					}
				}
			}
			true
		}
		headerImgPreference.setOnPreferenceClickListener {
			requestType = HEADER_REQUEST_CODE
			requestPermission()
			true
		}
		backgroundImgPreference.setOnPreferenceClickListener {
			requestType = BACKGROUND_REQUEST_CODE
			requestPermission()
			true
		}
		checkUpdatePreference.setOnPreferenceClickListener {
			var update: Update? = null
			Observable.create<Int> { subscriber ->
				val call = ScheduleHelper.getUpdateRetrofit().create(UpdateResponse::class.java).checkUpdateCall(getString(R.string.app_version_code).toInt())
				val response = call.execute()
				if (!response.isSuccessful)
				{
					subscriber.onNext(-1)
					subscriber.onComplete()
					return@create
				}
				update = response.body()
				Logs.i(TAG, "onCreate: " + update?.message)
				subscriber.onNext(update?.code!!)
				subscriber.onComplete()
			}
					.subscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : Observer<Int>
					{
						private var code = -233

						override fun onSubscribe(d: Disposable)
						{
							Logs.i(TAG, "onSubscribe: ")
							loadingDialog.show()
						}

						override fun onError(e: Throwable)
						{
							e.printStackTrace()
							loadingDialog.dismiss()
						}

						override fun onComplete()
						{
							Logs.i(TAG, "onComplete: ")
							loadingDialog.dismiss()
							if (code == 1)
								UpdateNotification.notify(activity, update!!.version)
							else
							{
								Snackbar.make(coordinatorLayout, update!!.message, Snackbar.LENGTH_SHORT)
										.show()
							}
						}

						override fun onNext(result: Int)
						{
							Logs.i(TAG, "onNext: ")
							code = result
						}
					})
			true
		}
		weilyProductPreference.setOnPreferenceClickListener {
			activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://weilylab.com")))
			true
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		if (resultCode == Activity.RESULT_OK && data != null)
			when (requestCode)
			{
				BACKGROUND_REQUEST_CODE ->
				{
					val size = Point()
					activity.windowManager.defaultDisplay.getSize(size)
					cropImg(data.data, BACKGROUND_CROP_REQUEST_CODE, size.x, size.y)
				}
				HEADER_REQUEST_CODE ->
				{
					cropImg(data.data, HEADER_CROP_REQUEST_CODE, 255, 176)
				}
				HEADER_CROP_REQUEST_CODE ->
				{
					Logs.i(TAG, "onActivityResult: HEADER_CROP_REQUEST_CODE")
					val saveFile = File(File(activity.filesDir, "CropImg"), "header")
					Settings.customHeaderImg = saveFile.absolutePath
					Snackbar.make(coordinatorLayout, R.string.hint_custom_img, Snackbar.LENGTH_SHORT)
							.show()
				}
				BACKGROUND_CROP_REQUEST_CODE ->
				{
					Logs.i(TAG, "onActivityResult: BACKGROUND_CROP_REQUEST_CODE")
					val saveFile = File(File(activity.filesDir, "CropImg"), "background")
					Settings.customBackgroundImg = saveFile.absolutePath
					Snackbar.make(coordinatorLayout, R.string.hint_custom_img, Snackbar.LENGTH_SHORT)
							.show()
				}
				UCrop.RESULT_ERROR -> Snackbar.make(coordinatorLayout, R.string.error_custom_img, Snackbar.LENGTH_SHORT)
						.show()
			}
		super.onActivityResult(requestCode, resultCode, data)
	}

	private fun requestPermission()
	{
		if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
				requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
						PERMISSION_REQUEST_CODE)
		}
		else
		{
			chooseImg()
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?,
											grantResults: IntArray)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == PERMISSION_REQUEST_CODE)
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
			{
				chooseImg()
			}
			else
			{
				Logs.i(TAG, "onRequestPermissionsResult: 权限拒绝")
				Snackbar.make((activity as SettingsActivity).coordinatorLayout, R.string.hint_permission, Snackbar.LENGTH_LONG)
						.setAction(android.R.string.ok, {
							requestPermission()
						})
						.show()
			}
	}

	private fun chooseImg()
	{
		startActivityForResult(Intent(Intent.ACTION_PICK)
				.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"),
				requestType)
	}

	private fun cropImg(uri: Uri, cropCode: Int, width: Int, height: Int)
	{
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