package cn.thens.arcfacedemo.common

import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore

interface ImageProvider {
    fun intentFromContent(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/jpeg"
        return intent
    }

    fun intentFromExternalCamera(): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }

    fun intentFromCamera(): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val uri = App.instance.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        return intent
    }

    companion object : ImageProvider
}