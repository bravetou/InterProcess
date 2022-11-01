package com.brave.inter.process.client

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.brave.inter.process.server.entity.IStudentService
import com.brave.inter.process.server.entity.StudentEntity
import com.brave.mvvmrapid.core.common.CommonViewModel

class HomeViewModel(application: Application) : CommonViewModel(application) {
    val name = MutableLiveData("")
    val age = MutableLiveData("")

    val content = MutableLiveData("")

    fun add(studentService: IStudentService?, sex: Int, type: Int = 0) {
        val sName = name.value ?: ""
        if (sName.isEmpty()) {
            ToastUtils.showLong(StringUtils.getString(R.string.str_please_enter_name))
            return
        }
        val sAge = age.value ?: ""
        if (sAge.isEmpty()) {
            ToastUtils.showLong(StringUtils.getString(R.string.str_please_enter_age))
            return
        }
        launch(false, {
            val entity = StudentEntity(null, sName, sAge.toInt(), sex)
            studentService?.let { service ->
                when (type) {
                    0 -> service.addStudentInOut(entity)
                    1 -> service.addStudentIn(entity)
                    2 -> service.addStudentOut(entity)
                }
            }
        })
    }

    fun queryAll(studentService: IStudentService?) {
        val service = studentService ?: return
        launch(false, {
            val buffer = StringBuffer("全部数据 =>\n")
            service.queryAll()
                .also {
                    ToastUtils.showLong("查询到${it.size}条数据")
                }
                .forEach {
                    buffer.append(it.id).append(" => ")
                    buffer.append(it.name).append(" => ")
                    buffer.append(it.age).append(" => ")
                    buffer.append(it.sex.sex()).append("\n")
                }
            content.value = buffer.toString()
        })
    }

    private fun Int.sex(): String = if (this == 1) "男" else "女"
}