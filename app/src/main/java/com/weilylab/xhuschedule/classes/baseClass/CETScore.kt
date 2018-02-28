/*
 * Created by Mystery0 on 18-2-28 下午10:08.
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
 * Last modified 18-2-28 下午10:08
 */

package com.weilylab.xhuschedule.classes.baseClass

import android.content.Context
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.ViewUtil
import com.weilylab.xhuschedule.util.XhuFileUtil
import java.io.File

class CETScore {
    var id = ""
    var name = ""
    var school = ""
    var total = ""
    var listen = ""
    var read = ""
    var write = ""
    var type = ""
    var oralId = ""//口语 准考证号
    var oralGrade = ""//口语 等级

    fun showInView(context: Context) {
        val scoreView = View.inflate(context, R.layout.dialog_cet_scores, null)
        val textName: TextView = scoreView.findViewById(R.id.textName)
        val textId: TextView = scoreView.findViewById(R.id.textId)
        val textSchool: TextView = scoreView.findViewById(R.id.textSchool)
        val textTotal: TextView = scoreView.findViewById(R.id.textTotal)
        val textListen: TextView = scoreView.findViewById(R.id.textListen)
        val textRead: TextView = scoreView.findViewById(R.id.textRead)
        val textWrite: TextView = scoreView.findViewById(R.id.textWrite)
        val textOralId: TextView = scoreView.findViewById(R.id.textOralId)
        val textOralGrade: TextView = scoreView.findViewById(R.id.textOralGrade)
        textName.text = name
        textId.text = id
        textSchool.text = school
        textTotal.text = total
        textListen.text = listen
        textRead.text = read
        textWrite.text = write
        textOralId.text = oralId
        textOralGrade.text = oralGrade
        val scoreDialog = AlertDialog.Builder(context)
                .setView(scoreView)
                .setPositiveButton(R.string.action_screenshot, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        scoreDialog.show()
        scoreDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val bitmap = ViewUtil.getViewBitmap(scoreView)
            XhuFileUtil.saveBitmapToFile(bitmap, File(Environment.getExternalStorageDirectory(), "test"))
        }
    }

    override fun toString(): String {
        return "CETScore(id='$id', name='$name', school='$school', total='$total', listen='$listen', read='$read', write='$write', type='$type', oralId='$oralId', oralGrade='$oralGrade')"
    }
}