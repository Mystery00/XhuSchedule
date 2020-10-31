/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.zhihu.matisse.engine.ImageEngine

class CustomGlideEngine : ImageEngine {
    override fun loadImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri?) {
        val requestOptions = RequestOptions()
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
                .fitCenter()
        Glide.with(context)
                .load(uri)
                .apply(requestOptions)
                .into(imageView)
    }

    override fun loadGifImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri?) {
        val requestOptions = RequestOptions()
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(requestOptions)
                .into(imageView)
    }

    override fun supportAnimatedGif(): Boolean = true

    override fun loadGifThumbnail(context: Context, resize: Int, placeholder: Drawable?, imageView: ImageView, uri: Uri?) {
        val requestOptions = RequestOptions()
                .override(resize, resize)
                .centerCrop()
                .placeholder(placeholder)
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(requestOptions)
                .into(imageView)
    }

    override fun loadThumbnail(context: Context, resize: Int, placeholder: Drawable?, imageView: ImageView, uri: Uri?) {
        val requestOptions = RequestOptions()
                .override(resize, resize)
                .centerCrop()
                .placeholder(placeholder)
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(requestOptions)
                .into(imageView)
    }
}