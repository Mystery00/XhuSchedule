package com.weilylab.xhuschedule.model.response

import com.weilylab.xhuschedule.model.Notice

class NoticeResponse : BaseResponse() {
	/**
	 * notices : [{"createTime":"2018-03-25 20:05:00","id":"13","title":"西瓜课表 - 用户调查","type":"1","content":"您好，我们是西瓜课表开发与运维团队。本次调查本着为了给广大西瓜籽们提供更好的软件服务的初衷。希望您能抽出几分钟时间，将您的感受和建议告诉我们，我们非常重视每位用户的宝贵意见，期待您的参与！请点击下面的链接完成问卷： https://wj.qq.com/s/1959730/6f29 \n\n【西华大学计算机学院-西瓜课表开发与运维团队 2018/03/25】","platform":"Android"}]
	 * msg : 成功
	 * rt : 0
	 */
	lateinit var notices: List<Notice>
}
