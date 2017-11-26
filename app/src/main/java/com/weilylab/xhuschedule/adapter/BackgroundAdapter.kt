package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.weilylab.xhuschedule.R

/**
 * Created by myste.
 */
class BackgroundAdapter(private val context: Context,
                        private val list: Array<String>) : RecyclerView.Adapter<BackgroundAdapter.ViewHolder>() {
    private var checkedListener: CheckListener? = null
    private val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(list[position]).apply(options).into(holder.imageView)
//        holder.imageView.setOnClickListener {
//            checkedListener?.onChecked(position)
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(parent.context, R.layout.item_set_background, parent))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    fun setCheckListener(checkListener: CheckListener) {
        this.checkedListener = checkListener
    }

    interface CheckListener {
        fun onChecked(position: Int)
    }
}