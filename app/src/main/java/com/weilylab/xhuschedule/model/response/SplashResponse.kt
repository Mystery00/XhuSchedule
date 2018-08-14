package com.weilylab.xhuschedule.model.response

class SplashResponse {
	lateinit var results: ArrayList<Splash>

	class Splash {
		/**
		 * splashTime : 4000
		 * isEnable : false
		 * indexID : 7
		 * updatedAt : 2018-07-14T04:40:19.962Z
		 * objectId : 5b128c422f301e00383fbad2
		 * locationUrl : http://www.xbfan.xin/
		 * createdAt : 2018-06-02T12:23:30.675Z
		 * splashUrl : image/splash/20180602.jpg
		 * imageMD5 : 925754dfeb605f9936ba4cb69e872883
		 */
		var splashTime: Long = 0L
		var isEnable: Boolean = false
		lateinit var objectId: String
		lateinit var locationUrl: String
		lateinit var splashUrl: String
		lateinit var imageMD5: String
	}
}
