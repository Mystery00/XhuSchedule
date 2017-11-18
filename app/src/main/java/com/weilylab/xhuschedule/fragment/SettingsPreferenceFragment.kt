package com.weilylab.xhuschedule.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.SettingsActivity
import com.weilylab.xhuschedule.classes.Update
import com.weilylab.xhuschedule.interfaces.UpdateResponse
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.UpdateNotification
import com.weilylab.xhuschedule.view.CustomDatePicker
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_settings.*
import vip.mystery0.tools.logs.Logs
import java.util.*
import java.util.regex.Pattern

/**
 * Created by myste.
 */
class SettingsPreferenceFragment : PreferenceFragment()
{
	companion object
	{
		private val TAG = "SettingsPreferenceFragment"
	}

	private lateinit var coordinatorLayout: CoordinatorLayout
	private lateinit var loadingDialog: ZLoadingDialog
	private lateinit var firstDayPreference: Preference
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

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			loadingDialog.setLoadingColor(resources.getColor(R.color.colorAccent, null))
			loadingDialog.setHintTextColor(resources.getColor(R.color.colorAccent, null))
		}
		firstDayPreference = findPreference(getString(R.string.key_first_day))
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
						calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY -> Snackbar.make(datePicker, R.string.error_time_not_monday, Snackbar.LENGTH_SHORT)
								.show()
						calendar.after(Calendar.getInstance()) -> Snackbar.make(datePicker, R.string.error_time_after, Snackbar.LENGTH_SHORT)
								.show()
						else ->
						{
							Settings.firstWeekOfTerm = datePicker.year.toString() + '-' + datePicker.month.toString() + '-' + datePicker.dayOfMonth.toString()
							firstDayPreference.summary = datePicker.year.toString() + '-' + (datePicker.month + 1).toString() + '-' + datePicker.dayOfMonth.toString()
							dialog.dismiss()
						}
					}
				}
			}
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
							if (code == 1)
								UpdateNotification.notify(activity, update!!.version)
							else
							{
								loadingDialog.dismiss()
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
}