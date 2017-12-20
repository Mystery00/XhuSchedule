/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午3:50
 */

package com.weilylab.xhuschedule.adapter

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jrummyapps.android.colorpicker.ColorPickerDialog
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.listener.ColorPickerChangeListener
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
class ColorPickerAdapter(var color: String,
                         private val context: Context) : RecyclerView.Adapter<ColorPickerAdapter.ViewHolder>() {
    companion object {
        private val TAG = "ColorPickerAdapter"
    }

    private var isChecked = false
    private val arrayOfBitmaps = Array<Bitmap?>(12, { null })

    var colorPickerChangeListener: ColorPickerChangeListener? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 11) {
            arrayOfBitmaps[position] = drawColorCircle()
            if (!isChecked)
                holder.imageView.setImageBitmap(drawColorCircleStroke(arrayOfBitmaps[position]))
            else
                holder.imageView.setImageBitmap(arrayOfBitmaps[position])
            holder.imageView.setOnClickListener {
                val colorPickerDialog = ColorPickerDialog.newBuilder()
                        .setDialogTitle(R.string.app_name)
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(0)
                        .create()
                colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
                    override fun onDialogDismissed(dialogId: Int) {
                    }

                    override fun onColorSelected(dialogId: Int, color: Int) {
                        this@ColorPickerAdapter.color = '#' + Integer.toHexString(color).toUpperCase().substring(2)
                        notifyDataSetChanged()
                        if (colorPickerChangeListener != null)
                            colorPickerChangeListener!!.onColorChanged('#' + Integer.toHexString(color).toUpperCase().substring(2))
                    }
                })
                colorPickerDialog.show((context as Activity).fragmentManager, "color-picker-dialog")
            }
            return
        }
        val color = context.resources.getStringArray(R.array.color_array)[position]
        arrayOfBitmaps[position] = drawCircle(color)
        if (color == this.color) {
            holder.imageView.setImageBitmap(drawCircleStroke(color, arrayOfBitmaps[position]))
            isChecked = true
        } else
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.colorPicker)
    }

    private fun drawCircle(color: String): Bitmap {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.parseColor(color)
        canvas.drawCircle(50F, 50F, 32F, paint)
        return bitmap
    }

    private fun drawCircleStroke(color: String, bitmap: Bitmap?): Bitmap? {
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.parseColor(color)
        paint.strokeWidth = 5F
        paint.style = Paint.Style.STROKE
        canvas.drawArc(RectF(6F, 6F, 94F, 94F), 0F, 360F, false, paint)
        return bitmap
    }

    private fun drawColorCircle(): Bitmap {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        val colors = intArrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.GREEN, Color.RED)
        val positions = FloatArray(5, { i -> 0.25F * i })
        val sweepGradient = SweepGradient(50F, 50F, colors, positions)
        paint.shader = sweepGradient
        canvas.drawCircle(50F, 50F, 32F, paint)
        return bitmap
    }

    private fun drawColorCircleStroke(bitmap: Bitmap?): Bitmap? {
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        val colors = intArrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.GREEN, Color.RED)
        val positions = FloatArray(5, { i -> 0.25F * i })
        val sweepGradient = SweepGradient(50F, 50F, colors, positions)
        paint.shader = sweepGradient
        paint.strokeWidth = 5F
        paint.style = Paint.Style.STROKE
        canvas.drawArc(RectF(6F, 6F, 94F, 94F), 0F, 360F, false, paint)
        return bitmap
    }
}