package com.weilylab.xhuschedule.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import com.oasisfeng.condom.CondomContext
import com.sina.weibo.sdk.WbSdk
import com.sina.weibo.sdk.api.ImageObject
import com.sina.weibo.sdk.api.TextObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.share.WbShareHandler
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.APP
import com.weilylab.xhuschedule.constant.Constants
import vip.mystery0.tools.utils.ActivityManagerTools
import vip.mystery0.tools.utils.FileTools
import kotlin.math.roundToInt

object ShareUtil {
	enum class ShareType {
		QQ, QZONE, WEIBO, WEIXIN, FRIEND, SYSTEM
	}

	val tencent: Tencent? by lazy {
		if (PackageUtil.isQQApplicationAvailable())
			try {
				Tencent.createInstance("1106663023", CondomContext.wrap(APP.context, "Tencent"))
			} catch (ignore: Exception) {
				Tencent.createInstance("1106663023", APP.context)
			}
		else null
	}

	val wxAPI: IWXAPI? by lazy {
		if (PackageUtil.isWeiXinApplicationAvailable())
			try {
				WXAPIFactory.createWXAPI(CondomContext.wrap(APP.context, "WeiXin"), "wx41799887957cbba8", false)
			} catch (ignore: Exception) {
				WXAPIFactory.createWXAPI(APP.context, "wx41799887957cbba8", false)
			}
		else null
	}

	fun shareApplication(context: Context, type: ShareType) {
		when (type) {
			ShareType.QQ -> {//分享到qq
				if (PackageUtil.isQQApplicationAvailable() || tencent != null) {
					val params = Bundle()
					params.putString(QQShare.SHARE_TO_QQ_TITLE, context.getString(R.string.app_name))
					params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getRandomText(context))
					params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Constants.SHARE_TARGET_URL)
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Constants.SHARE_IMAGE_URL)
					params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name))
					tencent!!.shareToQQ(ActivityManagerTools.currentActivity(), params, object : IUiListener {
						override fun onComplete(p0: Any?) {
						}

						override fun onCancel() {
						}

						override fun onError(p0: UiError?) {
						}
					})
				} else {
					Toast.makeText(context, R.string.hint_no_qq, Toast.LENGTH_SHORT)
							.show()
				}
			}
			ShareType.QZONE -> {//分享到空间
				if (PackageUtil.isQQApplicationAvailable() || tencent != null) {
					val params = Bundle()
					params.putString(QQShare.SHARE_TO_QQ_TITLE, context.getString(R.string.app_name))
					params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getRandomText(context))
					params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Constants.SHARE_TARGET_URL)
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Constants.SHARE_IMAGE_URL)
					params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name))
					params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN)
					tencent!!.shareToQQ(ActivityManagerTools.currentActivity(), params, object : IUiListener {
						override fun onComplete(p0: Any?) {
						}

						override fun onCancel() {
						}

						override fun onError(p0: UiError?) {
						}
					})
				} else {
					Toast.makeText(context, R.string.hint_no_qq, Toast.LENGTH_SHORT)
							.show()
				}
			}
			ShareType.WEIBO -> {//分享到微博
				if (PackageUtil.isWeiBoApplicationAvailable()) {
					try {
						WbSdk.install(CondomContext.wrap(APP.context, "WeiBo"), AuthInfo(CondomContext.wrap(APP.context, "WeiBo"), "2170085314", "https://api.weibo.com/oauth2/default.html", "statuses/share"))
					} catch (ignore: Exception) {
						WbSdk.install(CondomContext.wrap(APP.context, "WeiBo"), AuthInfo(APP.context, "2170085314", "https://api.weibo.com/oauth2/default.html", "statuses/share"))
					}
					val shareHandler = WbShareHandler(ActivityManagerTools.currentActivity())
					shareHandler.registerApp()
					val weiboMultiMessage = WeiboMultiMessage()
					val imageObject = ImageObject()
					imageObject.setImageObject(BitmapFactory.decodeResource(context.resources, R.mipmap.share_launcher))
					weiboMultiMessage.mediaObject = imageObject
					val textObject = TextObject()
					textObject.text = getRandomText(context)
					weiboMultiMessage.textObject = textObject
					shareHandler.shareMessage(weiboMultiMessage, false)
				} else {
					Toast.makeText(context, R.string.hint_no_weibo, Toast.LENGTH_SHORT)
							.show()
				}
			}
			ShareType.WEIXIN -> {//分享到微信
				if (PackageUtil.isWeiXinApplicationAvailable() || wxAPI != null) {
					val wxAPI = wxAPI
					val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.share_launcher)
					val wxWebpageObject = WXWebpageObject()
					wxWebpageObject.webpageUrl = Constants.SHARE_TARGET_URL

					val wxMediaMessage = WXMediaMessage(wxWebpageObject)
					wxMediaMessage.title = context.getString(R.string.app_name)
					wxMediaMessage.description = getRandomText(context)
					val thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
					bitmap.recycle()
					wxMediaMessage.thumbData = FileTools.bitmapToByteArray(Bitmap.CompressFormat.PNG, thumbBmp)

					val request = SendMessageToWX.Req()
					request.transaction = "ShareWithWeiXin${System.currentTimeMillis()}"
					request.message = wxMediaMessage
					request.scene = SendMessageToWX.Req.WXSceneSession
					wxAPI!!.sendReq(request)
				} else {
					Toast.makeText(context, R.string.hint_no_weixin, Toast.LENGTH_SHORT)
							.show()
				}
			}
			ShareType.FRIEND -> {//分享到朋友圈
				if (PackageUtil.isWeiXinApplicationAvailable() || wxAPI != null) {
					val wxAPI = wxAPI
					val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.share_launcher)
					val wxWebpageObject = WXWebpageObject()
					wxWebpageObject.webpageUrl = Constants.SHARE_TARGET_URL

					val wxMediaMessage = WXMediaMessage(wxWebpageObject)
					wxMediaMessage.title = context.getString(R.string.app_name)
					wxMediaMessage.description = getRandomText(context)
					val thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
					bitmap.recycle()
					wxMediaMessage.thumbData = FileTools.bitmapToByteArray(Bitmap.CompressFormat.PNG, thumbBmp)

					val req = SendMessageToWX.Req()
					req.transaction = "ShareWithFriends${System.currentTimeMillis()}"
					req.message = wxMediaMessage
					req.scene = SendMessageToWX.Req.WXSceneTimeline
					wxAPI!!.sendReq(req)
				} else {
					Toast.makeText(context, R.string.hint_no_weixin, Toast.LENGTH_SHORT)
							.show()
				}
			}
			ShareType.SYSTEM -> {//系统分享
				val shareIntent = Intent(Intent.ACTION_SEND)
				shareIntent.putExtra(Intent.EXTRA_TEXT, getRandomText(context))
				shareIntent.type = "text/plain"
				//设置分享列表的标题，并且每次都显示分享列表
				context.startActivity(Intent.createChooser(shareIntent, "分享西瓜课表到"))
			}
		}
	}

	fun linkWeiXinMiniProgram(context: Context) {
		if (PackageUtil.isWeiXinApplicationAvailable()) {
			val appID = "wx41799887957cbba8"
			val userName = "gh_90a4144326af"
			val wxAPI = WXAPIFactory.createWXAPI(context, appID)
			val req = WXLaunchMiniProgram.Req()
			req.userName = userName
			req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
			wxAPI.sendReq(req)
		} else {
			Toast.makeText(context, R.string.hint_no_weixin, Toast.LENGTH_SHORT)
					.show()
		}
	}

	private fun getRandomText(context: Context): String {
		val array = context.resources.getStringArray(R.array.hint_share_message)
		val random = (Math.random() * (array.size - 1)).roundToInt()
		return array[random]
	}
}