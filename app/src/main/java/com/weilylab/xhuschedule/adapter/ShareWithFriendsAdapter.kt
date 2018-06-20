/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.sina.weibo.sdk.api.ImageObject
import com.sina.weibo.sdk.api.TextObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.MainActivity
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.XhuFileUtil
import vip.mystery0.tools.base.BaseRecyclerViewAdapter

class ShareWithFriendsAdapter(private val context: Context, private val list: ArrayList<HashMap<String, Int>> = ArrayList()) : BaseRecyclerViewAdapter<ShareWithFriendsAdapter.ViewHolder, HashMap<String, Int>>(context, R.layout.item_share, list) {
	var shareView: PopupWindow? = null

	init {
		val titleArray = arrayOf(
				R.string.share_qq,
				R.string.share_qzone,
				R.string.share_weibo,
				R.string.share_weixin,
				R.string.share_friends,
				R.string.share_more
		)
		val imgArray = arrayOf(
				R.drawable.ic_share_qq,
				R.drawable.ic_share_qzone,
				R.drawable.ic_share_weibo,
				R.drawable.ic_share_weixin,
				R.drawable.ic_share_friends,
				R.drawable.ic_share_more
		)
		for (i in 0 until titleArray.size) {
			val map = HashMap<String, Int>()
			map["title"] = titleArray[i]
			map["icon"] = imgArray[i]
			list.add(map)
		}
	}

	override fun setItemView(holder: ViewHolder, position: Int, data: HashMap<String, Int>) {
		holder.imageView.setImageResource(data["icon"]!!)
		holder.textView.setText(data["title"]!!)
		holder.itemView.setOnClickListener {
			when (holder.adapterPosition) {
				0 -> {//分享到qq
					val params = Bundle()
					params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP)
					params.putString(QQShare.SHARE_TO_QQ_TITLE, context.getString(R.string.app_name))
					params.putString(QQShare.SHARE_TO_QQ_SUMMARY, context.getString(R.string.hint_share_message))
					params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Constants.SHARE_TARGET_URL)
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Constants.SHARE_IMAGE_URL)
					params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name))
					APP.tencent.shareToQQ(context as Activity, params, APP.tencentListener)
				}
				1 -> {//分享到空间
					val params = Bundle()
					params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP)
					params.putString(QQShare.SHARE_TO_QQ_TITLE, context.getString(R.string.app_name))
					params.putString(QQShare.SHARE_TO_QQ_SUMMARY, context.getString(R.string.hint_share_message))
					params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Constants.SHARE_TARGET_URL)
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Constants.SHARE_IMAGE_URL)
					params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name))
					params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN)
					APP.tencent.shareToQQ(context as Activity, params, APP.tencentListener)
				}
				2 -> {//分享到微博
					val weiboShareAPI = (context as MainActivity).mWeiboShareAPI
					if (weiboShareAPI.isWeiboAppInstalled) {
						val weiboMultiMessage = WeiboMultiMessage()
						val imageObject = ImageObject()
						imageObject.setImageObject(BitmapFactory.decodeResource(context.resources, R.mipmap.share_launcher))
						weiboMultiMessage.mediaObject = imageObject
						val textObject = TextObject()
						textObject.text = context.getString(R.string.hint_share_message)
						weiboMultiMessage.textObject = textObject
						val request = SendMultiMessageToWeiboRequest()
						request.transaction = System.currentTimeMillis().toString()
						request.multiMessage = weiboMultiMessage
						weiboShareAPI.sendRequest(request)
					} else {
						Toast.makeText(context, R.string.hint_no_weibo, Toast.LENGTH_SHORT)
								.show()
					}
				}
				3 -> {//分享到微信
					val wxAPI = (context as MainActivity).wxAPI
					if (wxAPI.isWXAppInstalled) {
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
				4 -> {//分享到朋友圈
					val wxAPI = (context as MainActivity).wxAPI
					if (wxAPI.isWXAppInstalled) {
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
				5 -> {//系统分享
					val shareIntent = Intent(Intent.ACTION_SEND)
					shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.hint_share_message))
					shareIntent.type = "text/plain"
					//设置分享列表的标题，并且每次都显示分享列表
					context.startActivity(Intent.createChooser(shareIntent, "分享西瓜课表到"))
				}
			}
			shareView?.dismiss()
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(createView(parent))
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var imageView: ImageView = itemView.findViewById(R.id.imageView)
		var textView: TextView = itemView.findViewById(R.id.textView)
	}
}