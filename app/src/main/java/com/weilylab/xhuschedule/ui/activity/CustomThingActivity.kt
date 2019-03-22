package com.weilylab.xhuschedule.ui.activity

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.jrummyapps.android.colorpicker.ColorPickerDialog
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.LayoutAddCustomThingBinding
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.repository.CustomThingRepository
import com.weilylab.xhuschedule.ui.adapter.CustomThingAdapter
import com.weilylab.xhuschedule.utils.AnimationUtil
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.viewmodel.CustomThingViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE

import kotlinx.android.synthetic.main.activity_custom_thing.*
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.Status
import java.text.SimpleDateFormat
import java.util.*

class CustomThingActivity : XhuBaseActivity(R.layout.activity_custom_thing) {
	private val customThingViewModel: CustomThingViewModel by lazy {
		ViewModelProviders.of(this).get(CustomThingViewModel::class.java)
	}
	private val customThingAdapter: CustomThingAdapter by lazy { CustomThingAdapter(this) }
	private lateinit var viewStubBinding: LayoutNullDataViewBinding
	private val customThingBinding by lazy { LayoutAddCustomThingBinding.inflate(LayoutInflater.from(this)) }
	private val bottomSheetDialog by lazy { BottomSheetDialog(this) }
	private val dateFormatter by lazy { SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA) }
	private val timeFormatter by lazy { SimpleDateFormat("HH:mm", Locale.CHINA) }
	private val saveDateTimeFormatter by lazy { SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA) }
	private var isUpdate = false
	private var collapsedHeight = 0
	private var expandedHeight = 0
	private val syncDialog: Dialog by lazy {
		ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_sync))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(this, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}

	private val customThingListObserver = Observer<PackageData<List<CustomThing>>> {
		when (it.status) {
			Status.Loading -> showRefresh()
			Status.Content -> {
				hideRefresh()
				customThingAdapter.items.clear()
				customThingAdapter.items.addAll(it.data!!)
				customThingAdapter.notifyDataSetChanged()
				checkData()
			}
			Status.Error -> {
				Logs.wtfm("customThingListObserver: ", it.error)
				hideRefresh()
				checkData()
				toastMessage(it.error?.message)
			}
			Status.Empty -> {
				hideRefresh()
				showNoDataLayout()
			}
		}
	}

	private val statusObserver = Observer<PackageData<Boolean>> {
		when (it.status) {
			Status.Loading -> showSyncDialog()
			Status.Content -> {
				hideSyncDialog()
				//false 表示上传到服务器，true 表示下载到本地
				//表示是否需要在操作之后刷新列表数据
				if (it.data!!) {
					refresh()
					Snackbar.make(coordinatorLayout, R.string.hint_sync_done, Snackbar.LENGTH_SHORT)
							.show()
				} else {
					Snackbar.make(coordinatorLayout, R.string.hint_sync_done, Snackbar.LENGTH_SHORT)
							.show()
				}
			}
			Status.Error -> {
				Logs.wtfm("customCourseListObserver: ", it.error)
				hideSyncDialog()
				toastMessage(it.error?.message)
			}
			Status.Empty -> {
			}
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = customThingAdapter
		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light)
		swipeRefreshLayout.setDistanceToTriggerSync(100)
		initAddLayout()
		initExpand()
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
		floatingActionButton.setOnClickListener {
			showAddLayout()
		}
		customThingAdapter.setOnClickListener { showAddLayout(it) }
		ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
			override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
				return false
			}

			override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
				val position = viewHolder.adapterPosition
				val item = customThingAdapter.items.removeAt(position)
				customThingAdapter.notifyItemRemoved(position)
				checkData()
				Snackbar.make(coordinatorLayout, R.string.hint_delete_done_snackbar, Snackbar.LENGTH_LONG)
						.setAction(R.string.action_cancel_do) {
							customThingAdapter.items.add(position, item)
							customThingAdapter.notifyItemInserted(position)
							checkData()
						}
						.addCallback(object : Snackbar.Callback() {
							override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
								if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
									CustomThingRepository.delete(item) {
										LayoutRefreshConfigUtil.isRefreshTodayFragment = true
									}
									super.onDismissed(transientBottomBar, event)
								}
							}
						})
						.show()
			}
		}).attachToRecyclerView(recyclerView)
		expandClose.setOnClickListener {
			collapse()
			expandClose.visibility = View.GONE
		}
		expandLayout.setOnClickListener {
			expand()
			expandClose.visibility = View.VISIBLE
		}
	}

	private fun initViewModel() {
		customThingViewModel.customThingList.observe(this, customThingListObserver)
		customThingViewModel.syncCustomThing.observe(this, statusObserver)
	}

	private fun initExpand() {
		expandLayout.post {
			collapsedHeight = expandLayout.measuredHeight
			expandLayout.maxLines = Int.MAX_VALUE
			expandLayout.postInvalidate()
			expandLayout.post {
				expandedHeight = expandLayout.measuredHeight
			}
		}
	}

	private fun refresh() {
		showRefresh()
		CustomThingRepository.getAll(customThingViewModel)
	}

	private fun initAddLayout() {
		bottomSheetDialog.setContentView(customThingBinding.root)
		bottomSheetDialog.setCancelable(true)
		bottomSheetDialog.setCanceledOnTouchOutside(true)
		customThingBinding.imageViewClose.setOnClickListener {
			bottomSheetDialog.dismiss()
		}
		customThingBinding.switchAllDay.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked) {
				customThingBinding.textViewStartTime.visibility = View.GONE
				customThingBinding.textViewEndTime.visibility = View.GONE
			} else {
				customThingBinding.textViewStartTime.visibility = View.VISIBLE
				customThingBinding.textViewEndTime.visibility = View.VISIBLE
			}
		}
		customThingBinding.textViewStartDate.setOnClickListener {
			val originText = customThingBinding.textViewStartDate.text.toString()
			val s = originText.substring(0, originText.length - 2)
			val now = Calendar.getInstance()
			now.time = dateFormatter.parse(s)
			DatePickerDialog(this, { _, year, month, dayOfMonth ->
				val calendar = Calendar.getInstance()
				calendar.timeInMillis = 0
				calendar.set(year, month, dayOfMonth)
				calendar.firstDayOfWeek = Calendar.MONDAY
				val showDate = "${dateFormatter.format(calendar.time)}${CalendarUtil.getWeekIndexInString(calendar.get(Calendar.DAY_OF_WEEK))}"
				customThingBinding.textViewStartDate.text = showDate
			}, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
					.show()
		}
		customThingBinding.textViewStartTime.setOnClickListener {
			val originText = customThingBinding.textViewStartTime.text.toString()
			val now = Calendar.getInstance()
			now.time = timeFormatter.parse(originText)
			TimePickerDialog(this, { _, hourOfDay, minute ->
				val calendar = Calendar.getInstance()
				calendar.timeInMillis = 0
				calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute)
				val showDate = timeFormatter.format(calendar.time)
				customThingBinding.textViewStartTime.text = showDate
			}, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
					.show()
		}
		customThingBinding.textViewEndDate.setOnClickListener {
			val originText = customThingBinding.textViewEndDate.text.toString()
			val s = originText.substring(0, originText.length - 2)
			val now = Calendar.getInstance()
			now.time = dateFormatter.parse(s)
			DatePickerDialog(this, { _, year, month, dayOfMonth ->
				val calendar = Calendar.getInstance()
				calendar.timeInMillis = 0
				calendar.set(year, month, dayOfMonth)
				calendar.firstDayOfWeek = Calendar.MONDAY
				val showDate = "${dateFormatter.format(calendar.time)}${CalendarUtil.getWeekIndexInString(calendar.get(Calendar.DAY_OF_WEEK))}"
				customThingBinding.textViewEndDate.text = showDate
			}, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
					.show()
		}
		customThingBinding.textViewEndTime.setOnClickListener {
			val originText = customThingBinding.textViewEndTime.text.toString()
			val now = Calendar.getInstance()
			now.time = timeFormatter.parse(originText)
			TimePickerDialog(this, { _, hourOfDay, minute ->
				val calendar = Calendar.getInstance()
				calendar.timeInMillis = 0
				calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute)
				val showDate = timeFormatter.format(calendar.time)
				customThingBinding.textViewEndTime.text = showDate
			}, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
					.show()
		}
		customThingBinding.textViewColor.setOnClickListener {
			val color = customThingBinding.imageViewColor.imageTintList!!.defaultColor
			val colorPickerDialog = ColorPickerDialog.newBuilder()
					.setDialogType(ColorPickerDialog.TYPE_PRESETS)
					.setColor(color)
					.setShowAlphaSlider(false)
					.setShowColorShades(false)
					.create()
			colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
				override fun onDialogDismissed(dialogId: Int) {
				}

				override fun onColorSelected(dialogId: Int, color: Int) {
					customThingBinding.imageViewColor.imageTintList = ColorStateList.valueOf(color)
				}
			})
			colorPickerDialog.show(fragmentManager, "custom-thing-color")
		}
	}

	private fun formatInput(binding: LayoutAddCustomThingBinding, customThing: CustomThing, listener: (Boolean) -> Unit) {
		customThing.title = binding.editTextTitle.text.toString()
		if (customThing.title == "")
			customThing.title = getString(R.string.prompt_unlabel)
		customThing.isAllDay = binding.switchAllDay.isChecked
		val start = if (customThing.isAllDay) binding.textViewStartDate.text.substring(0, binding.textViewStartDate.text.length - 2)
		else "${binding.textViewStartDate.text.substring(0, binding.textViewStartDate.text.length - 2)} ${binding.textViewStartTime.text}"
		val end = if (customThing.isAllDay) binding.textViewEndDate.text.substring(0, binding.textViewEndDate.text.length - 2)
		else "${binding.textViewEndDate.text.substring(0, binding.textViewEndDate.text.length - 2)} ${binding.textViewEndTime.text}"
		val startTime = if (customThing.isAllDay) dateFormatter.parse(start) else saveDateTimeFormatter.parse(start)
		val endTime = if (customThing.isAllDay) dateFormatter.parse(end) else saveDateTimeFormatter.parse(end)
		if (startTime.after(endTime)) {
			toastMessage(R.string.error_end_before_start)
			listener.invoke(false)
			return
		}
		customThing.startTime = start
		customThing.endTime = end
		customThing.location = binding.editTextLocation.text.toString()
		customThing.color = ConfigUtil.toHexEncoding(binding.imageViewColor.imageTintList!!.defaultColor)
		customThing.mark = binding.textViewMark.text.toString()
		if (isUpdate)
			CustomThingRepository.update(customThing) { b, t ->
				if (t != null)
					toastMessage(t.message ?: getString(R.string.error_db_action))
				listener.invoke(b)
			}
		else
			CustomThingRepository.save(customThing) { b, t ->
				if (t != null)
					toastMessage(t.message ?: getString(R.string.error_db_action))
				listener.invoke(b)
			}
	}

	private fun expand() {
		if (collapsedHeight == 0 || expandedHeight == 0)
			return
		AnimationUtil.expandLayout(expandLayout, collapsedHeight, expandedHeight)
	}

	private fun collapse() {
		if (collapsedHeight == 0 || expandedHeight == 0)
			return
		AnimationUtil.expandLayout(expandLayout, expandedHeight, collapsedHeight)
	}

	private fun showAddLayout(data: CustomThing? = null) {
		isUpdate = data != null
		if (data != null) {
			customThingBinding.editTextTitle.setText(data.title)
			customThingBinding.switchAllDay.isChecked = data.isAllDay
			val start = Calendar.getInstance()
			val end = Calendar.getInstance()
			start.time = if (data.isAllDay) dateFormatter.parse(data.startTime) else saveDateTimeFormatter.parse(data.startTime)
			end.time = if (data.isAllDay) dateFormatter.parse(data.endTime) else saveDateTimeFormatter.parse(data.endTime)
			val startDate = "${dateFormatter.format(start.time)}${CalendarUtil.getWeekIndexInString(start.get(Calendar.DAY_OF_WEEK))}"
			val endDate = "${dateFormatter.format(end.time)}${CalendarUtil.getWeekIndexInString(end.get(Calendar.DAY_OF_WEEK))}"
			customThingBinding.textViewStartDate.text = startDate
			customThingBinding.textViewEndDate.text = endDate
			customThingBinding.textViewStartTime.text = timeFormatter.format(start.time)
			customThingBinding.textViewEndTime.text = timeFormatter.format(end.time)
			customThingBinding.editTextLocation.setText(data.location)
			customThingBinding.imageViewColor.imageTintList = ColorStateList.valueOf(Color.parseColor(data.color))
			customThingBinding.textViewMark.setText(data.mark)
		} else {
			customThingBinding.editTextTitle.setText("")
			customThingBinding.switchAllDay.isChecked = false
			val start = Calendar.getInstance()
			val end = Calendar.getInstance()
			start.set(Calendar.MINUTE, 0)
			start.set(Calendar.SECOND, 0)
			start.set(Calendar.MILLISECOND, 0)
			end.timeInMillis = start.timeInMillis
			end.add(Calendar.HOUR_OF_DAY, 1)
			val startDate = "${dateFormatter.format(start.time)}${CalendarUtil.getWeekIndexInString(start.get(Calendar.DAY_OF_WEEK))}"
			val endDate = "${dateFormatter.format(end.time)}${CalendarUtil.getWeekIndexInString(end.get(Calendar.DAY_OF_WEEK))}"
			customThingBinding.textViewStartDate.text = startDate
			customThingBinding.textViewEndDate.text = endDate
			customThingBinding.textViewStartTime.text = timeFormatter.format(start.time)
			customThingBinding.textViewEndTime.text = timeFormatter.format(end.time)
			customThingBinding.editTextLocation.setText("")
			customThingBinding.imageViewColor.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
			customThingBinding.textViewMark.setText("")
		}
		customThingBinding.buttonSave.setOnClickListener {
			formatInput(customThingBinding, data
					?: CustomThing()) {
				if (it) {
					bottomSheetDialog.dismiss()
					LayoutRefreshConfigUtil.isRefreshTodayFragment = true
					refresh()
				}
			}
		}
		bottomSheetDialog.show()
	}

	private fun showRefresh() {
		if (!swipeRefreshLayout.isRefreshing)
			swipeRefreshLayout.isRefreshing = true
	}

	private fun hideRefresh() {
		if (swipeRefreshLayout.isRefreshing)
			swipeRefreshLayout.isRefreshing = false
	}

	private fun showSyncDialog() {
		if (!syncDialog.isShowing)
			syncDialog.show()
	}

	private fun hideSyncDialog() {
		if (syncDialog.isShowing)
			syncDialog.dismiss()
	}

	private fun checkData() {
		if (customThingAdapter.items.isEmpty())
			showNoDataLayout()
		else
			hideNoDataLayout()
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

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_custom_course_thing, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		return when (item?.itemId) {
			R.id.action_upload -> {
				CustomThingRepository.syncCustomThingForServer(customThingViewModel)
				true
			}
			R.id.action_download -> {
				CustomThingRepository.syncCustomThingForLocal(customThingViewModel)
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}
}
