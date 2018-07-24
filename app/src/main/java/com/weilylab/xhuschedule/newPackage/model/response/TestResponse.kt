package com.weilylab.xhuschedule.newPackage.model.response

import com.weilylab.xhuschedule.newPackage.model.Test

class TestResponse : BaseResponse() {
	lateinit var tests: List<Test>
}