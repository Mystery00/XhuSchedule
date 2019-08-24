package com.weilylab.xhuschedule.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.TestRepository
import com.weilylab.xhuschedule.ui.adapter.QueryTestRecyclerViewAdapter
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import com.weilylab.xhuschedule.viewmodel.QueryTestViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_query_test.*
import kotlinx.android.synthetic.main.content_query_test.*
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.PackageDataObserver
import vip.mystery0.tools.toastLong

class QueryTestActivity : XhuBaseActivity(R.layout.activity_query_test) {
	private val queryTestViewModel: QueryTestViewModel by lazy {
		ViewModelProvider(this)[QueryTestViewModel::class.java]
	}
	private lateinit var menu: Menu
	private lateinit var viewStubBinding: LayoutNullDataViewBinding
	private val dialog: Dialog by lazy {
		ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_get_tests))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(this, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}
	private val queryTestRecyclerViewAdapter: QueryTestRecyclerViewAdapter by lazy { QueryTestRecyclerViewAdapter(this) }

	private val queryStudentListObserver = object : PackageDataObserver<List<Student>> {
		override fun content(data: List<Student>?) {
			generateStudentMenuList(data!!)
			val mainStudent = UserUtil.findMainStudent(data)
			if (mainStudent == null)
				queryTestViewModel.testList.value = PackageData.empty()
			else
				queryTestForStudent(mainStudent)
		}

		override fun error(data: List<Student>?, e: Throwable?) {
			queryTestViewModel.testList.value = PackageData.error(e)
		}

		override fun empty(data: List<Student>?) {
			queryTestViewModel.testList.value = PackageData.empty()
		}

		override fun loading() {
			queryTestViewModel.testList.value = PackageData.loading()
		}
	}

	private val queryTestListObserver = object : PackageDataObserver<List<Test>> {
		override fun loading() {
			showDialog()
		}

		override fun content(data: List<Test>?) {
			hideDialog()
			hideNoDataLayout()
			queryTestRecyclerViewAdapter.items.clear()
			queryTestRecyclerViewAdapter.items.addAll(data!!)
		}

		override fun error(data: List<Test>?, e: Throwable?) {
			Logs.wtfm("queryTestListObserver: ", e)
			hideDialog()
			hideNoDataLayout()
			e.toastLong(this@QueryTestActivity)
		}

		override fun empty(data: List<Test>?) {
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
		TestRepository.queryStudentList(queryTestViewModel)
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
		nullDataViewStub.setOnInflateListener { _, inflated -> viewStubBinding = DataBindingUtil.bind(inflated)!! }
	}

	private fun initViewModel() {
		queryTestViewModel.studentList.observe(this, queryStudentListObserver)
		queryTestViewModel.testList.observe(this, queryTestListObserver)
		queryTestViewModel.html.observe(this, queryTestHtmlObserver)
	}

	private fun queryTestForStudent(student: Student) {
		queryTestViewModel.student.value = student.username
		TestRepository.queryTests(queryTestViewModel, student)
	}

	@SuppressLint("CheckResult")
	private fun generateStudentMenuList(list: List<Student>) {
		Observable.create<Boolean> {
			var index = 0
			while (!::menu.isInitialized) {
				Thread.sleep(500)
				if (index >= 3)
					break
				index++
			}
			it.onNext(::menu.isInitialized)
			it.onComplete()
		}
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe {
					if (it) {
						val groupId = 1
						var nowCheckId = 0
						list.forEachIndexed { index, student ->
							val itemId = 100 + index
							menu.add(groupId, itemId, 1, "${student.studentName}(${student.username})")
							if (student.username == queryTestViewModel.student.value)
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
				queryTestViewModel.studentList.value?.data?.forEachIndexed { index, student ->
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
