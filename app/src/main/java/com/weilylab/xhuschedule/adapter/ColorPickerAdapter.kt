package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.listener.ColorPickerChangeListener
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
class ColorPickerAdapter(var color: String,
						 private val context: Context) : RecyclerView.Adapter<ColorPickerAdapter.ViewHolder>()
{
	companion object
	{
		private val TAG = "ColorPickerAdapter"
	}

	private var isChecked = false
	private val arrayOfBitmaps = Array<Bitmap?>(12, { null })

	var colorPickerChangeListener: ColorPickerChangeListener? = null

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		val color = context.resources.getStringArray(R.array.color_array)[position]
		arrayOfBitmaps[position] = drawCircle(color)
		if (color == this.color || (!isChecked && position == 11))
		{
			holder.imageView.setImageBitmap(drawCircleStroke(color, arrayOfBitmaps[position]))
			isChecked = true
		}
		else
			holder.imageView.setImageBitmap(arrayOfBitmaps[position])
		holder.imageView.setOnClickListener {
			this.color = color
			notifyDataSetChanged()
			if (colorPickerChangeListener != null)
				colorPickerChangeListener!!.onColorChanged(color)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup?,
									viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_color_picker, parent, false))

	override fun getItemCount(): Int = 12

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	{
		var imageView: ImageView = itemView.findViewById(R.id.colorPicker)
	}

	private fun drawCircle(color: String): Bitmap
	{
		val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
		val canvas = Canvas(bitmap)
		val paint = Paint()
		paint.isAntiAlias = true
		paint.color = Color.parseColor(color)
		canvas.drawCircle(50F, 50F, 32F, paint)
		return bitmap
	}

	private fun drawCircleStroke(color: String, bitmap: Bitmap?): Bitmap?
	{
		val canvas = Canvas(bitmap)
		val paint = Paint()
		paint.isAntiAlias = true
		paint.color = Color.parseColor(color)
		paint.strokeWidth = 5F
		paint.style = Paint.Style.STROKE
		canvas.drawArc(RectF(6F, 6F, 94F, 94F), 0F, 360F, false, paint)
		return bitmap
	}
}