/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.model

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

    companion object CREATOR : Parcelable.Creator<Download> {
        override fun createFromParcel(parcel: Parcel): Download {
            return Download(parcel)
        }

        override fun newArray(size: Int): Array<Download?> {
            return arrayOfNulls(size)
        }
    }
}