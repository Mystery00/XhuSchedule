/*
 * Created by Mystery0 on 18-2-27 下午6:59.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-27 下午6:59
 */

package com.weilylab.xhuschedule.constant

object ResponseCodeConstants {
	const val DO_TOO_MANY = "-3"
	const val CATCH_ERROR = "-2"
	const val UNKNOWN_ERROR = "-1"
	const val DONE = "0"

	const val JWC_TIMEOUT = "101"
	const val JWC_BUSY = "102"
	const val JWC_NEED_RATE = "103"

	const val SERVER_WRONG = "201"
	const val SERVER_COURSE_ANALYZE_ERROR = "202"
	const val SERVER_EXAM_ANALYZE_ERROR = "203"
	const val SERVER_SCORE_ANALYZE_ERROR = "204"
	const val SERVER_PROFILE_ANALYZE_ERROR = "205"
	const val SERVER_TOKEN_INVALID_ERROR = "206"
	const val SERVER_TOKEN_ERROR = "207"

	const val VERIFY_SERVER_TIMEOUT = "301"
	const val VERIFY_SERVER_ANAYLYZE_ERROR = "302"

	const val ERROR_USERNAME = "401"
	const val ERROR_PASSWORD = "402"
	const val ERROR_VERIFY_CODE = "403"
	const val ERROR_NOT_LOGIN = "405"
	const val ERROR_API = "406"
}