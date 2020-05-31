package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.ui.adapter.QueryTestRecyclerViewAdapter
import com.weilylab.xhuschedule.viewmodel.QueryTestViewModel
import kotlinx.android.synthetic.main.activity_query_test.*
import kotlinx.android.synthetic.main.content_query_test.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver

class QueryTestActivity : XhuBaseActivity(R.layout.activity_query_test) {
	private val queryTestViewModel: QueryTestViewModel by viewModel()
	private lateinit var menu: Menu
	private lateinit var viewStubBinding: LayoutNullDataViewBinding
	private val dialog: Dialog by lazy { buildDialog(R.string.hint_dialog_get_tests) }
	private val queryTestRecyclerViewAdapter: QueryTestRecyclerViewAdapter by lazy { QueryTestRecyclerViewAdapter(this) }

	private val queryTestListObserver = object : DataObserver<List<Test>> {
		override fun loading() {
			showDialog()
		}

		override fun contentNoEmpty(data: List<Test>) {
			hideDialog()
			hideNoDataLayout()
			queryTestRecyclerViewAdapter.items.clear()
			queryTestRecyclerViewAdapter.items.addAll(data)
		}

		override fun error(e: Throwable?) {
			Logs.wtfm("queryTestListObserver: ", e)
			hideDialog()
			hideNoDataLayout()
			toastLong(e)
		}

		override fun empty() {
			hideDialog()
			showNoDataLayout()
		}
	}

	private val queryTestHtmlObserver = Observer<String> {
		if (::menu.isInitialized)
			menu.findItem(R.id.action_show_html).isVisible = true
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		recyclerView.layoutManager = LinearLayoutManager(this)
		val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
		dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_query_test)!!)
		recyclerView.addItemDecoration(dividerItemDecoration)
		recyclerView.adapter = queryTestRecyclerViewAdapter
	}

	override fun initData() {
		super.initData()
		initViewModel()
		queryTestViewModel.init()
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
		nullDataViewStub.setOnInflateListener { _, inflated -> viewStubBinding = DataBindingUtil.bind(inflated)!! }
	}

	private fun initViewModel() {
		queryTestViewModel.studentList.observe(this, Observer {
			generateStudentMenuList(it)
		})
		queryTestViewModel.student.observe(this, Observer {
			if (it == null) {
				toastLong(R.string.hint_action_not_login)
				finish()
			}
		})
		queryTestViewModel.testList.observe(this, queryTestListObserver)
		queryTestViewModel.html.observe(this, queryTestHtmlObserver)
	}

	private fun queryTestForStudent(student: Student) {
		queryTestViewModel.query(student)
	}

	private fun generateStudentMenuList(list: List<Student>) {
		launch(Dispatchers.Default) {
			repeat(3) {
				if (!::menu.isInitialized) {
					Thread.sleep(500)
				}
			}
			if (!::menu.isInitialized) {
				return@launch
			}
			withContext(Dispatchers.Main) {
				val groupId = 1
				var nowCheckId = 0
				list.forEachIndexed { index, student ->
					val itemId = 100 + index
					menu.add(groupId, itemId, 1, "${student.studentName}(${student.username})")
					if (student.username == queryTestViewModel.student.value!!.username)
						nowCheckId = itemId
				}
				menu.setGroupCheckable(groupId, true, true)
				menu.findItem(nowCheckId).isChecked = true
			}
		}
	}

	private fun showDialog() {
		if (!dialog.isShowing)
			dialog.show()
	}

	private fun hideDialog() {
		if (dialog.isShowing)
			dialog.dismiss()
	}

	private fun showNoDataLayout() {
		try {
			nullDataViewStub.inflate()
		} catch (e: Exception) {
			viewStubBinding.root.visibility = View.VISIBLE
		}
		recyclerView.visibility = View.GONE
	}

	private fun hideNoDataLayout() {
		if (::viewStubBinding.isInitialized)
			viewStubBinding.root.visibility = View.GONE
		recyclerView.visibility = View.VISIBLE
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_query_test, menu)
		menu?.let {
			this.menu = menu
			menu.findItem(R.id.action_show_html)?.isVisible = false
		}
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_show_html -> {
				if (!queryTestViewModel.html.value.isNullOrEmpty())
					WebViewActivity.intentTo(this, queryTestViewModel.html.value)
				true
			}
			else -> {
				queryTestViewModel.studentList.value?.forEachIndexed { index, student ->
					val itemId = 100 + index
					if (item.itemId == itemId) {
						queryTestForStudent(student)
						return true
					}
				}
				return super.onOptionsItemSelected(item)
			}
		}
	}
}
