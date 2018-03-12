package cn.thens.arcfacedemo.common

import android.util.Log

interface ILog {
    fun a(cond: Boolean, text: String) {
        if (debug && !cond) Log.e(TAG, text)
    }

    fun e(text: String) {
        if (debug) Log.e(TAG, text)
    }

    fun e(text: String = "", error: Throwable) {
        if (debug) Log.e(TAG, text, error)
    }

    fun w(text: String) {
        if (debug) Log.w(TAG, text)
    }

    fun i(text: String) {
        if (debug) Log.i(TAG, text)
    }

    companion object : ILog {
        private const val TAG = "@"
        private val debug = App.debug
    }
}