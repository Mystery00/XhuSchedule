/*
 * Created by Mystery0 on 6/20/18 1:15 PM.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 6/20/18 1:15 PM
 */

package com.weilylab.xhuschedule.handler

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.*
import com.weilylab.xhuschedule.adapter.ShareWithFriendsAdapter
import com.weilylab.xhuschedule.classes.baseClass.CETScore
import com.weilylab.xhuschedule.classes.baseClass.Operation
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.databinding.DialogShareWithFriendsBinding
import com.weilylab.xhuschedule.listener.FeedBackListener
import com.weilylab.xhuschedule.listener.GetCETScoresListener
import com.weilylab.xhuschedule.listener.GetCETVCodeListener
import com.weilylab.xhuschedule.newPackage.ui.activity.NoticeActivity
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.content_main.*
import vip.mystery0.logs.Logs
import java.io.File

class ItemOperationHandler(val context: Context) : BaseClickHandler<Operation>() {
	override fun click(view: View, any: Operation) {
		when (any.title) {
			context.getString(R.string.operation_notice) ->
				(context as MainActivity).startActivityForResult(Intent(context, NoticeActivity::class.java), context.noticeActivityCode)
			context.getString(R.string.operation_schedule) ->
				context.startActivity(Intent(context, ScheduleActivity::class.java))
			context.getString(R.string.operation_exam) ->
				context.startActivity(Intent(context, ExamActivity::class.java))
			context.getString(R.string.operation_score) ->
				context.startActivity(Intent(context, ScoreActivity::class.java))
			context.getString(R.string.operation_score_cet) -> {
				val loadingDialog = ZLoadingDialog(context)
						.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
						.setHintText(context.getString(R.string.hint_dialog_cet))
						.setHintTextSize(16F)
						.setCanceledOnTouchOutside(false)
						.setLoadingColor(ContextCompat.getColor(context, R.color.colorAccent))
						.setHintTextColor(ContextCompat.getColor(context, R.color.colorAccent))
						.create()
				val studentList = XhuFileUtil.getArrayFromFile(File(context.filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java)
				var mainStudent: Student? = (0 until studentList.size)
						.firstOrNull { studentList[it].isMain }
						?.let { studentList[it] }
				if (mainStudent == null)
					mainStudent = studentList[0]
				val layout = View.inflate(context, R.layout.dialog_get_cet_scores, null)
				val idInput: TextInputLayout = layout.findViewById(R.id.input_id)
				val nameInput: TextInputLayout = layout.findViewById(R.id.input_name)
				nameInput.editText!!.setText(mainStudent.profile?.name)
				val dialog = AlertDialog.Builder(context)
						.setTitle(R.string.operation_score_cet)
						.setView(layout)
						.setPositiveButton(android.R.string.ok, null)
						.setNegativeButton(android.R.string.cancel, null)
						.create()
				dialog.show()
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
					if (idInput.editText!!.text.toString().isEmpty() || nameInput.editText!!.text.toString().isEmpty()) {
						Toast.makeText(context, R.string.hint_cet_empty, Toast.LENGTH_SHORT)
								.show()
					} else {
						loadingDialog.show()
						val view = View.inflate(context, R.layout.dialog_vcode, null)
						val imageView: ImageView = view.findViewById(R.id.imageView)
						val editText: EditText = view.findViewById(R.id.editText)
						imageView.setOnClickListener {
							showVCode(mainStudent!!, idInput.editText!!.text.toString(), view, loadingDialog)
						}
						showVCode(mainStudent!!, idInput.editText!!.text.toString(), view, loadingDialog)
						AlertDialog.Builder(context)
								.setTitle(" ")
								.setView(view)
								.setPositiveButton(android.R.string.ok) { _, _ ->
									mainStudent!!.getCETScores(idInput.editText!!.text.toString(), nameInput.editText!!.text.toString(), editText.text.toString(), object : GetCETScoresListener {
										override fun error(rt: Int, e: Throwable) {
											Logs.wtf("error: ", e)
											loadingDialog.dismiss()
											Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
													.show()
										}

										override fun got(cetScore: CETScore) {
											loadingDialog.dismiss()
											cetScore.showInView(context)
										}
									})
								}
								.setNegativeButton(android.R.string.cancel, null)
								.show()

					}
				}
			}
			context.getString(R.string.operation_feedback) -> {
				val loadingDialog = ZLoadingDialog(context)
						.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
						.setHintText(context.getString(R.string.hint_dialog_feedback))
						.setHintTextSize(16F)
						.setCanceledOnTouchOutside(false)
						.setLoadingColor(ContextCompat.getColor(context, R.color.colorAccent))
						.setHintTextColor(ContextCompat.getColor(context, R.color.colorAccent))
						.create()
				val layout = View.inflate(context, R.layout.dialog_feedback, null)
				val emailInput: TextInputLayout = layout.findViewById(R.id.input_email)
				val textInput: TextInputLayout = layout.findViewById(R.id.input_text)
				val dialog = AlertDialog.Builder(context)
						.setTitle(R.string.operation_feedback)
						.setView(layout)
						.setPositiveButton(android.R.string.ok, null)
						.setNegativeButton(android.R.string.cancel, null)
						.create()
				dialog.show()
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
					if (emailInput.editText!!.text.toString().isEmpty() || textInput.editText!!.text.toString().isEmpty()) {
						Toast.makeText(context, R.string.hint_feedback_empty, Toast.LENGTH_SHORT)
								.show()
					} else {
						loadingDialog.show()
						val studentList = XhuFileUtil.getArrayFromFile(File(context.filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java)
						var mainStudent: Student? = (0 until studentList.size)
								.firstOrNull { studentList[it].isMain }
								?.let { studentList[it] }
						if (mainStudent == null)
							mainStudent = studentList[0]
						mainStudent!!.feedback(context, emailInput.editText!!.text.toString(), textInput.editText!!.text.toString(), object : FeedBackListener {
							override fun error(rt: Int, e: Throwable) {
								Logs.wtf("error: ", e)
								loadingDialog.dismiss()
								Toast.makeText(context, context.getString(R.string.hint_feedback_error, rt, e.message), Toast.LENGTH_LONG)
										.show()
							}

							override fun done(rt: Int) {
								loadingDialog.dismiss()
								dialog.dismiss()
								Toast.makeText(context, R.string.hint_feedback, Toast.LENGTH_SHORT)
										.show()
							}
						})
					}
				}
			}
			context.getString(R.string.operation_share) -> {
			}
		}
	}

	private fun showVCode(student: Student, id: String, vcodeView: View, loadingDialog: Dialog) {
		val progressBar: ProgressBar = vcodeView.findViewById(R.id.progressBar)
		progressBar.visibility = View.VISIBLE
		student.getCETVCode(id, object : GetCETVCodeListener {
			override fun error(rt: Int, e: Throwable) {
				e.printStackTrace()
				loadingDialog.dismiss()
				Toast.makeText(context, R.string.hint_cet_vcode_error, Toast.LENGTH_SHORT)
						.show()
			}

			override fun got(bitmap: Bitmap?) {
				val imageView: ImageView = vcodeView.findViewById(R.id.imageView)
				progressBar.visibility = View.GONE
				Glide.with(context)
						.load(bitmap)
						.into(imageView)
				loadingDialog.dismiss()
			}
		})
	}
}