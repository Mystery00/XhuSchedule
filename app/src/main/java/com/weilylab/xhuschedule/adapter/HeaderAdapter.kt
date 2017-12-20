/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-20 下午10:15
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.weilylab.xhuschedule.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class HeaderAdapter(private val context: Context) : RecyclerView.Adapter<HeaderAdapter.ViewHolder>() {
    private val imgArray = context.resources.getStringArray(R.array.header_img)
    var listener: ItemSelectedListener? = null
    private var readyNum = 0
    private val option = RequestOptions()
            .override(640, 352)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
                .load(imgArray[position])
                .apply(option)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        readyNum++
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        readyNum++
                        return false
                    }
                })
                .into((holder.itemView as ImageView))
        holder.itemView.setOnClickListener {
            listener?.onChecked(imgArray[holder.adapterPosition], holder.adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        Observable.create<Boolean> { subscriber ->
            while (true) {
                if (readyNum == imgArray.size)
                    break
                Thread.sleep(100)
            }
            subscriber.onComplete()
        }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Boolean>() {
                    override fun onNext(t: Boolean) {
                    }

                    override fun onComplete() {
                        notifyDataSetChanged()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        notifyDataSetChanged()
                    }
                })

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_choose_img_header, parent, false))
    }

    override fun getItemCount(): Int = imgArray.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    interface ItemSelectedListener {
        fun onChecked(link: String, position: Int)
    }
}