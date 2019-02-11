package com.weilylab.xhuschedule.ui.fragment

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.FragmentTodayBinding
import com.weilylab.xhuschedule.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.databinding.DialogShowJrscBinding
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.repository.JRSCRepository
import com.weilylab.xhuschedule.ui.adapter.FragmentTodayRecyclerViewAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import vip.mystery0.rxpackagedata.Status.*
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver

class TodayFragment : BaseBottomNavigationFragment<FragmentTodayBinding>(R.layout.fragment_today) {
	companion object {
		fun newInstance() = TodayFragment()
	}

	private val bottomNavigationViewModel: BottomNavigationViewModel by lazy {
		ViewModelProviders.of(activity!!)[BottomNavigationViewModel::class.java]
	}
	private var isInit = false
	private val adapter: FragmentTodayRecyclerViewAdapter by lazy { FragmentTodayRecyclerViewAdapter(activity!!) }
	private lateinit var viewStubBinding: LayoutNullDataViewBinding

	private val todayCourseListObserver = Observer<PackageData<List<Schedule>>> {
		when (it.status) {
			Content -> {
				if (it.data != null) {
					adapter.items.clear()
					adapter.items.addAll(it.data!!)
					adapter.notifyDataSetChanged()
					checkNoDataLayout()
				}
			}
			Empty -> checkNoDataLayout()
			Error -> {
				Logs.wtfm("todayCourseListObserver: ", it.error)
				toastMessage(it.error?.message)
				checkNoDataLayout()
			}
		}
	}

	override fun initView() {
		initViewModel()
		binding.recyclerView.layoutManager = LinearLayoutManager(activity)
		binding.recyclerView.adapter = adapter
		isInit = true
	}

	private fun initViewModel() {
		bottomNavigationViewModel.todayCourseList.observe(activity!!, todayCourseListObserver)
	}

	override fun monitor() {
		super.monitor()
		binding.nullDataViewStub.setOnInflateListener { _, inflated -> viewStubBinding = DataBindingUtil.bind(inflated)!! }
		JRSCRepository.load { data ->
			binding.jrscLayout.visibility = View.VISIBLE
			binding.jrscTextView.text = data.data.content
			val text = "——${data.data.origin.author}《${data.data.origin.title}》"
			binding.jrscAuthorTextView.text = text
			binding.jrscLayout.setOnClickListener {
				val stringBuilder = StringBuilder()
				data.data.origin.content.forEach { s -> stringBuilder.appendln(s) }
				val dialogShowJrscBinding = DialogShowJrscBinding.inflate(LayoutInflater.from(activity))
				val title = "《${data.data.origin.title}》"
				dialogShowJrscBinding.title.text = title
				val author = "[${data.data.origin.dynasty}] ${data.data.origin.author}"
				dialogShowJrscBinding.author.text = author
				dialogShowJrscBinding.content.text = stringBuilder.toString()
				AlertDialog.Builder(activity!!)
						.setView(dialogShowJrscBinding.root)
						.setPositiveButton(android.R.string.ok, null)
						.show()
			}
			checkNoDataLayout()
		}
	}

	private fun checkNoDataLayout() {
		if (binding.jrscLayout.visibility == View.VISIBLE || adapter.items.isNotEmpty())
			hideNoDataLayout()
		else
			showNoDataLayout()
	}

	private fun showNoDataLayout() {
		if (!binding.nullDataViewStub.isInflated)
			binding.nullDataViewStub.viewStub!!.inflate()
		else
			viewStubBinding.root.visibility = View.VISIBLE
		binding.line.visibility = View.GONE
		binding.recyclerView.visibility = View.GONE
	}

	private fun hideNoDataLayout() {
		if (binding.nullDataViewStub.isInflated)
			viewStubBinding.root.visibility = View.GONE
		binding.line.visibility = View.VISIBLE
		binding.recyclerView.visibility = View.VISIBLE
	}

	override fun onResume() {
		super.onResume()
		if (LayoutRefreshConfigUtil.isRefreshTodayFragment) {
			if (!LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity && !LayoutRefreshConfigUtil.isRefreshTableFragment)
				BottomNavigationRepository.queryCacheCourses(bottomNavigationViewModel)
			LayoutRefreshConfigUtil.isRefreshTodayFragment = false
		}
	}

	override fun updateTitle() {
		RxObservable<Boolean>()
				.doThings {
					var num = 0
					while (true) {
						when {
							isInit -> it.onFinish(true)
							num >= 10 -> it.onFinish(false)
						}
						Thread.sleep(200)
						num++
					}
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onFinish(data: Boolean?) {
						if (data != null && data)
							if (bottomNavigationViewModel.currentWeek.value?.data != null && bottomNavigationViewModel.currentWeek.value!!.data!! <= 0) {
								val whenTime = CalendarUtil.whenBeginSchool()
								if (whenTime > 0)
									bottomNavigationViewModel.title.value = "距离开学还有${whenTime}天 ${CalendarUtil.getWeekIndexInString()}"
								else
									bottomNavigationViewModel.title.value = "第${bottomNavigationViewModel.currentWeek.value?.data
											?: "0"}周 ${CalendarUtil.getWeekIndexInString()}"
							} else
								bottomNavigationViewModel.title.value = "第${bottomNavigationViewModel.currentWeek.value?.data
										?: "0"}周 ${CalendarUtil.getWeekIndexInString()}"
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}
}
