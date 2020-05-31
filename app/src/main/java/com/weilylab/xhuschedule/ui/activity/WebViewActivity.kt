/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.activity

import android.content.Context
import android.content.Intent
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity

import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.content_web_view.*

class WebViewActivity : XhuBaseActivity(R.layout.activity_web_view) {

	companion object {
		fun intentTo(context: Context, html: String?) {
			if (html != null && html != "")
				context.startActivity(Intent(context, WebViewActivity::class.java)
						.putExtra("html", html))
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	override fun initData() {
		super.initData()
		val html = intent.getStringExtra("html")
		val webSettings = webView.settings
		webSettings.javaScriptEnabled = false
		webSettings.loadWithOverviewMode = true
		webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
		webView.webChromeClient = object : WebChromeClient() {
			override fun onReceivedTitle(view: WebView?, title: String?) {
				super.onReceivedTitle(view, title)
				setTitle(title)
				toolbar.title = title
			}
		}
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
	}
}
