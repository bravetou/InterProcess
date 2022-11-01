package com.brave.inter.process.server.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.brave.inter.process.server.DaoHelper
import com.brave.inter.process.server.entity.StudentEntity
import com.brave.mvvmrapid.utils.launchScope
import kotlinx.coroutines.Dispatchers

class MessengerService : Service() {
    companion object {
        private const val TAG = "MessengerService"
        const val MESSAGE_FROM_CLIENT = 0x01
        const val MESSAGE_FROM_SERVICE = 0x02
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mServiceMessenger.binder
    }

    // 将Messenger和Handler关联起来
    private val mServiceMessenger = Messenger(MessengerHandler())

    private class MessengerHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MESSAGE_FROM_CLIENT -> {
                    val receivedMsg = msg.data.getString("msg")
                    launchScope(Dispatchers.Main.immediate, {
                        GsonUtils.fromJson(receivedMsg, StudentEntity::class.java)?.let {
                            it.id = null
                            DaoHelper.daoSession.insert(it)
                        }
                    })
                    // 打印接收到的客户端消息
                    Log.e(TAG, "ImServer => $receivedMsg")
                    // 给客户端回复一条消息
                    val clientMessenger = msg.replyTo
                    val message = Message.obtain()
                    message.what = MESSAGE_FROM_SERVICE
                    val bundle = Bundle()
                    bundle.putString("msg", "received => $receivedMsg")
                    message.data = bundle
                    try {
                        clientMessenger.send(message)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}