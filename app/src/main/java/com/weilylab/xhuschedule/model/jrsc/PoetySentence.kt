/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */
package com.weilylab.xhuschedule.model.jrsc

import java.io.Serializable

class PoetySentence : Serializable {
	lateinit var data: DataBean

	class DataBean {
		lateinit var content: String
		lateinit var origin: OriginBean

		class OriginBean {
			lateinit var title: String
			lateinit var dynasty: String
			lateinit var author: String
			lateinit var content: List<String>
			var translate: List<String>? = null
		}
	}
}