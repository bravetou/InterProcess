package com.brave.inter.process.server.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.brave.inter.process.server.DaoHelper
import com.brave.inter.process.server.R
import com.brave.inter.process.server.entity.StudentEntity
import com.brave.inter.process.server.entity.StudentEntityDao
import com.brave.mvvmrapid.core.common.CommonViewModel

class HomeViewModel(application: Application) : CommonViewModel(application) {
    val name = MutableLiveData("")
    val age = MutableLiveData("")

    val content = MutableLiveData("")

    fun add(sex: Int) {
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
            DaoHelper.daoSession.insert(entity).let { id ->
                if (id > 0) {
                    ToastUtils.showLong("添加数据成功")
                } else {
                    ToastUtils.showLong("添加数据失败")
                }
            }
        })
    }

    fun queryByName() {
        val sName = name.value ?: ""
        if (sName.isEmpty()) {
            ToastUtils.showLong(StringUtils.getString(R.string.str_please_enter_name))
            return
        }
        launch(false, {
            val buffer = StringBuffer("根据姓名模糊查询数据 =>\n")
            DaoHelper.daoSession.studentEntityDao
                .queryBuilder()
                .where(StudentEntityDao.Properties.Name.like("%${sName}%"))
                .orderAsc(StudentEntityDao.Properties.Id)
                .list()
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

    fun queryAll() {
        launch(false, {
            val buffer = StringBuffer("全部数据 =>\n")
            DaoHelper.daoSession.studentEntityDao
                .queryBuilder()
                .list()
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