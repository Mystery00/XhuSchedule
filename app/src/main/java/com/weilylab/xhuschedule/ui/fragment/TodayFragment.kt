package com.weilylab.xhuschedule.ui.fragment

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jinrishici.sdk.android.model.PoetySentence
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.databinding.FragmentTodayBinding
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.repository.JRSCRepository
import com.weilylab.xhuschedule.ui.adapter.FragmentTodayRecyclerViewAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.zhuangfei.timetable.model.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class TodayFragment : BaseBottomNavigationFragment<FragmentTodayBinding>(R.layout.fragment_today) {
	companion object {
		fun newInstance() = TodayFragment()
	}

	private val bottomNavigationViewModel: BottomNavigationViewModel by viewModel()
	private val adapter: FragmentTodayRecyclerViewAdapter by lazy { FragmentTodayRecyclerViewAdapter(requireActivity()) }
	private lateinit var viewStubBinding: LayoutNullDataViewBinding
	private var sortJob: Job? = null

	private val todayCourseListObserver = Observer<List<Schedule>> { data ->
		data?.let { list ->
			sortJob?.cancel()
			adapter.tempList.removeAll { it is Schedule }
			adapter.tempList.addAll(list)
			sortItemList {
				checkNoDataLayout()
			}
		}
	}

	private val customThingListObserver = Observer<List<CustomThing>> { data ->
		data?.let { list ->
			sortJob?.cancel()
			adapter.tempList.removeAll { it is CustomThing }
			adapter.tempList.addAll(list)
			sortItemList {
				checkNoDataLayout()
			}
		}
	}

	override fun initView() {
		initViewModel()
		binding.recyclerView.layoutManager = LinearLayoutManager(activity)
		binding.recyclerView.adapter = adapter
		JRSCRepository.load(requireContext()) { bottomNavigationViewModel.poetySentence.postValue(this) }
	}

	private fun initViewModel() {
		bottomNavigationViewModel.poetySentence.observe(requireActivity(), Observer { sentence ->
			sortJob?.cancel()
			adapter.tempList.removeAll { it is PoetySentence }
			adapter.tempList.add(sentence)
			sortItemList {
				checkNoDataLayout()
			}
		})
		bottomNavigationViewModel.todayCourseList.observe(requireActivity(), todayCourseListObserver)
		bottomNavigationViewModel.customThingList.observe(requireActivity(), customThingListObserver)
	}

	override fun monitor() {
		super.monitor()
		binding.nullDataViewStub.setOnInflateListener { _, inflated -> viewStubBinding = DataBindingUtil.bind(inflated)!! }
	}

	private fun checkNoDataLayout() {
		if (adapter.items.isNotEmpty())
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

	override fun updateTitle() {
		bottomNavigationViewModel.currentWeek.value?.data?.let {
			val shouldShowTomorrow = CalendarUtil.shouldShowTomorrowInfo()
			val whenTime = CalendarUtil.whenBeginSchool(shouldShowTomorrow)
			val weekIndex = CalendarUtil.getWeekIndexInString(if (shouldShowTomorrow) CalendarUtil.getTomorrowIndex() else CalendarUtil.getWeekIndex())
			if (bottomNavigationViewModel.currentWeek.value!!.data!! <= 0 && whenTime > 0) {
				bottomNavigationViewModel.title.postValue(Pair(javaClass, "距离开学还有${whenTime}天 $weekIndex"))
			} else {
				var week = bottomNavigationViewModel.currentWeek.value?.data
				if (week != null && shouldShowTomorrow) {
					val index = CalendarUtil.getWeekIndex()
					if (index == 7)//如果今天是周日，那么明天就是周一,周数加一
						week++
				}
				bottomNavigationViewModel.title.postValue(Pair(javaClass, "第${week ?: "0"}周 $weekIndex"))
			}
		}
	}

	private fun sortItemList(doneListener: () -> Unit) {
		sortJob?.cancel()
		sortJob = bottomNavigationViewModel.viewModelScope.launch {
			withContext(Dispatchers.Default) {
				val poetySentenceList = ArrayList<PoetySentence>()
				val courseList = ArrayList<Schedule>()
				val customThingList = ArrayList<CustomThing>()
				adapter.tempList.forEach {
					when (it) {
						is PoetySentence -> poetySentenceList.add(it)
						is CustomThing -> customThingList.add(it)
						is Schedule -> courseList.add(it)
					}
				}
				val list = ArrayList<Any>(poetySentenceList.size + courseList.size + customThingList.size)
				list.addAll(poetySentenceList)
				if (ConfigurationUtil.showCustomThingFirst) {
					list.addAll(customThingList)
					list.addAll(courseList)
				} else {
					list.addAll(courseList)
					list.addAll(customThingList)
				}
				adapter.items.clear()
				adapter.items.addAll(list)
				list.clear()
				sortJob = null
				doneListener()
			}
		}
	}
}
