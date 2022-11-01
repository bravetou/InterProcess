package com.brave.inter.process.client

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SizeUtils
import com.brave.inter.process.client.databinding.ActivityHomeBinding
import com.brave.inter.process.server.entity.IStudentService
import com.brave.inter.process.server.entity.StudentEntity
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
            FunctionBean(3, R.mipmap.icon_dot, "查询全部"),
            FunctionBean(4, R.mipmap.icon_dot, "通过Messenger添加")
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
                4 -> {
                    if (viewModel.name.value.isNullOrEmpty()) return@setOnItemClickListener
                    if (viewModel.age.value.isNullOrEmpty()) return@setOnItemClickListener
                    // 通过Messenger添加
                    val message = Message.obtain()
                    message.what = MESSAGE_FROM_CLIENT
                    val bundle = Bundle()
                    StudentEntity(
                        null,
                        viewModel.name.value,
                        viewModel.age.value!!.toInt(),
                        binding.cbWomen.isChecked.sex()
                    ).let {
                        bundle.putString("msg", GsonUtils.toJson(it))
                    }
                    message.data = bundle
                    message.replyTo = mClientMessenger
                    try {
                        mServiceMessenger!!.send(message)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
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
        bindMessengerService()
    }

    private fun bindStudentService() {
        // AppUtils.launchApp("com.brave.inter.process.server")
        val intent = Intent()
        intent.setPackage("com.brave.inter.process.server")
        intent.action = "com.brave.inter.process.server.service.action"
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    private fun bindMessengerService() {
        // AppUtils.launchApp("com.brave.inter.process.server")
        val intent = Intent()
        intent.setPackage("com.brave.inter.process.server")
        intent.action = "com.brave.inter.process.server.service.messenger"
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        unbindService(mServiceConnection)
    }

    //-----------------------------------------------------------------------
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

    //-----------------------------------------------------------------------
    companion object {
        private const val TAG = "HomeActivity"
        const val MESSAGE_FROM_CLIENT: Int = 0x01
        const val MESSAGE_FROM_SERVICE = 0x02
    }

    private var mServiceMessenger: Messenger? = null

    private val mClientMessenger = Messenger(MessengerHandler())

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mServiceMessenger = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

    private class MessengerHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MESSAGE_FROM_SERVICE -> Log.e(TAG, "${msg.data.getString("msg")}")
            }
        }
    }
    //-----------------------------------------------------------------------

    private fun Boolean.sex(): Int = if (this) 0 else 1
}