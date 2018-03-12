package cn.thens.arcfacedemo.face.arcface

import android.graphics.Bitmap
import cn.thens.arcfacedemo.common.ILog
import cn.thens.arcfacedemo.common.NV21Utils
import com.arcsoft.facedetection.AFD_FSDKEngine
import com.arcsoft.facedetection.AFD_FSDKError
import com.arcsoft.facedetection.AFD_FSDKFace

class FaceDetector(appId: String, sdkKey: String) {
    private val engine: AFD_FSDKEngine by lazy {
        AFD_FSDKEngine().apply {
            AFD_FSDK_InitialFaceEngine(appId, sdkKey, AFD_FSDKEngine.AFD_OPF_0_ONLY, 16, 25).let {
                ILog.a(it.code == AFD_FSDKError.MOK, "AFD_FSDK_InitialFaceEngine fail! error code :" + it.code)
            }
        }
    }

    fun detect(bitmap: Bitmap): List<AFD_FSDKFace> {
        return detect(NV21Utils.getNV21(bitmap), bitmap.width, bitmap.height)
    }

    fun detect(bytes: ByteArray, width: Int, height: Int): List<AFD_FSDKFace> {
        val result = ArrayList<AFD_FSDKFace>()
        engine.AFD_FSDK_StillImageFaceDetection(bytes, width, height, AFD_FSDKEngine.CP_PAF_NV21, result)
        return ArrayList(result)
    }

    fun destroy() {
        engine.AFD_FSDK_UninitialFaceEngine()
    }
}