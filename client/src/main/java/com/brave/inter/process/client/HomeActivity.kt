package com.brave.inter.process.client

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SizeUtils
import com.brave.inter.process.client.databinding.ActivityHomeBinding
import com.brave.inter.process.server.entity.IStudentService
import com.brave.mvvmrapid.core.common.CommonActivity
import com.brave.mvvmrapid.utils.drawBackground

class HomeActivity : CommonActivity<ActivityHomeBinding, HomeViewModel>() {
    override val variableId: Int
        get() = BR.viewModel

    private val adapter by lazy {
        FunctionAdapter()
    }

    private val data by lazy {
        mutableListOf(
            FunctionBean(0, R.mipmap.icon_dot, "添加(inout)"),
            FunctionBean(1, R.mipmap.icon_dot, "添加(in)"),
            FunctionBean(2, R.mipmap.icon_dot, "添加(out)"),
            FunctionBean(3, R.mipmap.icon_dot, "查询全部")
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter
        adapter.setNewInstance(data)

        adapter.setOnItemClickListener { _, _, position ->
            if (null == studentService || !connected) {
                bindStudentService()
                return@setOnItemClickListener
            }
            val item = adapter.getItem(position)
            when (item.id) {
                3 -> {
                    // 查询全部
                    viewModel.queryAll(studentService)
                }
                else -> {
                    // 添加 0/1/2
                    viewModel.add(studentService, binding.cbWomen.isChecked.sex(), item.id)
                }
            }
        }

        binding.etName.drawBackground()
            .isDrawBackground(true)
            .setBackgroundColorRes(android.R.color.transparent)
            .setBorderColorStr("#9450F0")
            .setBorderWidth(SizeUtils.dp2px(1f))
            .setRoundedCorners(SizeUtils.dp2px(24f))
            .build()
        binding.etAge.drawBackground()
            .isDrawBackground(true)
            .setBackgroundColorRes(android.R.color.transparent)
            .setBorderColorStr("#9450F0")
            .setBorderWidth(SizeUtils.dp2px(1f))
            .setRoundedCorners(SizeUtils.dp2px(24f))
            .build()

        binding.cbMan.isChecked = true
        binding.cbMan.setOnCheckedChangeListener { _, check ->
            binding.cbWomen.isChecked = !check
        }
        binding.cbWomen.setOnCheckedChangeListener { _, check ->
            binding.cbMan.isChecked = !check
        }

        bindStudentService()
    }

    private fun bindStudentService() {
        // AppUtils.launchApp("com.brave.inter.process.server")
        val intent = Intent()
        intent.setPackage("com.brave.inter.process.server")
        intent.action = "com.brave.inter.process.server.service.action"
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    private var studentService: IStudentService? = null

    private var connected = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            studentService = IStudentService.Stub.asInterface(service)
            connected = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            connected = false
        }
    }

    private fun Boolean.sex(): Int = if (this) 0 else 1
}