package com.weilylab.xhuschedule.activity

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.interfaces.RTResponse
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

import kotlinx.android.synthetic.main.content_login.*
import vip.mystery0.tools.logs.Logs
import android.animation.ValueAnimator


class LoginActivity : AppCompatActivity()
{
	companion object
	{
		private val TAG = "LoginActivity"
	}

	private val retrofit = ScheduleHelper.getRetrofit()
	private lateinit var vcodeDialog: ZLoadingDialog
	private lateinit var loginDialog: ZLoadingDialog
	private var name = "0"

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)

		initView()

		loadVcode()

		vcode_image_view.setOnClickListener { loadVcode() }
		login_button.setOnClickListener { attemptLogin() }
	}

	private fun initView()
	{
		vcodeDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_update_vcode))
				.setHintTextSize(16F)

		loginDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.STAR_LOADING)
				.setHintText(getString(R.string.hint_dialog_login))
				.setHintTextSize(16F)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			vcodeDialog.setLoadingColor(resources.getColor(R.color.colorAccent, null))
			vcodeDialog.setHintTextColor(resources.getColor(R.color.colorAccent, null))
			loginDialog.setLoadingColor(resources.getColor(R.color.colorAccent, null))
			loginDialog.setHintTextColor(resources.getColor(R.color.colorAccent, null))
		}

		val colorAnim = ObjectAnimator.ofInt(login_form, "backgroundColor", -0x7f80, -0x7f7f01)
		colorAnim.duration = 3000
		colorAnim.setEvaluator(ArgbEvaluator())
		colorAnim.repeatCount = ValueAnimator.INFINITE
		colorAnim.repeatMode = ValueAnimator.REVERSE
		colorAnim.start()
	}

	private fun loadVcode()
	{
		val observer = object : Observer<Bitmap>
		{
			lateinit var bitmap: Bitmap

			override fun onSubscribe(d: Disposable)
			{
				vcodeDialog.show()
			}

			override fun onError(e: Throwable)
			{
				e.printStackTrace()
				vcodeDialog.dismiss()
				Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT)
						.show()
			}

			override fun onComplete()
			{
				vcodeDialog.dismiss()
				vcode_image_view.setImageBitmap(bitmap)
			}

			override fun onNext(bitmap: Bitmap)
			{
				this.bitmap = bitmap
			}
		}

		val observable = Observable.create<Bitmap> { subscriber ->
			val service = retrofit.create(RTResponse::class.java)
			val call = service.getVCodeCall(1)
			val response = call.execute()
			if (response.isSuccessful)
			{
				subscriber.onNext(BitmapFactory.decodeStream(response.body()?.byteStream()))
				subscriber.onComplete()
			}
			else
			{
				Toast.makeText(this@LoginActivity, response.errorBody().toString(), Toast.LENGTH_SHORT)
						.show()
			}
		}

		observable.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}

	private fun attemptLogin()
	{
		username.error = null
		password.error = null
		vcode.error = null

		val usernameStr = username.text.toString()
		val passwordStr = password.text.toString()
		val vcodeStr = vcode.text.toString()

		var cancel = false
		var focusView: View? = null

		when
		{
			TextUtils.isEmpty(usernameStr) ->
			{
				username.error = getString(R.string.error_field_required)
				focusView = username
				cancel = true
			}
			TextUtils.isEmpty(passwordStr) ->
			{
				password.error = getString(R.string.error_field_required)
				focusView = password
				cancel = true
			}
			TextUtils.isEmpty(vcodeStr) ->
			{
				vcode.error = getString(R.string.error_field_required)
				focusView = vcode
				cancel = true
			}
		}

		if (cancel)
		{
			focusView?.requestFocus()
		}
		else
		{
			login()
		}
	}

	private fun login()
	{
		val usernameStr = username.text.toString()
		val passwordStr = password.text.toString()
		val vcodeStr = vcode.text.toString()

		val observer = object : Observer<Int>
		{
			private var result = -1

			override fun onSubscribe(d: Disposable)
			{
				loginDialog.show()
			}

			override fun onError(e: Throwable)
			{
				e.printStackTrace()
				loginDialog.dismiss()
				Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT)
						.show()
			}

			override fun onComplete()
			{
				loginDialog.dismiss()
				when (result)
				{
					0 ->
					{
						ScheduleHelper.isLogin = false
						Toast.makeText(this@LoginActivity, R.string.error_timeout, Toast.LENGTH_SHORT)
								.show()
					}
					1 ->
					{
						ScheduleHelper.isLogin = true
						Toast.makeText(this@LoginActivity, getString(R.string.success_login, name, getString(R.string.app_name)), Toast.LENGTH_SHORT)
								.show()
						startActivity(Intent(this@LoginActivity, MainActivity::class.java))
						finish()
						return
					}
					2 ->
					{
						ScheduleHelper.isLogin = false
						username.error = getString(R.string.error_invalid_username)
						username.requestFocus()
					}
					3 ->
					{
						ScheduleHelper.isLogin = false
						password.error = getString(R.string.error_invalid_password)
						password.requestFocus()
					}
					4 ->
					{
						ScheduleHelper.isLogin = false
						vcode.error = getString(R.string.error_invalid_vcode)
						vcode.requestFocus()
					}
					else ->
					{
						ScheduleHelper.isLogin = false
						Toast.makeText(this@LoginActivity, R.string.error_other, Toast.LENGTH_SHORT)
								.show()
					}
				}
				loadVcode()
			}

			override fun onNext(result: Int)
			{
				Logs.i(TAG, "onNext: ")
				this.result = result
			}
		}

		val observable = Observable.create<Int> { subscriber ->
			val params = HashMap<String, String>()
			params.put("username", usernameStr)
			params.put("password", passwordStr)
			params.put("vcode", vcodeStr)
			val service = retrofit.create(RTResponse::class.java)
			val call = service.loginCall(usernameStr, passwordStr, vcodeStr)
			val response = call.execute()
			if (!response.isSuccessful)
			{
				Logs.i(TAG, "login: 请求失败")
				subscriber.onComplete()
				return@create
			}
			subscriber.onNext(response.body()!!.rt.toInt())
			if (response.body()?.rt == "1")
			{
				val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
				val editor = sharedPreference.edit()
				editor.putString("studentNumber", usernameStr)
				editor.putString("studentName", response.body()?.name)
				name = response.body()?.name!!
				editor.apply()
			}
			subscriber.onComplete()
		}
		observable.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}
}
