/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午3:50
 */

package com.weilylab.xhuschedule.util.download

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by JokAr-.
 * 原文地址：http://blog.csdn.net/a1018875550/article/details/51832700
 */
class Download : Parcelable {
    var progress = 0
    var currentFileSize = 0L
    var totalFileSize = 0L

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.progress)
        dest.writeLong(this.currentFileSize)
        dest.writeLong(this.totalFileSize)
    }

    constructor()

    private constructor(parcel: Parcel) {
        this.progress = parcel.readInt()
        this.currentFileSize = parcel.readLong()
        this.totalFileSize = parcel.readLong()
    }

    companion object {
        val CREATOR: Parcelable.Creator<Download> = object : Parcelable.Creator<Download> {
            override fun createFromParcel(source: Parcel): Download = Download(source)
            override fun newArray(size: Int): Array<Download?> = arrayOfNulls(size)
        }
    }
}