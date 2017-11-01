package com.weilylab.xhuschedule

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.weilylab.xhuschedule.classes.RT
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

import kotlinx.android.synthetic.main.activity_login.*
import vip.mystery0.tools.hTTPok.HTTPok
import vip.mystery0.tools.hTTPok.HTTPokResponse
import vip.mystery0.tools.hTTPok.HTTPokResponseListener
import vip.mystery0.tools.logs.Logs
import java.io.File

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
//			showProgress(true)
		}
	}

	private fun login()
	{
		val observer=object :Observer<RT>
		{
			override fun onSubscribe(d: Disposable)
			{
			}

			override fun onError(e: Throwable)
			{
			}

			override fun onComplete()
			{
			}

			override fun onNext(t: RT)
			{
			}
		}
	}
}
