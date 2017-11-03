package com.weilylab.xhuschedule.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.LoginRT
import com.weilylab.xhuschedule.classes.RT
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_login.*
import vip.mystery0.tools.hTTPok.HTTPok
import vip.mystery0.tools.hTTPok.HTTPokException
import vip.mystery0.tools.hTTPok.HTTPokResponse
import vip.mystery0.tools.hTTPok.HTTPokResponseListener
import vip.mystery0.tools.logs.Logs

class LoginActivity : AppCompatActivity()
{
	companion object
	{
		private val TAG = "LoginActivity"
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)

		loadVcode()

		vcode_image_view.setOnClickListener { loadVcode() }
		username_sign_in_button.setOnClickListener { attemptLogin() }
	}

	private fun loadVcode()
	{
		val dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE)
				.setLoadingColor(Color.BLACK)
				.setHintText("Login......")
				.setHintTextSize(16F)
				.setHintTextColor(Color.BLACK)

		val observer = object : Observer<Bitmap>
		{
			lateinit var bitmap: Bitmap

			override fun onSubscribe(d: Disposable)
			{
				dialog.show()
			}

			override fun onError(e: Throwable)
			{
				e.printStackTrace()
				dialog.dismiss()
				Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT)
						.show()
			}

			override fun onComplete()
			{
				dialog.dismiss()
				vcode_image_view.setImageBitmap(bitmap)
			}

			override fun onNext(bitmap: Bitmap)
			{
				this.bitmap = bitmap
			}
		}

		val observable = Observable.create<Bitmap> { subscriber ->
			Logs.i(TAG, "loadVcode: ")
			HTTPok().setOkHttpClient(ScheduleHelper.getInstance().client)
					.setURL(getString(R.string.url_vcode))
					.setRequestMethod(HTTPok.GET)
					.setListener(object : HTTPokResponseListener
					{
						override fun onError(message: String?)
						{
							subscriber.onError(HTTPokException(message!!))
						}

						override fun onResponse(response: HTTPokResponse)
						{
							subscriber.onNext(BitmapFactory.decodeStream(response.inputStream))
							subscriber.onComplete()
						}
					})
					.open()
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

		val dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.STAR_LOADING)
				.setLoadingColor(Color.BLACK)
				.setHintText("Login......")
				.setHintTextSize(16F)
				.setHintTextColor(Color.BLACK)

		val observer = object : Observer<String>
		{
			lateinit var message: String

			override fun onSubscribe(d: Disposable)
			{
				dialog.show()
			}

			override fun onError(e: Throwable)
			{
				e.printStackTrace()
				dialog.dismiss()
				Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT)
						.show()
			}

			override fun onComplete()
			{
				dialog.dismiss()
				val gson = Gson()
				var rt: RT = gson.fromJson(message, RT::class.java)
				if (rt.rt == "1")
				{
					rt = gson.fromJson(message, LoginRT::class.java)
					ScheduleHelper.getInstance().isCookieAvailable = true
					Toast.makeText(this@LoginActivity, getString(R.string.success_login, rt.name, getString(R.string.app_name)), Toast.LENGTH_SHORT)
							.show()

					val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
					val editor = sharedPreference.edit()
					editor.putString("studentNumber", usernameStr)
					editor.putString("studentName", rt.name)
					editor.apply()

					startActivity(Intent(this@LoginActivity, MainActivity::class.java))
					finish()
				}
				else
				{
					ScheduleHelper.getInstance().isCookieAvailable = false
					Toast.makeText(this@LoginActivity,
							when (rt.rt)
							{
								"2" -> "用户名错误！"
								"3" -> "密码错误！"
								"4" -> "验证码错误！"
								else -> "登陆错误！"
							}, Toast.LENGTH_SHORT)
							.show()
					loadVcode()
				}
			}

			override fun onNext(message: String)
			{
				this.message = message
			}
		}

		val observable = Observable.create<String> { subscriber ->
			val params = HashMap<String, String>()
			params.put("username", usernameStr)
			params.put("password", passwordStr)
			params.put("vcode", vcodeStr)
			HTTPok().setOkHttpClient(ScheduleHelper.getInstance().client)
					.setURL(getString(R.string.url_login))
					.setRequestMethod(HTTPok.POST)
					.setParams(params)
					.setListener(object : HTTPokResponseListener
					{
						override fun onError(message: String?)
						{
							subscriber.onError(HTTPokException(message!!))
						}

						override fun onResponse(response: HTTPokResponse)
						{
							subscriber.onNext(response.getMessage())
							subscriber.onComplete()
						}
					})
					.open()
		}
		observable.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}
}
