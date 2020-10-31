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
import androidx.annotation.NonNull
import cc.shinichi.library.glide.progress.ProgressManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

@GlideModule
class CustomGlideModule : AppGlideModule() {
    override fun registerComponents(@NonNull context: Context, @NonNull glide: Glide,
                                    @NonNull registry: Registry) {
        super.registerComponents(context, glide, registry)
        // 替换底层网络框架为okhttp3，这步很重要！
        // 如果您的app中已经存在了自定义的GlideModule，您只需要把这一行代码，添加到对应的重载方法中即可。
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(ProgressManager.getOkHttpClient()))
    }
}