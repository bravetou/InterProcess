package com.brave.inter.process.server

import com.brave.mvvmrapid.core.CommonApp

class App : CommonApp() {
    override fun onCreate() {
        super.onCreate()
        DaoHelper.init(this)
    }
}