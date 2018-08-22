package com.weilylab.xhuschedule.ui.fragment.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.preference.CheckBoxPreference
import android.preference.Preference
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.weilylab.xhuschedule.ui.custom.CustomGlideEngine
import com.weilylab.xhuschedule.utils.FileUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.utils.ShareUtil
import com.yalantis.ucrop.UCrop
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import vip.mystery0.tools.utils.DensityTools
import java.io.File

class SettingsPreferenceFragment : BasePreferenceFragment(R.xml.preference_settings) {
	companion object {
		private const val REQUEST_CHOOSE_USER = 21
		private const val REQUEST_CHOOSE_BACKGROUND = 22
		private const val REQUEST_CROP_USER = 31
		private const val REQUEST_CROP_BACKGROUND = 32
	}

	private lateinit var userImgPreference: Preference
	private lateinit var backgroundImgPreference: Preference
	private lateinit var resetUserImgPreference: Preference
	private lateinit var resetBackgroundPreference: Preference
	private lateinit var autoCheckUpdatePreference: CheckBoxPreference
	private lateinit var weixinPreference: Preference
	private lateinit var checkUpdatePreference: Preference
	private lateinit var updateLogPreference: Preference

	override fun initPreference() {
		super.initPreference()
		userImgPreference = findPreferenceById(R.string.key_user_img)
		backgroundImgPreference = findPreferenceById(R.string.key_background_img)
		resetUserImgPreference = findPreferenceById(R.string.key_reset_user_img)
		resetBackgroundPreference = findPreferenceById(R.string.key_reset_background_img)
		autoCheckUpdatePreference = findPreferenceById(R.string.key_auto_check_update) as CheckBoxPreference
		weixinPreference = findPreferenceById(R.string.key_weixin)
		checkUpdatePreference = findPreferenceById(R.string.key_check_update)
		updateLogPreference = findPreferenceById(R.string.key_update_log)

		autoCheckUpdatePreference.isChecked = ConfigurationUtil.autoCheckUpdate
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
		resetUserImgPreference.setOnPreferenceClickListener {
			AlertDialog.Builder(activity)
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
			AlertDialog.Builder(activity)
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
		autoCheckUpdatePreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.autoCheckUpdate = !autoCheckUpdatePreference.isChecked
			true
		}
		weixinPreference.setOnPreferenceClickListener {
			ShareUtil.linkWeiXinMiniProgram(activity)
			true
		}
	}

	private fun requestImageChoose(requestCode: Int) {
		if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
				requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
		} else
			Matisse.from(activity)
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
			Snackbar.make(activity.window.decorView, "权限被拒绝，无法使用", Snackbar.LENGTH_LONG)
					.setAction("重新申请") {
						requestImageChoose(requestCode)
					}
					.show()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		getResult(requestCode, resultCode, data)
	}

	fun getResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			REQUEST_CHOOSE_USER -> if (resultCode == Activity.RESULT_OK) {
				cropImage(Matisse.obtainResult(data)[0], REQUEST_CROP_USER, 500, 500)
			}
			REQUEST_CHOOSE_BACKGROUND -> if (resultCode == Activity.RESULT_OK) {
				cropImage(Matisse.obtainResult(data)[0], REQUEST_CROP_BACKGROUND, DensityTools.getScreenWidth(activity), DensityTools.getScreenHeight(activity))
			}
			REQUEST_CROP_USER -> if (resultCode == Activity.RESULT_OK) {
				val file = getFile(requestCode)
				ConfigurationUtil.customUserImage = file.absolutePath
				LayoutRefreshConfigUtil.isChangeUserImage = true
				Toast.makeText(activity, R.string.hint_custom_img, Toast.LENGTH_SHORT)
						.show()
			}
			REQUEST_CROP_BACKGROUND -> if (resultCode == Activity.RESULT_OK) {
				val file = getFile(requestCode)
				ConfigurationUtil.customBackgroundImage = file.absolutePath
				LayoutRefreshConfigUtil.isChangeBackgroundImage = true
				Toast.makeText(activity, R.string.hint_custom_img, Toast.LENGTH_SHORT)
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
				.start(activity, this, cropCode)
	}
}