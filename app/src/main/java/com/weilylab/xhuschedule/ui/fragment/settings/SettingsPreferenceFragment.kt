/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.fragment.settings

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import com.google.android.material.snackbar.Snackbar
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBasePreferenceFragment
import com.weilylab.xhuschedule.model.event.CheckUpdateEvent
import com.weilylab.xhuschedule.model.event.UI
import com.weilylab.xhuschedule.model.event.UIConfigEvent
import com.weilylab.xhuschedule.service.CheckUpdateService
import com.weilylab.xhuschedule.ui.activity.SettingsActivity
import com.weilylab.xhuschedule.ui.custom.CustomGlideEngine
import com.weilylab.xhuschedule.utils.*
import com.yalantis.ucrop.UCrop
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import vip.mystery0.tools.utils.AndroidVersionCode
import vip.mystery0.tools.utils.screenHeight
import vip.mystery0.tools.utils.screenWidth
import vip.mystery0.tools.utils.sdkIsAfter
import java.io.File
import java.util.*

class SettingsPreferenceFragment : XhuBasePreferenceFragment(R.xml.preference_settings) {
	companion object {
		const val ACTION_CHECK_UPDATE_DONE = "action_check_update_done"
		private const val FILE_SELECT_USER = 11
		private const val FILE_SELECT_BACKGROUND = 12
		private const val REQUEST_CHOOSE_USER = 21
		private const val REQUEST_CHOOSE_BACKGROUND = 22
		private const val REQUEST_CROP_USER = 31
		private const val REQUEST_CROP_BACKGROUND = 32
	}

	private val eventBus: EventBus by inject()

	private val userImgPreference by lazy { findPreferenceById<Preference>(R.string.key_user_img) }
	private val backgroundImgPreference by lazy { findPreferenceById<Preference>(R.string.key_background_img) }
	private val nightModePreference by lazy { findPreferenceById<Preference>(R.string.key_night_mode) }
	private val enableViewPagerTransformPreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_enable_viewpager_transform) }
	private val tintNavigationBarPreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_tint_navigation_bar) }
	private val useInAppImageSelectorPreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_use_in_app_image_selector) }
	private val resetUserImgPreference by lazy { findPreferenceById<Preference>(R.string.key_reset_user_img) }
	private val resetBackgroundPreference by lazy { findPreferenceById<Preference>(R.string.key_reset_background_img) }
	private val notificationCoursePreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_notification_course) }
	private val notificationExamPreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_notification_exam) }
	private val notificationTimePreference by lazy { findPreferenceById<Preference>(R.string.key_notification_time) }
	private val disableJRSCPreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_disable_jrsc) }
	private val showJRSCTranslationPreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_show_jrsc_translation) }
	private val autoCheckUpdatePreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_auto_check_update) }
	private val weixinPreference by lazy { findPreferenceById<Preference>(R.string.key_weixin) }
	private val checkUpdatePreference by lazy { findPreferenceById<Preference>(R.string.key_check_update) }
	private val aboutPreference by lazy { findPreferenceById<Preference>(R.string.key_about) }

	private val dialog: Dialog by lazy {
		ZLoadingDialog(requireActivity())
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_check_update))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(requireActivity(), R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(requireActivity(), R.color.colorAccent))
				.create()
	}

	override fun initPreference() {
		super.initPreference()
		enableViewPagerTransformPreference.isChecked = ConfigurationUtil.enableViewPagerTransform
		disableJRSCPreference.isChecked = ConfigurationUtil.disableJRSC
		tintNavigationBarPreference.isChecked = ConfigurationUtil.tintNavigationBar
		tintNavigationBarPreference.isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
		useInAppImageSelectorPreference.isChecked = ConfigurationUtil.useInAppImageSelector
		autoCheckUpdatePreference.isChecked = ConfigurationUtil.autoCheckUpdate
		notificationCoursePreference.isChecked = ConfigurationUtil.notificationCourse
		notificationExamPreference.isChecked = ConfigurationUtil.notificationExam
		notificationTimePreference.summary = getString(R.string.summary_notification_time, ConfigurationUtil.notificationTime)

		if (sdkIsAfter(AndroidVersionCode.VERSION_Q, false)) {
			useInAppImageSelectorPreference.isChecked = false
			ConfigurationUtil.useInAppImageSelector = false
			useInAppImageSelectorPreference.isEnabled = false
			useInAppImageSelectorPreference.summary = getString(R.string.summary_use_in_app_image_selector_q)
		}
	}

	override fun monitor() {
		super.monitor()
		userImgPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			requestImageChoose(REQUEST_CHOOSE_USER)
			true
		}
		backgroundImgPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			requestImageChoose(REQUEST_CHOOSE_BACKGROUND)
			true
		}
		nightModePreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			val itemArray = resources.getStringArray(R.array.night_mode)
			var selectedIndex = ConfigurationUtil.nightMode
			AlertDialog.Builder(requireActivity())
					.setTitle(R.string.title_night_mode)
					.setSingleChoiceItems(itemArray, selectedIndex) { _, index ->
						selectedIndex = index
					}
					.setPositiveButton(R.string.action_ok) { _, _ ->
						if (ConfigurationUtil.nightMode != selectedIndex) {
							ConfigurationUtil.nightMode = selectedIndex
							snackBarMessage(R.string.hint_need_restart, {
								setAction(R.string.action_restart) {
									val intent = requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)
									intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
									requireActivity().startActivity(intent)
									requireActivity().finish()
								}
							}, Snackbar.LENGTH_SHORT)
						}
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
			true
		}
		enableViewPagerTransformPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.enableViewPagerTransform = !enableViewPagerTransformPreference.isChecked
			snackBarMessage(R.string.hint_need_restart, {
				setAction(R.string.action_restart) {
					val intent = requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)
					intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
					requireActivity().startActivity(intent)
					requireActivity().finish()
				}
			}, Snackbar.LENGTH_SHORT)
			true
		}
		tintNavigationBarPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.tintNavigationBar = !tintNavigationBarPreference.isChecked
			snackBarMessage(R.string.hint_need_restart, {
				setAction(R.string.action_restart) {
					val intent = requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)
					intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
					requireActivity().startActivity(intent)
					requireActivity().finish()
				}
			}, Snackbar.LENGTH_SHORT)
			true
		}
		useInAppImageSelectorPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.useInAppImageSelector = !useInAppImageSelectorPreference.isChecked
			true
		}
		resetUserImgPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			AlertDialog.Builder(requireActivity())
					.setTitle(R.string.hint_confirm_reset_user_img)
					.setMessage("")
					.setPositiveButton(R.string.action_ok) { _, _ ->
						ConfigurationUtil.customUserImage = ""
						eventBus.post(UIConfigEvent(arrayListOf(UI.USER_IMG)))
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
			true
		}
		resetBackgroundPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			AlertDialog.Builder(requireActivity())
					.setTitle(R.string.hint_confirm_reset_background_img)
					.setMessage("")
					.setPositiveButton(R.string.action_ok) { _, _ ->
						ConfigurationUtil.customBackgroundImage = ""
						eventBus.post(UIConfigEvent(arrayListOf(UI.BACKGROUND_IMG)))
					}
					.setNegativeButton(R.string.action_cancel, null)
					.show()
			true
		}
		notificationCoursePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.notificationCourse = !notificationCoursePreference.isChecked
			true
		}
		notificationExamPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.notificationExam = !notificationExamPreference.isChecked
			true
		}
		notificationTimePreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
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
			TimePickerDialog(requireActivity(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
				val hourString = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
				val minuteString = if (minute < 10) "0$minute" else minute.toString()
				val newString = "$hourString:$minuteString"
				ConfigurationUtil.notificationTime = newString
				notificationTimePreference.summary = getString(R.string.summary_notification_time, ConfigurationUtil.notificationTime)
				ConfigUtil.setTrigger(requireActivity())
			}, oldHour, oldMinute, true)
					.show()
			true
		}
		disableJRSCPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.disableJRSC = !disableJRSCPreference.isChecked
			snackBarMessage(R.string.hint_need_restart, {
				setAction(R.string.action_restart) {
					val intent = requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)
					intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
					requireActivity().startActivity(intent)
					requireActivity().finish()
				}
			}, Snackbar.LENGTH_SHORT)
			true
		}
		showJRSCTranslationPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.showJRSCTranslation = !showJRSCTranslationPreference.isChecked
			true
		}
		autoCheckUpdatePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.autoCheckUpdate = !autoCheckUpdatePreference.isChecked
			true
		}
		weixinPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			ShareUtil.linkWeiXinMiniProgram(requireActivity())
			true
		}
		checkUpdatePreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			showCheckUpdateDialog()
			val intent = Intent(requireActivity(), CheckUpdateService::class.java)
			intent.putExtra(CheckUpdateService.CHECK_ACTION_BY_MANUAL, true)
			requireActivity().startService(intent)
			true
		}
		aboutPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			SettingsActivity.intentTo(activity, SettingsActivity.TYPE_ABOUT)
			true
		}
	}

	private fun requestImageChoose(requestCode: Int) {
		if (ConfigurationUtil.useInAppImageSelector)
			requestPermissionsOnFragment(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)) { code, array ->
				if (array.isEmpty() || array[0] == PackageManager.PERMISSION_GRANTED) {
					Matisse.from(this)
							.choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.BMP))
							.showSingleMediaType(true)
							.countable(false)
							.maxSelectable(1)
							.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
							.thumbnailScale(0.85f)
							.imageEngine(CustomGlideEngine())
							.theme(if (ContextCompat.getColor(requireActivity(), R.color.isNight) == Color.WHITE) R.style.Matisse_Zhihu else R.style.Matisse_Dracula)
							.forResult(requestCode)
				} else {
					Snackbar.make(requireActivity().window.decorView, R.string.hint_permission_deny, Snackbar.LENGTH_LONG)
							.setAction(R.string.action_re_request) {
								reRequestPermission(code)
							}
							.show()
				}
			}
		else {
			val intent = Intent(Intent.ACTION_PICK)
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
			startActivityForResult(intent, requestCode - 10)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			FILE_SELECT_USER -> if (resultCode == Activity.RESULT_OK) {
				cropImage(data?.data!!, REQUEST_CROP_USER, 500, 500)
			}
			FILE_SELECT_BACKGROUND -> if (resultCode == Activity.RESULT_OK) {
				cropImage(data?.data!!, REQUEST_CROP_BACKGROUND, screenWidth, screenHeight)
			}
			REQUEST_CHOOSE_USER -> if (resultCode == Activity.RESULT_OK) {
				cropImage(Matisse.obtainResult(data)[0], REQUEST_CROP_USER, 500, 500)
			}
			REQUEST_CHOOSE_BACKGROUND -> if (resultCode == Activity.RESULT_OK) {
				cropImage(Matisse.obtainResult(data)[0], REQUEST_CROP_BACKGROUND, screenWidth, screenHeight)
			}
			REQUEST_CROP_USER -> if (resultCode == Activity.RESULT_OK) {
				val file = getFile(requestCode)
				ConfigurationUtil.customUserImage = file.absolutePath
				eventBus.post(UIConfigEvent(arrayListOf(UI.USER_IMG)))
				Toast.makeText(requireActivity(), R.string.hint_custom_img, Toast.LENGTH_SHORT)
						.show()
			}
			REQUEST_CROP_BACKGROUND -> if (resultCode == Activity.RESULT_OK) {
				val file = getFile(requestCode)
				ConfigurationUtil.customBackgroundImage = file.absolutePath
				eventBus.post(UIConfigEvent(arrayListOf(UI.BACKGROUND_IMG)))
				Toast.makeText(requireActivity(), R.string.hint_custom_img, Toast.LENGTH_SHORT)
						.show()
			}
		}
	}

	private fun getFile(cropCode: Int): File = getImageFile(when (cropCode) {
		REQUEST_CROP_USER -> FileUtil.UI_IMAGE_USER_IMG
		REQUEST_CROP_BACKGROUND -> FileUtil.UI_IMAGE_BACKGROUND
		else -> throw NullPointerException("null")
	})!!

	private fun cropImage(uri: Uri, cropCode: Int, width: Int, height: Int) {
		val saveFile = getFile(cropCode)
		if (!saveFile.parentFile!!.exists())
			saveFile.parentFile!!.mkdirs()
		val destinationUri = Uri.fromFile(saveFile)
		UCrop.of(uri, destinationUri)
				.withAspectRatio(width.toFloat(), height.toFloat())
				.withMaxResultSize(width * 10, height * 10)
				.start(requireActivity(), this, cropCode)
	}

	private fun showCheckUpdateDialog() {
		if (!dialog.isShowing)
			dialog.show()
	}

	private fun dismissCheckUpdateDialog() {
		if (dialog.isShowing)
			dialog.dismiss()
		toast(R.string.hint_check_update_done)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		eventBus.register(this)
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onDestroyView() {
		eventBus.unregister(this)
		super.onDestroyView()
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun checkUploadDialog(event: CheckUpdateEvent) {
		if (event.action == ACTION_CHECK_UPDATE_DONE) {
			dismissCheckUpdateDialog()
		}
	}
}