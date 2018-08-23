package com.weilylab.xhuschedule.ui.activity

import android.app.Activity
import android.os.Bundle
import com.weilylab.xhuschedule.utils.ShareUtil

class LinkToWeiXinMiniProgramActivity : Activity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		ShareUtil.linkWeiXinMiniProgram(this)
		finish()
	}
}