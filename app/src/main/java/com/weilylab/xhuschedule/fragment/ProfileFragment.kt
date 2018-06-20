/*
 * Created by Mystery0 on 18-2-21 下午9:12.
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
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.OperationAdapter
import com.weilylab.xhuschedule.classes.baseClass.Profile
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.weilylab.xhuschedule.activity.LoginActivity
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.XhuFileUtil
import java.util.*

/**
 * Created by myste.
 */
class ProfileFragment : Fragment() {
	companion object {
		fun newInstance(profile: Profile): ProfileFragment {
			val bundle = Bundle()
			bundle.putSerializable(Constants.INTENT_TAG_NAME_PROFILE, profile)
			val fragment = ProfileFragment()
			fragment.arguments = bundle
			return fragment
		}
	}

	private var profile: Profile? = null
	private var rootView: View? = null
	private lateinit var profileImageView: ImageView
	private lateinit var profileTextView: TextView
	private lateinit var recyclerView: RecyclerView
	private lateinit var logoutButton: Button
	private var adapter: OperationAdapter? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		profile = arguments?.getSerializable(Constants.INTENT_TAG_NAME_PROFILE) as Profile
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_profile, container, false)
			profileImageView = rootView!!.findViewById(R.id.profile_img)
			profileTextView = rootView!!.findViewById(R.id.textView_title)
			recyclerView = rootView!!.findViewById(R.id.recycler_view)
			logoutButton = rootView!!.findViewById(R.id.button_logout)
		}
		return rootView
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		recyclerView.layoutManager = LinearLayoutManager(activity)
		val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
		divider.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.lines)!!)
		recyclerView.addItemDecoration(divider)
		adapter = OperationAdapter(activity!!)
		recyclerView.adapter = adapter
		logoutButton.setOnClickListener {
			AlertDialog.Builder(activity!!)
					.setTitle(R.string.hint_logout_title)
					.setMessage(R.string.hint_logout_content)
					.setPositiveButton(android.R.string.ok) { _, _ ->
						val file = XhuFileUtil.getStudentListFile(activity!!)
						if (file.exists())
							file.delete()
						startActivity(Intent(context, LoginActivity::class.java))
					}
					.setNegativeButton(android.R.string.cancel, null)
					.show()
		}
		setProfileImg()
	}

	fun setProfileImg() {
		setProfileImg(0)
	}

	/**
	 * 使用重试机制，每次延时400，重试5次
	 * @param time 当前重试的次数
	 */
	private fun setProfileImg(time: Int) {
		try {
			if (Settings.userImg != "") {
				val options = RequestOptions()
						.signature(MediaStoreSignature("image/*", Calendar.getInstance().timeInMillis, 0))
						.diskCacheStrategy(DiskCacheStrategy.NONE)
				Glide.with(activity!!)
						.load(Settings.userImg)
						.apply(options)
						.into(profileImageView)
			} else
				profileImageView.setImageResource(R.mipmap.profile_img)
		} catch (e: Exception) {
			if (time > 5)
				e.printStackTrace()
			else {
				Timer().schedule(object : TimerTask() {
					override fun run() {
						setProfileImg(time + 1)
					}
				}, 400)
			}
		}
	}

	fun setProfile(profile: Profile) {
		setProfile(profile, 0)
	}

	/**
	 * 使用重试机制，每次延时400，重试5次
	 * @param time 当前重试的次数
	 */
	private fun setProfile(profile: Profile, time: Int) {
		try {
			profileTextView.text = getString(R.string.profile_title, profile.no, profile.name)
			profileTextView.setOnClickListener {
				val stringBuilder = StringBuilder()
						.appendln(getString(R.string.profile_no, profile.no))
						.appendln(getString(R.string.profile_name, profile.name))
						.appendln(getString(R.string.profile_sex, profile.sex))
						.appendln(getString(R.string.profile_grade, profile.grade))
						.appendln(getString(R.string.profile_institute, profile.institute))
						.appendln(getString(R.string.profile_professional, profile.profession))
						.appendln(getString(R.string.profile_classname, profile.classname))
						.appendln(getString(R.string.profile_direction, profile.direction))
				AlertDialog.Builder(activity!!)
						.setTitle(" ")
						.setMessage(stringBuilder.toString())
						.setNegativeButton(android.R.string.ok, null)
						.show()
			}
		} catch (e: Exception) {
			if (time > 5)
				e.printStackTrace()
			else {
				Timer().schedule(object : TimerTask() {
					override fun run() {
						setProfile(profile, time + 1)
					}
				}, 400)
			}
		}
	}

	fun updateNoticeBadge() {
		adapter?.updateBadge()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		if (rootView != null)
			(rootView!!.parent as ViewGroup).removeView(rootView)
	}
}