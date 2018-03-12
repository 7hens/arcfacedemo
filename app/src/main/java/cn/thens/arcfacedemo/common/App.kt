package cn.thens.arcfacedemo.common

import android.app.Application
import cn.thens.arcfacedemo.BuildConfig
import rx_activity_result2.RxActivityResult

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        RxActivityResult.register(this)
    }

    companion object {
        lateinit var instance: App
        val debug: Boolean = BuildConfig.DEBUG
    }
}