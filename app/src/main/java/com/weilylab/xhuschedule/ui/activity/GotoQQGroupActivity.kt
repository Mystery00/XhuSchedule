/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.weilylab.xhuschedule.R

class GotoQQGroupActivity : Activity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (intent.data?.host == "goto_qq_group") {
			val key = intent.data!!.getQueryParameter("key")
			if (key != null) {
				try {
					val goIntent = Intent(Intent.ACTION_VIEW, Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key"))
					startActivity(goIntent)
				} catch (e: ActivityNotFoundException) {
					Toast.makeText(this, R.string.hint_no_qq, Toast.LENGTH_SHORT)
							.show()
				}
			}
		}
		finish()
	}
}