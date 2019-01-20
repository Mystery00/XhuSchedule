package com.weilylab.xhuschedule.ui.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.widget.Toast

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
						Toast.makeText(this, "QQ未安装！", Toast.LENGTH_SHORT)
								.show()
				}
			}
		}
		finish()
	}
}