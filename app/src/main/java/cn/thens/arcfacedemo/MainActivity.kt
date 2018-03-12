package cn.thens.arcfacedemo

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cn.thens.arcfacedemo.common.ILog
import cn.thens.arcfacedemo.common.ImageProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vImage.setOnClickListener {
            ILog.e("clicked =======================")
            AlertDialog.Builder(this)
                    .setTitle("请选择注册方式")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setItems(arrayOf("打开图片", "拍摄照片")) { _, i ->
                        when (i) {
                            0 -> startActivityForResult(ImageProvider.intentFromContent(), CODE_IMAGE)
                            1 -> startActivityForResult(ImageProvider.intentFromExternalCamera(), CODE_CAMERA)
                        }
                    }
                    .show()
        }
    }

    override fun onResume() {
        super.onResume()
        val storagePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
        if (ActivityCompat.checkSelfPermission(this, storagePermission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(storagePermission), CODE_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            CODE_CAMERA -> {
                if (data != null) {
                    val bitmap = data.extras.get("data") as Bitmap
                    vImage.setImageBitmap(bitmap)
                }
            }
            CODE_IMAGE -> {
                if (data != null) {
                    Log.e("@", data.data.toString())
                    val inputStream = contentResolver.openInputStream(data.data)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    vImage.setImageBitmap(bitmap)
                }
            }
        }
    }

    companion object {
        private const val CODE_CAMERA = 0
        private const val CODE_IMAGE = 1
        private const val CODE_STORAGE = 2

        fun decodeImage(path: String): Bitmap? {
            val res: Bitmap
            try {
                val exif = ExifInterface(path)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

                val op = BitmapFactory.Options()
                op.inSampleSize = 1
                op.inJustDecodeBounds = false
                //op.inMutable = true;
                res = BitmapFactory.decodeFile(path, op)
                //rotate and scale.
                val matrix = Matrix()

                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    matrix.postRotate(90F)
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    matrix.postRotate(180F)
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    matrix.postRotate(270F)
                }

                val temp = Bitmap.createBitmap(res, 0, 0, res.width, res.height, matrix, true)
                Log.d("com.arcsoft", "check target Image:" + temp.width + "X" + temp.height)

                if (temp != res) {
                    res.recycle()
                }
                return temp
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

    }
}
