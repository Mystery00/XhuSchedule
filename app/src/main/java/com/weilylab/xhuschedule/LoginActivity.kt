package com.weilylab.xhuschedule

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity()
{

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
			if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL)
			{
				return@OnEditorActionListener true
			}
			false
		})

		username_sign_in_button.setOnClickListener { attemptLogin() }
	}


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private fun attemptLogin()
	{
		username.error = null
		password.error = null

		val usernameStr = username.text.toString()
		val passwordStr = password.text.toString()

		var cancel = false
		var focusView: View? = null

		if (TextUtils.isEmpty(usernameStr))
		{
			username.error = getString(R.string.error_field_required)
			focusView = username
			cancel = true
		}
		else if (TextUtils.isEmpty(passwordStr))
		{
			password.error = getString(R.string.error_field_required)
			focusView = password
			cancel = true
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
}
