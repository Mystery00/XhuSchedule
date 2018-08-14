package com.weilylab.xhuschedule.model.response

class StartDateTimeResponse {
	lateinit var results: List<ResultsBean>

	class ResultsBean {
		/**
		 * date : 2018-03-04
		 * createdAt : 2018-07-20T03:32:14.574Z
		 * updatedAt : 2018-07-20T03:32:14.574Z
		 * objectId : 5b5157be2f301e003903408e
		 */

		lateinit var date: String
		lateinit var createdAt: String
		lateinit var updatedAt: String
		lateinit var objectId: String
	}
}
