package com.weilylab.xhuschedule.util.download

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by JokAr-.
 * 原文地址：http://blog.csdn.net/a1018875550/article/details/51832700
 */
class Download : Parcelable
{

	var progress: Int = 0
	var currentFileSize: Long = 0
	var totalFileSize: Long = 0

	override fun describeContents(): Int = 0

	override fun writeToParcel(dest: Parcel, flags: Int)
	{
		dest.writeInt(this.progress)
		dest.writeLong(this.currentFileSize)
		dest.writeLong(this.totalFileSize)
	}

	constructor()
	{
	}

	private constructor(parcel: Parcel)
	{
		this.progress = parcel.readInt()
		this.currentFileSize = parcel.readLong()
		this.totalFileSize = parcel.readLong()
	}

	companion object
	{

		val CREATOR: Parcelable.Creator<Download> = object : Parcelable.Creator<Download>
		{
			override fun createFromParcel(source: Parcel): Download = Download(source)
			override fun newArray(size: Int): Array<Download?> = arrayOfNulls(size)
		}
	}
}