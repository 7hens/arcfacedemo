package cn.thens.arcfacedemo.face

import cn.thens.arcfacedemo.BuildConfig
import cn.thens.arcfacedemo.face.arcface.FaceDetector
import cn.thens.arcfacedemo.face.arcface.FaceRecognizer
import cn.thens.arcfacedemo.face.arcface.FaceTracker

object ArcFaceHelper {
    fun createDetector(): FaceDetector {
        return FaceDetector(BuildConfig.AF_APP_ID, BuildConfig.AF_KEY_FD)
    }

    fun createRecognizer(): FaceRecognizer {
        return FaceRecognizer(BuildConfig.AF_APP_ID, BuildConfig.AF_KEY_FR)
    }

    fun createTracker(): FaceTracker {
        return FaceTracker(BuildConfig.AF_APP_ID, BuildConfig.AF_KEY_FT)
    }
}