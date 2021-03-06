/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemNoticeBinding
import com.weilylab.xhuschedule.model.Notice
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class NoticeAdapter(private val context: Context) : BaseBindingRecyclerViewAdapter<Notice, ItemNoticeBinding>(R.layout.item_notice) {
    override fun setItemView(binding: ItemNoticeBinding, position: Int, data: Notice) {
        binding.notice = data
    }
}