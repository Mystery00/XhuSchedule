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
import com.weilylab.xhuschedule.base.XhuBasePreferenceFragment
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

class SettingsPreferenceFragmentXhu : XhuBasePreferenceFragment(R.xml.preference_settings) {
	companion object {
		const val ACTION_CHECK_UPDATE_DONE = "action_check_update_done"
		private const val REQUEST_CHOOSE_USER = 21
		private const val REQUEST_CHOOSE_BACKGROUND = 22
		private const val REQUEST_CROP_USER = 31
		private const val REQUEST_CROP_BACKGROUND = 32
	}

	private val userImgPreference: Preference by lazy { findPreferenceById(R.string.key_user_img) }
	private val backgroundImgPreference: Preference by lazy { findPreferenceById(R.string.key_background_img) }
	private val nightModePreference: Preference by lazy { findPreferenceById(R.string.key_night_mode) }
	private val enableViewPagerTransformPreference: CheckBoxPreference by lazy { findPreferenceById(R.string.key_enable_viewpager_transform) as CheckBoxPreference }
	private val tintNavigationBarPreference: CheckBoxPreference by lazy { findPreferenceById(R.string.key_tint_navigation_bar) as CheckBoxPreference }
	private val resetUserImgPreference: Preference by lazy { findPreferenceById(R.string.key_reset_user_img) }
	private val resetBackgroundPreference: Preference by lazy { findPreferenceById(R.string.key_reset_background_img) }
	private val notificationCoursePreference: CheckBoxPreference by lazy { findPreferenceById(R.string.key_notification_course) as CheckBoxPreference }
	private val notificationExamPreference: CheckBoxPreference by lazy { findPreferenceById(R.string.key_notification_exam) as CheckBoxPreference }
	private val notificationTimePreference: Preference by lazy { findPreferenceById(R.string.key_notification_time) }
	private val disableJRSCPreference: CheckBoxPreference by lazy { findPreferenceById(R.string.key_disable_jrsc) as CheckBoxPreference }
	private val showJRSCTranslationPreference: CheckBoxPreference by lazy { findPreferenceById(R.string.key_show_jrsc_translation) as CheckBoxPreference }
	private val autoCheckUpdatePreference: CheckBoxPreference by lazy { findPreferenceById(R.string.key_auto_check_update) as CheckBoxPreference }
	private val weixinPreference: Preference by lazy { findPreferenceById(R.string.key_weixin) }
	private val checkUpdatePreference: Preference by lazy { findPreferenceById(R.string.key_check_update) }
	private val aboutPreference: Preference by lazy { findPreferenceById(R.string.key_about) }

	private val dialog: Dialog by lazy {
		ZLoadingDialog(activity!!)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_check_update))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
				.create()
	}
	private val localBroadcastManager: LocalBroadcastManager by lazy { LocalBroadcastManager.getInstance(activity!!) }

	override fun initPreference() {
		super.initPreference()
		enableViewPagerTransformPreference.isChecked = ConfigurationUtil.enableViewPagerTransform
		disableJRSCPreference.isChecked = ConfigurationUtil.disableJRSC
		tintNavigationBarPreference.isChecked = ConfigurationUtil.tintNavigationBar
		tintNavigationBarPreference.isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
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
					.setTitle(R.string.title_night_mode)
					.setSingleChoiceItems(itemArray, selectedIndex) { _, index ->
						selectedIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						if (ConfigurationUtil.nightMode != selectedIndex) {
							ConfigurationUtil.nightMode = selectedIndex
							snackBarMessage(R.string.hint_need_restart, { snackBar ->
								snackBar.setAction(R.string.action_restart) {
									val intent = activity!!.packageManager.getLaunchIntentForPackage(activity!!.packageName)
									intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
									activity!!.startActivity(intent)
									activity!!.finish()
								}
							})
						}
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
			true
		}
		enableViewPagerTransformPreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.enableViewPagerTransform = !enableViewPagerTransformPreference.isChecked
			snackBarMessage(R.string.hint_need_restart, { snackBar ->
				snackBar.setAction(R.string.action_restart) {
					val intent = activity!!.packageManager.getLaunchIntentForPackage(activity!!.packageName)
					intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
					activity!!.startActivity(intent)
					activity!!.finish()
				}
			})
			true
		}
		tintNavigationBarPreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.tintNavigationBar = !tintNavigationBarPreference.isChecked
			snackBarMessage(R.string.hint_need_restart, { snackBar ->
				snackBar.setAction(R.string.action_restart) {
					val intent = activity!!.packageManager.getLaunchIntentForPackage(activity!!.packageName)
					intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
					activity!!.startActivity(intent)
					activity!!.finish()
				}
			})
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
		disableJRSCPreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.disableJRSC = !disableJRSCPreference.isChecked
			snackBarMessage(R.string.hint_need_restart, { snackBar ->
				snackBar.setAction(R.string.action_restart) {
					val intent = activity!!.packageManager.getLaunchIntentForPackage(activity!!.packageName)
					intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
					activity!!.startActivity(intent)
					activity!!.finish()
				}
			})
			true
		}
		showJRSCTranslationPreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.showJRSCTranslation = !showJRSCTranslationPreference.isChecked
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
		if (grantResults.isEmpty())
			return
		if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
			requestImageChoose(requestCode)
		else
			Snackbar.make(activity!!.window.decorView, R.string.hint_permission_deny, Snackbar.LENGTH_LONG)
					.setAction(R.string.action_re_request) {
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
				cropImage(Matisse.obtainResult(data)[0], REQUEST_CROP_BACKGROUND, DensityTools.getScreenWidth(), DensityTools.getScreenHeight())
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
		if (!dialog.isShowing)
			dialog.show()
	}

	private fun dismissCheckUpdateDialog() {
		if (dialog.isShowing)
			dialog.dismiss()
		toastMessage(R.string.hint_check_update_done)
	}

	inner class CheckUpdateLocalBroadcastReceiver : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent?.action == ACTION_CHECK_UPDATE_DONE) {
				dismissCheckUpdateDialog()
				localBroadcastManager.unregisterReceiver(this)
			}
		}
	}
}