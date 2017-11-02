package com.weilylab.xhuschedule.activity

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.RT
import com.weilylab.xhuschedule.util.FileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.io.IOException

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

		username_sign_in_button.setOnClickListener { attemptLogin() }
	}

	private fun loadVcode()
	{
//		Glide.with(this)
//				.load(getString(R.string.url_vcode))
//				.diskCacheStrategy(DiskCacheStrategy.NONE)
//				.into(vcode_image_view)

		val request = Request.Builder()
				.url(getString(R.string.url_vcode))
				.build()

		val observer = object : Observer<File>
		{
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
			}

			override fun onNext(file: File)
			{
				Logs.i(TAG, "onNext: ")
				Glide.with(this@LoginActivity)
						.load(file)
						.asBitmap()
						.into(vcode_image_view)
			}
		}

		val observable = Observable.create<File> { subscriber ->
			val response = client.newCall(request).execute()
			if (!response.isSuccessful) throw IOException("Unexpected code " + response)

			val responseHeaders = response.headers()
			for (i in 0 until responseHeaders.size())
			{
				Logs.i(TAG, "loadVcode: " + responseHeaders.name(i))
				Logs.i(TAG, "loadVcode: " + responseHeaders.value(i))
			}
			val file = File(cacheDir.absolutePath + "/vcode")
			FileUtil.saveFile(response.body().byteStream(), file)
			subscriber.onNext(file)
			subscriber.onComplete()
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

		val observer = object : Observer<RT>
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

			override fun onNext(rt: RT)
			{
				Logs.i(TAG, "onNext: " + rt.rt)
			}
		}

		val observable = Observable.create<RT> { subscriber ->
			val params = HashMap<String, String>()
			params.put("username", usernameStr)
			params.put("password", passwordStr)
			params.put("vcode", vcodeStr)
			Logs.i(TAG, "login: " + usernameStr)
			Logs.i(TAG, "login: " + passwordStr)
			Logs.i(TAG, "login: " + vcodeStr)

			val requestBody = FormBody.Builder()
					.add("username", usernameStr)
					.add("password", passwordStr)
					.add("vcode", vcodeStr)
					.build()
			val request = Request.Builder()
					.url(getString(R.string.url_login))
					.post(requestBody)
					.build()
			val response = client.newCall(request).execute()
			if (!response.isSuccessful) throw IOException("Unexpected code " + response)
			Logs.i(TAG, "login: " + response.body().string())
		}

		observable.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}
}
