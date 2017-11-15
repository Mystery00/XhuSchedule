package com.weilylab.xhuschedule.util;

/**
 * Created by zhy on 16/10/7.
 */
public class BsPatch
{
	static
	{
		System.loadLibrary("bsdiff");
	}
	public static native int patch(String oldApk, String newApk, String patch);
}
