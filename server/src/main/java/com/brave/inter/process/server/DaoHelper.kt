package com.brave.inter.process.server

import android.content.Context
import com.brave.inter.process.server.entity.DaoMaster
import com.brave.inter.process.server.entity.DaoSession

object DaoHelper {
    private var mDaoSession: DaoSession? = null

    fun init(context: Context) {
        if (null == mDaoSession) {
            synchronized(this) {
                if (null == mDaoSession) {
                    val devOpenHelper = DaoMaster.DevOpenHelper(context, "test.db", null)
                    val daoMaster = DaoMaster(devOpenHelper.writableDb)
                    mDaoSession = daoMaster.newSession()
                }
            }
        }
    }

    val daoSession: DaoSession
        get() = mDaoSession ?: error("Please initialize the database first")
}