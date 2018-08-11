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

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.utils.ShareUtil
import vip.mystery0.tools.base.BaseRecyclerViewAdapter

class ShareWithFriendsAdapter(private val context: Context) : BaseRecyclerViewAdapter<ShareWithFriendsAdapter.ViewHolder, HashMap<String, Int>>(R.layout.item_share) {
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
			val type = when (holder.adapterPosition) {
				0 -> ShareUtil.ShareType.QQ
				1 -> ShareUtil.ShareType.QZONE
				2 -> ShareUtil.ShareType.WEIBO
				3 -> ShareUtil.ShareType.WEIXIN
				4 -> ShareUtil.ShareType.FRIEND
				else -> ShareUtil.ShareType.SYSTEM
			}
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