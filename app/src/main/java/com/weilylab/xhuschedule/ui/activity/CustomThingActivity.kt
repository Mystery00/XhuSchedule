package com.weilylab.xhuschedule.ui.activity

import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.ui.adapter.CustomThingAdapter
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.viewmodel.CustomThingViewModel

import kotlinx.android.synthetic.main.activity_custom_thing.*
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status

class CustomThingActivity : XhuBaseActivity(R.layout.activity_custom_thing) {
	private val customThingViewModel: CustomThingViewModel by lazy {
		ViewModelProviders.of(this).get(CustomThingViewModel::class.java)
	}
	private val customThingAdapter: CustomThingAdapter by lazy { CustomThingAdapter(this) }
	private lateinit var viewStubBinding: LayoutNullDataViewBinding

	private val customThingListObserver = Observer<PackageData<List<CustomThing>>> {
		when (it.status) {
			Status.Loading -> showRefresh()
			Status.Content -> {
				hideRefresh()
				hideNoDataLayout()
				customThingAdapter.items.clear()
				customThingAdapter.items.addAll(it.data!!)
				customThingAdapter.notifyDataSetChanged()
			}
			Status.Error -> {
				Logs.wtfm("customThingListObserver: ", it.error)
				hideRefresh()
				hideNoDataLayout()
				toastMessage(it.error?.message)
			}
			Status.Empty -> {
				hideRefresh()
				showNoDataLayout()
			}
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = customThingAdapter
		recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light)
		showRefresh()
	}

	override fun initData() {
		super.initData()
		initViewModel()
	}

	override fun requestData() {
		super.requestData()
		refresh()
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			LayoutRefreshConfigUtil.isRefreshNoticeDot = true
			finish()
		}
		swipeRefreshLayout.setOnRefreshListener {
			refresh()
		}
		nullDataViewStub.setOnInflateListener { _, inflated -> viewStubBinding = DataBindingUtil.bind(inflated)!! }
		floatingActionButton.setOnClickListener { view ->
			Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
		}
	}

	private fun initViewModel() {
		customThingViewModel.customThingList.observe(this, customThingListObserver)
	}

	private fun refresh() {
		val i = CustomThing()
		i.title = "1"
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.items.add(i)
		customThingAdapter.notifyDataSetChanged()
		swipeRefreshLayout.isRefreshing = false
	}

	private fun showRefresh() {
		if (!swipeRefreshLayout.isRefreshing)
			swipeRefreshLayout.isRefreshing = true
	}

	private fun hideRefresh() {
		if (swipeRefreshLayout.isRefreshing)
			swipeRefreshLayout.isRefreshing = false
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
}
