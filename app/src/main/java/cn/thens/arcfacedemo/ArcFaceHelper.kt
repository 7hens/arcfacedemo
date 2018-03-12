package cn.thens.arcfacedemo

import android.graphics.Bitmap
import android.util.Log
import com.arcsoft.facedetection.AFD_FSDKEngine
import com.arcsoft.facerecognition.AFR_FSDKEngine
import com.arcsoft.facerecognition.AFR_FSDKError
import com.arcsoft.facerecognition.AFR_FSDKFace
import com.arcsoft.facerecognition.AFR_FSDKMatching

object ArcFaceHelper {
    private const val APP_ID = "BGdrzDB2itn4sK1wWZXvANsJkrGY8xgGo8AoZuK5Kdtp"
    private const val SDK_KEY_FT = "3W1shxXJowqVBsBdzDiXEfN74rP2SN1LxU4b5endB9WZ"
    private const val SDK_KEY_FD = "3W1shxXJowqVBsBdzDiXEfNEEFe9L76uKvHMsZSN2kgx"
    private const val SDK_KEY_FR = "3W1shxXJowqVBsBdzDiXEfNMPeuMe6ReW9H3aLzaYTNf"
    private const val SDK_KEY_AGE = "3W1shxXJowqVBsBdzDiXEfNyCfDAeSStcCxy6USQFTkY"
    private const val SDK_KEY_GENDER = "3W1shxXJowqVBsBdzDiXEfP6N4UMjBaMYS5yH253M1Ff"

    private val userList = ArrayList<User>()

    fun addFace(name: String, face: AFR_FSDKFace) {
        val one = userList.firstOrNull { it.name == name } ?: User(name).also { userList.add(it) }
        one.faceList.add(face)
    }

    private val frEngine: AFR_FSDKEngine by lazy {
        AFR_FSDKEngine().apply {
            val error = AFR_FSDK_InitialEngine(APP_ID, SDK_KEY_FR)
            if (error.code != AFR_FSDKError.MOK) {
                Log.e("@", "AFR_FSDK_InitialEngine fail! error code :" + error.code)
            }
        }
    }

    fun debug() {
        // 用来存放提取到的人脸信息, face_1 是注册的人脸，face_2 是要识别的人脸
        val face1 = AFR_FSDKFace()
        val face2 = AFR_FSDKFace()

        // 初始化人脸识别引擎，使用时请替换申请的 APPID 和 SDKKEY
        var error = frEngine.AFR_FSDK_InitialEngine(APP_ID, SDK_KEY_FR)
        Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error.code)

        // 输入的 data 数据为 NV21 格式（如 Camera 里 NV21 格式的 preview 数据）；
        // 人脸坐标一般使用人脸检测返回的 Rect 传入；
        // 人脸角度请按照人脸检测引擎返回的值传入。
//        error = engine.AFR_FSDK_ExtractFRFeature(data1, width, height, AFR_FSDKEngine.CP_PAF_NV21, Rect(210, 178, 478, 446), AFR_FSDKEngine.AFR_FOC_0, face1)
//        Log.d("com.arcsoft", "Face=" + face1.getFeatureData()[0] + "," + face1.getFeatureData()[1] + "," + face1.getFeatureData()[2] + "," + error.getCode())
//
//        error = engine.AFR_FSDK_ExtractFRFeature(data1, width, height, AFR_FSDKEngine.CP_PAF_NV21, Rect(210, 170, 470, 440), AFR_FSDKEngine.AFR_FOC_0, face2)
//        Log.d("com.arcsoft", "Face=" + face2.getFeatureData()[0] + "," + face2.getFeatureData()[1] + "," + face2.getFeatureData()[2] + "," + error.getCode())

        // score 用于存放人脸对比的相似度值
        val score = AFR_FSDKMatching()
        error = frEngine.AFR_FSDK_FacePairMatching(face1, face2, score)
        Log.d("com.arcsoft", "AFR_FSDK_FacePairMatching=" + error.getCode())
        Log.d("com.arcsoft", "Score:" + score.getScore())

        //销毁人脸识别引擎
        error = frEngine.AFR_FSDK_UninitialEngine()
        Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error.getCode())
    }

    fun destroy() {
        frEngine.AFR_FSDK_UninitialEngine()
    }

    fun toNV21(bitmap: Bitmap, width: Int = bitmap.width, height: Int = bitmap.height): ByteArray {
        val argb = IntArray(width * height)
        bitmap.getPixels(argb, 0, width, 0, 0, width, height)
        val yuv = ByteArray(width * height * 3 / 2)
        encodeYUV420SP(yuv, argb, width, height)
        bitmap.recycle()
        return yuv
    }

    fun encodeYUV420SP(yuv420sp: ByteArray, argb: IntArray, width: Int, height: Int) {
        val frameSize = width * height
        var yIndex = 0
        var uvIndex = frameSize
        var a: Int
        var R: Int
        var G: Int
        var B: Int
        var Y: Int
        var U: Int
        var V: Int
        var index = 0
        for (j in 0 until height) {
            for (i in 0 until width) {

                a = argb[index] and -0x1000000 shr 24 // a is not used obviously
                R = argb[index] and 0xff0000 shr 16
                G = argb[index] and 0xff00 shr 8
                B = argb[index] and 0xff shr 0

                // well known RGB to YUV algorithm
                Y = (66 * R + 129 * G + 25 * B + 128 shr 8) + 16
                U = (-38 * R - 74 * G + 112 * B + 128 shr 8) + 128
                V = (112 * R - 94 * G - 18 * B + 128 shr 8) + 128

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (if (Y < 0) 0 else if (Y > 255) 255 else Y).toByte()
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (if (V < 0) 0 else if (V > 255) 255 else V).toByte()
                    yuv420sp[uvIndex++] = (if (U < 0) 0 else if (U > 255) 255 else U).toByte()
                }

                index++
            }
        }
    }
}