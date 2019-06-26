package com.weilylab.xhuschedule.model.response


import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("url")
                val url: String = "")


data class SchoolCalendarResponse(@SerializedName("code")
                                  val code: Int = 0,
                                  @SerializedName("data")
                                  val data: Data,
                                  @SerializedName("message")
                                  val message: String = "")


