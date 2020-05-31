/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil.api.load
import com.weilylab.xhuschedule.R

class PlaceholderFragment : Fragment() {
	private var imageID: Int? = null
	private lateinit var imageView: ImageView

	companion object {
		fun newInstance(imageID: Int): PlaceholderFragment {
			val fragment = PlaceholderFragment()
			val args = Bundle()
			args.putInt("image", imageID)
			fragment.arguments = args
			return fragment
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		imageID = arguments?.getInt("image")
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		val rootView = inflater.inflate(R.layout.fragment_welcome, container, false)
		imageView = rootView.findViewById(R.id.imageView)
		return rootView
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		imageID?.let {
			imageView.load(it)
		}
	}
}