package com.weilylab.xhuschedule.util

/**
 * Created by zhy on 16/10/7.
 * 合并增量更新包的方法
 */
object BsPatch
{
	init
	{
		System.loadLibrary("bspatch")
	}

	external fun patch(oldApk: String, newApk: String, patch: String): Int
}
