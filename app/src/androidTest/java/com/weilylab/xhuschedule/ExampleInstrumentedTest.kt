/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-10-31 下午6:50
 */

package com.weilylab.xhuschedule

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest
{
	@Test
	fun useAppContext()
	{
		// Context of the app under test.
		val appContext = InstrumentationRegistry.getTargetContext()
		assertEquals("com.weilylab.xhuschedule", appContext.packageName)
	}
}
