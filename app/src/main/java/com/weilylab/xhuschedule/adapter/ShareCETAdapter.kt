/*
 * Created by Mystery0 on 18-3-1 下午3:19.
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
 * Last modified 18-2-27 下午7:23
 */

package com.weilylab.xhuschedule.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.sina.weibo.sdk.api.ImageObject
import com.sina.weibo.sdk.api.WeiboMessage
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.MainActivity
import com.weilylab.xhuschedule.util.XhuFileUtil

class ShareCETAdapter(private val context: Context) : RecyclerView.Adapter<ShareCETAdapter.ViewHolder>() {
    private val list = ArrayList<HashMap<String, Int>>()
    var shareView: PopupWindow? = null
    var fileName = ""

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val map = list[position]
        holder.imageView.setImageResource(map["icon"]!!)
        holder.textView.setText(map["title"]!!)
        holder.itemView.setOnClickListener {
            when (holder.adapterPosition) {
                0 -> {//分享到qq
                    val params = Bundle()
                    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, XhuFileUtil.getCETImageFile(fileName).absolutePath)
                    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name))
                    APP.tencent.shareToQQ(context as Activity, params, APP.tencentListener)
                }
                1 -> {//分享到空间
                    val params = Bundle()
                    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, XhuFileUtil.getCETImageFile(fileName).absolutePath)
                    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name))
                    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN)
                    APP.tencent.shareToQQ(context as Activity, params, APP.tencentListener)
                }
                2 -> {//分享到微博
                    val weiboShareAPI = (context as MainActivity).mWeiboShareAPI
                    if (weiboShareAPI.isWeiboAppInstalled) {
                        val weiboMessage = WeiboMessage()
                        val imageObject = ImageObject()
                        imageObject.setImageObject(BitmapFactory.decodeFile(XhuFileUtil.getCETImageFile(fileName).absolutePath))
                        weiboMessage.mediaObject = imageObject
                        val request = SendMessageToWeiboRequest()
                        request.transaction = System.currentTimeMillis().toString()
                        request.message = weiboMessage
                        weiboShareAPI.sendRequest(request)
                    } else {
                        Toast.makeText(context, R.string.hint_no_weibo, Toast.LENGTH_SHORT)
                                .show()
                    }
                }
                3 -> {//分享到微信
                    val wxAPI = (context as MainActivity).wxAPI
                    if (wxAPI.isWXAppInstalled) {
                        val shareBitmap = BitmapFactory.decodeFile(XhuFileUtil.getCETImageFile(fileName).absolutePath)
                        val thumbBmp = Bitmap.createScaledBitmap(shareBitmap, 100, 100, true)
                        val wxMediaMessage = WXMediaMessage()
                        val wxImageObject = WXImageObject(shareBitmap)
                        wxMediaMessage.mediaObject = wxImageObject
                        wxMediaMessage.thumbData = XhuFileUtil.bmpToByteArray(thumbBmp, true)
                        shareBitmap.recycle()
                        val request = SendMessageToWX.Req()
                        request.transaction = "img${System.currentTimeMillis()}"
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
                        val shareBitmap = BitmapFactory.decodeFile(XhuFileUtil.getCETImageFile(fileName).absolutePath)
                        val thumbBmp = Bitmap.createScaledBitmap(shareBitmap, 100, 100, true)
                        val wxMediaMessage = WXMediaMessage()
                        val wxImageObject = WXImageObject(shareBitmap)
                        wxMediaMessage.mediaObject = wxImageObject
                        wxMediaMessage.thumbData = XhuFileUtil.bmpToByteArray(thumbBmp, true)
                        shareBitmap.recycle()
                        val request = SendMessageToWX.Req()
                        request.transaction = "img${System.currentTimeMillis()}"
                        request.message = wxMediaMessage
                        request.scene = SendMessageToWX.Req.WXSceneTimeline
                        wxAPI.sendReq(request)
                    } else {
                        Toast.makeText(context, R.string.hint_no_weixin, Toast.LENGTH_SHORT)
                                .show()
                    }
                }
                5 -> {//系统分享
                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        FileProvider.getUriForFile(context, context.getString(R.string.uri_authority), XhuFileUtil.getCETImageFile(fileName))
                    else
                        Uri.fromFile(XhuFileUtil.getCETImageFile(fileName))
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    shareIntent.type = "image/*"
                    XhuFileUtil.grantUriPermission(context, shareIntent, uri)
                    //设置分享列表的标题，并且每次都显示分享列表
                    context.startActivity(Intent.createChooser(shareIntent, "分享到"))
                }
            }
            shareView?.dismiss()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_share, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var textView: TextView = itemView.findViewById(R.id.textView)
    }
}