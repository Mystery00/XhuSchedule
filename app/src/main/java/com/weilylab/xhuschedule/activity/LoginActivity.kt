package com.weilylab.xhuschedule.activity

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
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.LoginRT
import com.weilylab.xhuschedule.classes.RT
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
import vip.mystery0.tools.hTTPok.HTTPokException
import vip.mystery0.tools.logs.Logs

class LoginActivity : AppCompatActivity()
{
	companion object
	{
		private val TAG = "LoginActivity"
	}

	private val retrofit = ScheduleHelper.getInstance().getRetrofit()
	private lateinit var vcodeDialog: ZLoadingDialog

	private lateinit var loginDialog: ZLoadingDialog

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
				.setHintText("updating......")
				.setHintTextSize(16F)

		loginDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.STAR_LOADING)
				.setHintText("logging in......")
				.setHintTextSize(16F)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			vcodeDialog.setLoadingColor(resources.getColor(R.color.colorAccent, null))
			vcodeDialog.setHintTextColor(resources.getColor(R.color.colorAccent, null))
			loginDialog.setLoadingColor(resources.getColor(R.color.colorAccent, null))
			loginDialog.setHintTextColor(resources.getColor(R.color.colorAccent, null))
		}
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
			Logs.i(TAG, "loadVcode: ")
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
				subscriber.onError(HTTPokException(response.errorBody().toString()))
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

		val observer = object : Observer<String>
		{
			lateinit var message: String

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
			val service = retrofit.create(RTResponse::class.java)
			val call = service.loginCall(usernameStr, passwordStr, vcodeStr)
			val response = call.execute()
			if (response.isSuccessful)
			{
				subscriber.onNext(response.body()?.string()!!)
				subscriber.onComplete()
			}
			else
			{
				subscriber.onError(HTTPokException(response.errorBody().toString()))
			}
		}
		observable.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}
}
