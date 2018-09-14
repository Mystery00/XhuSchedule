package com.weilylab.xhuschedule.ui.fragment.settings

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import com.weilylab.xhuschedule.R
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import com.weilylab.xhuschedule.base.BasePreferenceFragment
import com.weilylab.xhuschedule.service.CheckUpdateService
import com.weilylab.xhuschedule.ui.activity.SettingsActivity
import com.weilylab.xhuschedule.ui.custom.CustomGlideEngine
import com.weilylab.xhuschedule.utils.*
import com.yalantis.ucrop.UCrop
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.DensityTools
import java.io.File
import java.util.*

class SettingsPreferenceFragment : BasePreferenceFragment(R.xml.preference_settings) {
	companion object {
		const val ACTION_CHECK_UPDATE_DONE = "action_check_update_done"
		private const val REQUEST_CHOOSE_USER = 21
		private const val REQUEST_CHOOSE_BACKGROUND = 22
		private const val REQUEST_CROP_USER = 31
		private const val REQUEST_CROP_BACKGROUND = 32
	}

	private lateinit var userImgPreference: Preference
	private lateinit var backgroundImgPreference: Preference
	private lateinit var nightModePreference: Preference
	private lateinit var resetUserImgPreference: Preference
	private lateinit var resetBackgroundPreference: Preference
	private lateinit var notificationCoursePreference: CheckBoxPreference
	private lateinit var notificationExamPreference: CheckBoxPreference
	private lateinit var notificationTimePreference: Preference
	private lateinit var autoCheckUpdatePreference: CheckBoxPreference
	private lateinit var weixinPreference: Preference
	private lateinit var checkUpdatePreference: Preference
	private lateinit var aboutPreference: Preference
	private lateinit var dialog: Dialog
	private lateinit var localBroadcastManager: LocalBroadcastManager

	override fun initPreference() {
		super.initPreference()
		userImgPreference = findPreferenceById(R.string.key_user_img)
		backgroundImgPreference = findPreferenceById(R.string.key_background_img)
		nightModePreference = findPreferenceById(R.string.key_night_mode)
		resetUserImgPreference = findPreferenceById(R.string.key_reset_user_img)
		resetBackgroundPreference = findPreferenceById(R.string.key_reset_background_img)
		notificationCoursePreference = findPreferenceById(R.string.key_notification_course) as CheckBoxPreference
		notificationExamPreference = findPreferenceById(R.string.key_notification_exam) as CheckBoxPreference
		notificationTimePreference = findPreferenceById(R.string.key_notification_time)
		autoCheckUpdatePreference = findPreferenceById(R.string.key_auto_check_update) as CheckBoxPreference
		weixinPreference = findPreferenceById(R.string.key_weixin)
		checkUpdatePreference = findPreferenceById(R.string.key_check_update)
		aboutPreference = findPreferenceById(R.string.key_about)

		autoCheckUpdatePreference.isChecked = ConfigurationUtil.autoCheckUpdate
		notificationCoursePreference.isChecked = ConfigurationUtil.notificationCourse
		notificationExamPreference.isChecked = ConfigurationUtil.notificationExam
		notificationTimePreference.summary = getString(R.string.summary_notification_time, ConfigurationUtil.notificationTime)
	}

	override fun monitor() {
		super.monitor()
		userImgPreference.setOnPreferenceClickListener {
			requestImageChoose(REQUEST_CHOOSE_USER)
			true
		}
		backgroundImgPreference.setOnPreferenceClickListener {
			requestImageChoose(REQUEST_CHOOSE_BACKGROUND)
			true
		}
		nightModePreference.setOnPreferenceClickListener {
			val itemArray = resources.getStringArray(R.array.night_mode)
			var selectedIndex = ConfigurationUtil.nightMode
			AlertDialog.Builder(activity!!)
					.setTitle(R.string.hint_dialog_choose_student)
					.setSingleChoiceItems(itemArray, selectedIndex) { _, index ->
						selectedIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						if (ConfigurationUtil.nightMode != selectedIndex) {
							ConfigurationUtil.nightMode = selectedIndex
							toastMessage("请重启APP以应用新的设置", true)
						}
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
			true
		}
		resetUserImgPreference.setOnPreferenceClickListener {
			AlertDialog.Builder(activity!!)
					.setTitle(R.string.hint_confirm_reset_user_img)
					.setMessage("")
					.setPositiveButton(R.string.action_ok) { _, _ ->
						ConfigurationUtil.customUserImage = ""
						LayoutRefreshConfigUtil.isChangeUserImage = true
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
			true
		}
		resetBackgroundPreference.setOnPreferenceClickListener {
			AlertDialog.Builder(activity!!)
					.setTitle(R.string.hint_confirm_reset_background_img)
					.setMessage("")
					.setPositiveButton(R.string.action_ok) { _, _ ->
						ConfigurationUtil.customBackgroundImage = ""
						LayoutRefreshConfigUtil.isChangeBackgroundImage = true
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
			true
		}
		notificationCoursePreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.notificationCourse = !notificationCoursePreference.isChecked
			true
		}
		notificationExamPreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.notificationExam = !notificationExamPreference.isChecked
			true
		}
		notificationTimePreference.setOnPreferenceClickListener {
			val time = ConfigurationUtil.notificationTime
			val oldHour: Int
			val oldMinute: Int
			if (time == "") {
				val calendar = Calendar.getInstance()
				oldHour = calendar.get(Calendar.HOUR_OF_DAY)
				oldMinute = calendar.get(Calendar.MINUTE)
			} else {
				val array = time.split(':')
				oldHour = array[0].toInt()
				oldMinute = array[1].toInt()
			}
			TimePickerDialog(activity!!, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
				val hourString = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
				val minuteString = if (minute < 10) "0$minute" else minute.toString()
				val newString = "$hourString:$minuteString"
				ConfigurationUtil.notificationTime = newString
				notificationTimePreference.summary = getString(R.string.summary_notification_time, ConfigurationUtil.notificationTime)
				ConfigUtil.setTrigger(activity!!)
			}, oldHour, oldMinute, true)
					.show()
			true
		}
		autoCheckUpdatePreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.autoCheckUpdate = !autoCheckUpdatePreference.isChecked
			true
		}
		weixinPreference.setOnPreferenceClickListener {
			ShareUtil.linkWeiXinMiniProgram(activity!!)
			true
		}
		checkUpdatePreference.setOnPreferenceClickListener {
			showCheckUpdateDialog()
			val intentFilter = IntentFilter(ACTION_CHECK_UPDATE_DONE)
			if (!::localBroadcastManager.isInitialized)
				localBroadcastManager = LocalBroadcastManager.getInstance(activity!!)
			localBroadcastManager.registerReceiver(CheckUpdateLocalBroadcastReceiver(), intentFilter)
			val intent = Intent(activity!!, CheckUpdateService::class.java)
			intent.putExtra(CheckUpdateService.CHECK_ACTION_BY_MANUAL, true)
			activity!!.startService(intent)
			true
		}
		aboutPreference.setOnPreferenceClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_ABOUT)
			true
		}
	}

	private fun requestImageChoose(requestCode: Int) {
		if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
				requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
		} else
			Matisse.from(this)
					.choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.BMP))
					.showSingleMediaType(true)
					.countable(false)
					.maxSelectable(1)
					.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
					.thumbnailScale(0.85f)
					.imageEngine(CustomGlideEngine())
					.forResult(requestCode)
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
			requestImageChoose(requestCode)
		else
			Snackbar.make(activity!!.window.decorView, "权限被拒绝，无法使用", Snackbar.LENGTH_LONG)
					.setAction("重新申请") {
						requestImageChoose(requestCode)
					}
					.show()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		Logs.i("onActivityResult: $requestCode")
		when (requestCode) {
			REQUEST_CHOOSE_USER -> if (resultCode == Activity.RESULT_OK) {
				cropImage(Matisse.obtainResult(data)[0], REQUEST_CROP_USER, 500, 500)
			}
			REQUEST_CHOOSE_BACKGROUND -> if (resultCode == Activity.RESULT_OK) {
				cropImage(Matisse.obtainResult(data)[0], REQUEST_CROP_BACKGROUND, DensityTools.getScreenWidth(activity!!), DensityTools.getScreenHeight(activity!!))
			}
			REQUEST_CROP_USER -> if (resultCode == Activity.RESULT_OK) {
				val file = getFile(requestCode)
				ConfigurationUtil.customUserImage = file.absolutePath
				LayoutRefreshConfigUtil.isChangeUserImage = true
				Toast.makeText(activity!!, R.string.hint_custom_img, Toast.LENGTH_SHORT)
						.show()
			}
			REQUEST_CROP_BACKGROUND -> if (resultCode == Activity.RESULT_OK) {
				val file = getFile(requestCode)
				ConfigurationUtil.customBackgroundImage = file.absolutePath
				LayoutRefreshConfigUtil.isChangeBackgroundImage = true
				Toast.makeText(activity!!, R.string.hint_custom_img, Toast.LENGTH_SHORT)
						.show()
			}
		}
	}

	private fun getFile(cropCode: Int): File = FileUtil.getImageFile(activity!!, when (cropCode) {
		REQUEST_CROP_USER -> FileUtil.UI_IMAGE_USER_IMG
		REQUEST_CROP_BACKGROUND -> FileUtil.UI_IMAGE_BACKGROUND
		else -> throw NullPointerException("null")
	})!!

	private fun cropImage(uri: Uri, cropCode: Int, width: Int, height: Int) {
		val saveFile = getFile(cropCode)
		if (!saveFile.parentFile.exists())
			saveFile.parentFile.mkdirs()
		val destinationUri = Uri.fromFile(saveFile)
		UCrop.of(uri, destinationUri)
				.withAspectRatio(width.toFloat(), height.toFloat())
				.withMaxResultSize(width * 10, height * 10)
				.start(activity!!, this, cropCode)
	}

	private fun showCheckUpdateDialog() {
		if (!::dialog.isInitialized)
			dialog = ZLoadingDialog(activity!!)
					.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
					.setHintText(getString(R.string.hint_dialog_check_update))
					.setHintTextSize(16F)
					.setCanceledOnTouchOutside(false)
					.setLoadingColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
					.setHintTextColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
					.create()
		if (!dialog.isShowing)
			dialog.show()
	}

	private fun dismissCheckUpdateDialog() {
		if (dialog.isShowing)
			dialog.dismiss()
		Toast.makeText(activity!!, "检查更新完成！", Toast.LENGTH_SHORT)
				.show()
	}

	inner class CheckUpdateLocalBroadcastReceiver : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent?.action == ACTION_CHECK_UPDATE_DONE) {
				dismissCheckUpdateDialog()
				if (::localBroadcastManager.isInitialized)
					localBroadcastManager.unregisterReceiver(this)
			}
		}
	}
}