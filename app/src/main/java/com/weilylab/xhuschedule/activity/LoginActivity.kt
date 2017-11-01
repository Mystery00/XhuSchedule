package com.weilylab.xhuschedule.activity

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
import vip.mystery0.tools.hTTPok.HTTPok
import vip.mystery0.tools.hTTPok.HTTPokException
import vip.mystery0.tools.hTTPok.HTTPokResponse
import vip.mystery0.tools.hTTPok.HTTPokResponseListener
import vip.mystery0.tools.logs.Logs
import java.io.InputStreamReader

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

		username_sign_in_button.setOnClickListener { attemptLogin() }
	}

	private fun loadVcode()
	{
		Glide.with(this)
				.load(getString(R.string.url_vcode))
				.diskCacheStrategy(DiskCacheStrategy.NONE)
				.into(vcode_image_view)
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
			HTTPok().setURL(getString(R.string.url_login))
					.setRequestMethod(HTTPok.GET)
					.setParams(params)
					.setListener(object : HTTPokResponseListener
					{
						override fun onError(message: String?)
						{
							subscriber.onError(HTTPokException(message!!))
						}

						override fun onResponse(response: HTTPokResponse)
						{
							val rt = Gson().fromJson(InputStreamReader(response.inputStream), RT::class.java)
							subscriber.onNext(rt)
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
