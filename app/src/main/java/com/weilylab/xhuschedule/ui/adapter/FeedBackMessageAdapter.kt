package com.weilylab.xhuschedule.ui.adapter

import android.graphics.Color
import android.view.View
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemFeedBackMessageBinding
import com.weilylab.xhuschedule.model.FeedBackMessage
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class FeedBackMessageAdapter : BaseBindingRecyclerViewAdapter<FeedBackMessage, ItemFeedBackMessageBinding>(R.layout.item_feed_back_message) {
	override fun setItemView(binding: ItemFeedBackMessageBinding, position: Int, data: FeedBackMessage) {
		binding.message = data
		if (data.status == -1)
			binding.sendTextView.setBackgroundColor(Color.RED)
		else
			binding.sendTextView.setBackgroundColor(Color.parseColor("#2e85df"))
		when {
			data.sender == "System" ->
				binding.sendTextView.visibility = View.GONE
			data.receiver == "System" ->
				binding.receivedTextView.visibility = View.GONE
		}
	}
}