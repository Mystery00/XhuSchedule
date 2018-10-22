package com.weilylab.xhuschedule.provider

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.ui.activity.SplashActivity

class TodaySliceProvider : SliceProvider() {
	override fun onCreateSliceProvider(): Boolean = true

	override fun onMapIntentToUri(intent: Intent?): Uri {
		var uriBuilder: Uri.Builder = Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
		if (intent == null) return uriBuilder.build()
		val data = intent.data ?: return uriBuilder.build()
		uriBuilder = uriBuilder.path(data.path)
		if (context != null)
			uriBuilder = uriBuilder.authority(context!!.packageName)
		return uriBuilder.build()
	}

	override fun onBindSlice(sliceUri: Uri): Slice? {
		val context = context ?: return null
		val activityAction = createActivityAction() ?: return null
		return when (sliceUri.path) {
			"/" -> ListBuilder(context, sliceUri, ListBuilder.INFINITY)
					.addRow(ListBuilder.RowBuilder()
							.setTitle("URI found.")
							.setPrimaryAction(activityAction))
					.build()
			"/today.class"->ListBuilder(context, sliceUri, ListBuilder.INFINITY)
					.setHeader(ListBuilder.HeaderBuilder()
							.setTitle("课表")
							.setSubtitle("副标题")
							.setSummary("摘要"))
					.addRow(ListBuilder.RowBuilder()
							.setTitle("今日课程")
							.setPrimaryAction(activityAction))
					.build()
			else -> ListBuilder(context, sliceUri, ListBuilder.INFINITY)
					.addRow(ListBuilder.RowBuilder()
							.setTitle("URI not found.")
							.setPrimaryAction(activityAction))
					.build()
		}
	}

	private fun createActivityAction(): SliceAction? {
		return SliceAction.create(
				PendingIntent.getActivity(context, 0, Intent(context, SplashActivity::class.java), 0),
				IconCompat.createWithResource(context, R.drawable.ic_launcher),
				ListBuilder.ICON_IMAGE,
				"Open App"
		)
	}
}
