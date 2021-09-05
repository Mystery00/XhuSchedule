/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.event.UI
import com.weilylab.xhuschedule.model.event.UIConfigEvent
import com.weilylab.xhuschedule.ui.adapter.FeedBackMessageAdapter
import com.weilylab.xhuschedule.viewmodel.FeedBackViewModel
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.content_feedback.*
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.rx.DataObserver
import vip.mystery0.tools.ResourceException

class FeedbackActivity : XhuBaseActivity(R.layout.activity_feedback) {
    private val feedBackViewModel: FeedBackViewModel by viewModel()
    private val eventBus: EventBus by inject()
    private val dialog: Dialog by lazy { buildDialog(R.string.hint_dialog_init) }
    private val feedBackMessageAdapter: FeedBackMessageAdapter by lazy { FeedBackMessageAdapter() }
    private var isRefreshByManual = true

    private val feedBackMessageObserver = object : DataObserver<List<FeedBackMessage>> {
        override fun contentNoEmpty(data: List<FeedBackMessage>) {
            hideRefresh()
            addMessage(data)
            isRefreshByManual = false
        }

        override fun loading() {
            if (isRefreshByManual)
                showRefresh()
        }

        override fun empty() {
            hideRefresh()
        }

        override fun error(e: Throwable?) {
            Log.e(TAG, "error: ", e)
            hideRefresh()
            toastLong(e)
            if (e is ResourceException && feedBackViewModel.mainStudent.value == null) {
                finish()
            }
        }
    }

    override fun initView() {
        super.initView()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = feedBackMessageAdapter
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
        disableInput()
    }

    override fun initData() {
        super.initData()
        initViewModel()
        showInitDialog()
        feedBackViewModel.init()
    }

    private fun initViewModel() {
        feedBackViewModel.mainStudent.observe(this, Observer {
            hideInitDialog()
            if (it == null) {
                toastLong(R.string.hint_action_not_login)
                finish()
                return@Observer
            }
            feedBackViewModel.startReceiveMessage(it)
        })
        feedBackViewModel.feedBackMessageList.observe(this, feedBackMessageObserver)
    }

    override fun monitor() {
        super.monitor()
        toolbar.setNavigationOnClickListener {
            finish()
        }
        swipeRefreshLayout.setOnRefreshListener {
            isRefreshByManual = true
            val student = feedBackViewModel.mainStudent.value
            if (student == null) {
                toastLong(R.string.hint_action_not_login)
                return@setOnRefreshListener
            }
            feedBackViewModel.getMessageFromLocal(student)
        }
        buttonSubmit.setOnClickListener {
            if (inputEditText.text.toString().trim() == "")
                toast(R.string.hint_feedback_empty)
            else {
                feedBackViewModel.sendMessage(inputEditText.text.toString())
                inputEditText.setText("")
            }
        }
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 0)
                    disableInput()
                else
                    enableInput()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        feedBackViewModel.stopReceiveMessage()
        eventBus.post(UIConfigEvent(arrayListOf(UI.FEEDBACK_DOT)))
    }

    private fun showInitDialog() {
        if (!dialog.isShowing)
            dialog.show()
    }

    private fun hideInitDialog() {
        if (dialog.isShowing)
            dialog.dismiss()
    }

    private fun showRefresh() {
        if (!swipeRefreshLayout.isRefreshing)
            swipeRefreshLayout.isRefreshing = true
    }

    private fun hideRefresh() {
        if (swipeRefreshLayout.isRefreshing)
            swipeRefreshLayout.isRefreshing = false
        isRefreshByManual = false
    }

    private fun enableInput() {
        buttonSubmit.isEnabled = true
        buttonSubmit.isClickable = true
        buttonSubmit.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent))
    }

    private fun disableInput() {
        buttonSubmit.isEnabled = false
        buttonSubmit.isClickable = false
        buttonSubmit.setColorFilter(ColorPoolHelper.colorPool.uselessColor)
    }

    private fun addMessage(messageList: List<FeedBackMessage>) {
        if (feedBackMessageAdapter.items.isEmpty()) {
            feedBackMessageAdapter.items.clear()
            feedBackMessageAdapter.items.addAll(messageList)
            recyclerView.scrollToPosition(messageList.size - 1)
            return
        }
        if (feedBackMessageAdapter.items.size == messageList.size) {
            var startIndex = -1
            for (i in messageList.indices) {
                val oldMessage = feedBackMessageAdapter.items[i]
                val newMessage = messageList[i]
                if (oldMessage.createTime == newMessage.createTime && oldMessage.id == newMessage.id && oldMessage.id != -1)
                    continue
                else {
                    startIndex = i
                    break
                }
            }
            if (startIndex == -1)
                return
            for (i in startIndex until messageList.size) {
                feedBackMessageAdapter.items[i].replace(messageList[i])
            }
            feedBackMessageAdapter.notifyItemRangeChanged(startIndex, messageList.size - startIndex)
            return
        }
        val lastShowMessage = feedBackMessageAdapter.items[feedBackMessageAdapter.items.size - 1]
        val sameMessage = messageList[feedBackMessageAdapter.items.size - 1]
        if (lastShowMessage.id == sameMessage.id && lastShowMessage.createTime == sameMessage.createTime) {
            feedBackMessageAdapter.items.addAll(
                messageList.subList(
                    feedBackMessageAdapter.items.size,
                    messageList.lastIndex + 1
                )
            )
            recyclerView.scrollToPosition(feedBackMessageAdapter.items.size - 1)
        } else {
            feedBackMessageAdapter.items.clear()
            feedBackMessageAdapter.items.addAll(messageList)
            recyclerView.scrollToPosition(messageList.size - 1)
        }
    }

    companion object {
        private const val TAG = "FeedbackActivity"
    }
}
