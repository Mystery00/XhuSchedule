package com.weilylab.xhuschedule.model.response

import com.weilylab.xhuschedule.model.Test

class TestResponse : BaseResponse() {
	lateinit var tests: List<Test>
}