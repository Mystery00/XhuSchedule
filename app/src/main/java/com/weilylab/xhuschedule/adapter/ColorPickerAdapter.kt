package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by myste.
 */
class ColorPickerAdapter(
		private val context: Context) : RecyclerView.Adapter<ColorPickerAdapter.ViewHolder>()
{
	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		holder.circleImageView.circleBackgroundColor = Color.parseColor(context.resources.getStringArray(R.array.color_array)[position])
	}

	override fun onCreateViewHolder(parent: ViewGroup?,
									viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_color_picker, parent, false))

	override fun getItemCount(): Int = 12

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	{
		var circleImageView: CircleImageView = itemView.findViewById(R.id.colorPicker)
	}
}