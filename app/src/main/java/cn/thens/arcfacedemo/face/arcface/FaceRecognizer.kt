package cn.thens.arcfacedemo.face.arcface

import android.graphics.Bitmap
import cn.thens.arcfacedemo.common.ILog
import cn.thens.arcfacedemo.common.NV21Utils
import com.arcsoft.facedetection.AFD_FSDKEngine
import com.arcsoft.facedetection.AFD_FSDKFace
import com.arcsoft.facerecognition.AFR_FSDKEngine
import com.arcsoft.facerecognition.AFR_FSDKError
import com.arcsoft.facerecognition.AFR_FSDKFace
import com.arcsoft.facerecognition.AFR_FSDKMatching

class FaceRecognizer(appId: String, sdkKey: String) {
    private val engine: AFR_FSDKEngine by lazy {
        AFR_FSDKEngine().apply {
            AFR_FSDK_InitialEngine(appId, sdkKey).let {
                ILog.a(it.code == AFR_FSDKError.MOK, "AFR_FSDK_InitialEngine fail! error code :" + it.code)
            }
        }
    }

    fun recognize(bytes: ByteArray, width: Int, height: Int, detectedFace: AFD_FSDKFace): AFR_FSDKFace {
        val face = AFR_FSDKFace()
        engine.AFR_FSDK_ExtractFRFeature(bytes, width, height, AFD_FSDKEngine.CP_PAF_NV21, detectedFace.rect, detectedFace.degree, face)
        return face
    }

    fun recognize(bitmap: Bitmap, detectedFace: AFD_FSDKFace): AFR_FSDKFace {
        return recognize(NV21Utils.getNV21(bitmap), bitmap.width, bitmap.height, detectedFace)
    }

    fun match(face1: AFR_FSDKFace, face2: AFR_FSDKFace): Float {
        val score = AFR_FSDKMatching()
        engine.AFR_FSDK_FacePairMatching(face1, face2, score)
        return score.score
    }

    fun destroy() {
        engine.AFR_FSDK_UninitialEngine()
    }

    companion object {

    }
}