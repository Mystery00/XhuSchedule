package com.weilylab.xhuschedule.newPackage.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import com.sina.weibo.sdk.api.ImageObject
import com.sina.weibo.sdk.api.TextObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.share.WbShareHandler
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.config.APP
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.XhuFileUtil

object ShareUtil {
	enum class ShareType {
		QQ, QZONE, WEIBO, WEIXIN, FRIEND, SYSTEM
	}

	fun shareApplication(context: Context, type: ShareType) {
		when (type) {
			ShareType.QQ -> {//分享到qq
				if (PackageUtil.isQQApplicationAvailable()) {
					val params = Bundle()
					params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP)
					params.putString(QQShare.SHARE_TO_QQ_TITLE, context.getString(R.string.app_name))
					params.putString(QQShare.SHARE_TO_QQ_SUMMARY, context.getString(R.string.hint_share_message))
					params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Constants.SHARE_TARGET_URL)
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Constants.SHARE_IMAGE_URL)
					params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name))
					APP.tencent.shareToQQ(APPActivityManager.currentActivity(), params, object : IUiListener {
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
				if (PackageUtil.isQQApplicationAvailable()) {
					val params = Bundle()
					params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP)
					params.putString(QQShare.SHARE_TO_QQ_TITLE, context.getString(R.string.app_name))
					params.putString(QQShare.SHARE_TO_QQ_SUMMARY, context.getString(R.string.hint_share_message))
					params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Constants.SHARE_TARGET_URL)
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Constants.SHARE_IMAGE_URL)
					params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name))
					params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN)
					APP.tencent.shareToQQ(APPActivityManager.currentActivity(), params, object : IUiListener {
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
					val shareHandler = WbShareHandler(APPActivityManager.currentActivity())
					shareHandler.registerApp()
					val weiboMultiMessage = WeiboMultiMessage()
					val imageObject = ImageObject()
					imageObject.setImageObject(BitmapFactory.decodeResource(context.resources, R.mipmap.share_launcher))
					weiboMultiMessage.mediaObject = imageObject
					val textObject = TextObject()
					textObject.text = context.getString(R.string.hint_share_message)
					weiboMultiMessage.textObject = textObject
					shareHandler.shareMessage(weiboMultiMessage, false)
				} else {
					Toast.makeText(context, R.string.hint_no_weibo, Toast.LENGTH_SHORT)
							.show()
				}
			}
			ShareType.WEIXIN -> {//分享到微信
				if (PackageUtil.isWeiXinApplicationAvailable()) {
					val wxAPI = APP.wxAPI
					val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.share_launcher)
					val wxWebpageObject = WXWebpageObject()
					wxWebpageObject.webpageUrl = Constants.SHARE_TARGET_URL

					val wxMediaMessage = WXMediaMessage(wxWebpageObject)
					wxMediaMessage.title = context.getString(R.string.app_name)
					wxMediaMessage.description = context.getString(R.string.hint_share_message)
					val thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
					bitmap.recycle()
					wxMediaMessage.thumbData = XhuFileUtil.bmpToByteArray(thumbBmp, true)

					val request = SendMessageToWX.Req()
					request.transaction = "ShareWithWeiXin${System.currentTimeMillis()}"
					request.message = wxMediaMessage
					request.scene = SendMessageToWX.Req.WXSceneSession
					wxAPI.sendReq(request)
				} else {
					Toast.makeText(context, R.string.hint_no_weixin, Toast.LENGTH_SHORT)
							.show()
				}
			}
			ShareType.FRIEND -> {//分享到朋友圈
				if (PackageUtil.isWeiXinApplicationAvailable()) {
					val wxAPI = APP.wxAPI
					val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.share_launcher)
					val wxWebpageObject = WXWebpageObject()
					wxWebpageObject.webpageUrl = Constants.SHARE_TARGET_URL

					val wxMediaMessage = WXMediaMessage(wxWebpageObject)
					wxMediaMessage.title = context.getString(R.string.app_name)
					wxMediaMessage.description = context.getString(R.string.hint_share_message)
					val thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
					bitmap.recycle()
					wxMediaMessage.thumbData = XhuFileUtil.bmpToByteArray(thumbBmp, true)

					val req = SendMessageToWX.Req()
					req.transaction = "ShareWithFriends${System.currentTimeMillis()}"
					req.message = wxMediaMessage
					req.scene = SendMessageToWX.Req.WXSceneTimeline
					wxAPI.sendReq(req)
				} else {
					Toast.makeText(context, R.string.hint_no_weixin, Toast.LENGTH_SHORT)
							.show()
				}
			}
			ShareType.SYSTEM -> {//系统分享
				val shareIntent = Intent(Intent.ACTION_SEND)
				shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.hint_share_message))
				shareIntent.type = "text/plain"
				//设置分享列表的标题，并且每次都显示分享列表
				context.startActivity(Intent.createChooser(shareIntent, "分享西瓜课表到"))
			}
		}
	}
}