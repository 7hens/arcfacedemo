package cn.thens.arcfacedemo.common

import android.graphics.Bitmap

object NV21Utils {
    /**
     * Bitmap转换成Drawable
     * Bitmap bm = xxx; //xxx根据你的情况获取
     * BitmapDrawable bd = new BitmapDrawable(getResource(), bm);
     * 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
     */
    fun getNV21(bitmap: Bitmap, inputWidth: Int = bitmap.width, inputHeight: Int = bitmap.height): ByteArray {
        val argb = IntArray(inputWidth * inputHeight)
        bitmap.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight)
        val yuv = ByteArray(inputWidth * inputHeight * 3 / 2)
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight)
        return yuv
//        return convertRGB_IYUV_I420(argb, inputWidth, inputHeight)
    }

    private fun encodeYUV420SP(yuv420sp: ByteArray, argb: IntArray, width: Int, height: Int) {
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

                /** NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                 * meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is
                 * every otherpixel AND every other scanline.
                 */
                yuv420sp[yIndex++] = (if (Y < 0) 0 else if (Y > 255) 255 else Y).toByte()
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (if (V < 0) 0 else if (V > 255) 255 else V).toByte()
                    yuv420sp[uvIndex++] = (if (U < 0) 0 else if (U > 255) 255 else U).toByte()
                }
                index++
            }
        }
    }

    fun convertRGB_IYUV_I420(aRGB: IntArray, width: Int, height: Int): ByteArray {
        val frameSize = width * height
        val chromasize = frameSize / 4

        var yIndex = 0
        var uIndex = frameSize
        var vIndex = frameSize + chromasize
        val yuv = ByteArray(width * height * 3 / 2)

        val a: Int
        var R: Int
        var G: Int
        var B: Int
        var Y: Int
        var U: Int
        var V: Int
        var index = 0
        for (j in 0 until height) {
            for (i in 0 until width) {
                //a = (aRGB[index] & 0xff000000) >> 24; //not using it right now
                R = aRGB[index] and 0xff0000 shr 16
                G = aRGB[index] and 0xff00 shr 8
                B = aRGB[index] and 0xff shr 0

                Y = (66 * R + 129 * G + 25 * B + 128 shr 8) + 16
                U = (-38 * R - 74 * G + 112 * B + 128 shr 8) + 128
                V = (112 * R - 94 * G - 18 * B + 128 shr 8) + 128

                yuv[yIndex++] = (if (Y < 0) 0 else if (Y > 255) 255 else Y).toByte()

                if (j % 2 == 0 && index % 2 == 0) {
                    yuv[vIndex++] = (if (U < 0) 0 else if (U > 255) 255 else U).toByte()
                    yuv[uIndex++] = (if (V < 0) 0 else if (V > 255) 255 else V).toByte()
                }
                index++
            }
        }
        return yuv
    }
}