package com.weilylab.xhuschedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.weilylab.xhuschedule.api.UserAPI
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.model.response.LoginResponse
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource

@Entity(tableName = "tb_student")
class Student {
	@PrimaryKey(autoGenerate = true)
	var id = 0

	@ColumnInfo(name = "username")
	lateinit var username: String
	@ColumnInfo(name = "password")
	lateinit var password: String
	@ColumnInfo(name = "is_main")
	var isMain = false
	@ColumnInfo(name = "student_name")
	var studentName: String = ""

	suspend fun login(): LoginResponse {
		val response = RetrofitFactory.retrofit
				.create(UserAPI::class.java)
				.autoLogin(username, password)
		if (response.rt == ResponseCodeConstants.DONE) {
			//登录成功，存储token
			StudentLocalDataSource.registerFeedBackToken(this, response.fbToken)
		} else {
			throw Exception(response.msg)
		}
		return response
	}

	suspend fun <R> reLogin(block: suspend () -> R): R {
		login()
		return block()
	}
}