package com.weilylab.xhuschedule.ui.adapter

import android.view.View
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemFeedBackMessageBinding
import com.weilylab.xhuschedule.model.FeedBackMessage
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter
import java.text.SimpleDateFormat
import java.util.*

class FeedBackMessageAdapter : BaseBindingRecyclerViewAdapter<FeedBackMessage, ItemFeedBackMessageBinding>(R.layout.item_feed_back_message) {
	private val simpleDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.CHINA) }
	private val showDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }
	private val showTimeFormat by lazy { SimpleDateFormat("HH:mm:ss", Locale.CHINA) }

	override fun setItemView(binding: ItemFeedBackMessageBinding, position: Int, data: FeedBackMessage) {
		binding.message = data
		if (data.status == -1) {
			binding.progressBar.visibility = View.VISIBLE
		} else {
			binding.progressBar.visibility = View.GONE
		}
		when {
			data.sender == "System" -> {
				binding.receivedTextView.visibility = View.VISIBLE
				binding.sendTextView.visibility = View.GONE
			}
			data.receiver == "System" -> {
				binding.receivedTextView.visibility = View.GONE
				binding.sendTextView.visibility = View.VISIBLE
			}
		}
		val index = items.indexOf(data)
		if (index > 0) {
			val lastMessage = items[index - 1]
			val lastMessageTimeString = lastMessage.createTime
			val lastMessageTime = simpleDateFormat.parse(lastMessageTimeString).time
			val thisMessageTimeString = data.createTime
			val thisMessageTime = simpleDateFormat.parse(thisMessageTimeString).time
			if (thisMessageTime - lastMessageTime > 10 * 60 * 1000)
				showDateAndTime(binding.dateTextView, thisMessageTime)
			else
				binding.dateTextView.alpha = 0F
		} else
			showDateAndTime(binding.dateTextView, simpleDateFormat.parse(data.createTime).time)
	}

	private fun showDateAndTime(textView: TextView, time: Long) {
		val calendar = Calendar.getInstance()
		calendar.timeInMillis = time
		if (showDateFormat.format(calendar.time) == showDateFormat.format(Calendar.getInstance().time))
			textView.text = showTimeFormat.format(calendar.time)
		else
			textView.text = showDateFormat.format(calendar.time)
		textView.alpha = 1F
	}
}