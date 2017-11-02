package com.weilylab.xhuschedule.activity

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
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
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
		private val client = OkHttpClient.Builder()
				.cookieJar(object : CookieJar
				{
					private val cookieStore = HashMap<String, MutableList<Cookie>?>()

					override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>?)
					{
						cookieStore.put(url.host(), cookies)
					}

					override fun loadForRequest(url: HttpUrl): MutableList<Cookie>
					{
						return cookieStore[url.host()] ?: ArrayList()
					}
				})
				.build()
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
		val observer = object : Observer<Bitmap>
		{
			lateinit var bitmap: Bitmap

			override fun onSubscribe(d: Disposable)
			{
				Logs.i(TAG, "onSubscribe: ")
			}

			override fun onError(e: Throwable)
			{
				Logs.i(TAG, "onError: ")
			}

			override fun onComplete()
			{
				Logs.i(TAG, "onComplete: ")
				vcode_image_view.setImageBitmap(bitmap)
			}

			override fun onNext(bitmap: Bitmap)
			{
				Logs.i(TAG, "onNext: ")
				this.bitmap = bitmap
			}
		}

		val observable = Observable.create<Bitmap> { subscriber ->
			HTTPok().setOkHttpClient(client)
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
			override fun onSubscribe(d: Disposable)
			{
				Logs.i(TAG, "onSubscribe: ")
				dialog.show()
			}

			override fun onError(e: Throwable)
			{
				Logs.i(TAG, "onError: ")
				dialog.dismiss()
			}

			override fun onComplete()
			{
				Logs.i(TAG, "onComplete: ")
				dialog.dismiss()
			}

			override fun onNext(message: String)
			{
				Logs.i(TAG, "onNext: ")
				val gson = Gson()
				var rt: RT = gson.fromJson(message, RT::class.java)
				Logs.i(TAG, "onNext: " + rt.rt)
				if (rt.rt == "1")
				{
					rt = gson.fromJson(message, LoginRT::class.java)
					Toast.makeText(this@LoginActivity, rt.name, Toast.LENGTH_SHORT)
							.show()
				}
			}
		}

		val observable = Observable.create<String> { subscriber ->
			val params = HashMap<String, String>()
			params.put("username", usernameStr)
			params.put("password", passwordStr)
			params.put("vcode", vcodeStr)
			Logs.i(TAG, "login: " + usernameStr)
			Logs.i(TAG, "login: " + passwordStr)
			Logs.i(TAG, "login: " + vcodeStr)
			HTTPok().setOkHttpClient(client)
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
