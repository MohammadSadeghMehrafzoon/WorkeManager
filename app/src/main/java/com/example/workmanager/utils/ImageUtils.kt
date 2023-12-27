package com.example.workmanager.utils

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import java.net.URL
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID


//extension function

fun URL.toBitmap(): Bitmap? {
    return try {
        BitmapFactory.decodeStream(openStream())
    } catch (e: IOException) {
        null
    }
}


fun Bitmap.saveToInternalStorage(context: Context): Uri? {
    val wrapper = ContextWrapper(context)
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)
    file = File(file, "${UUID.randomUUID()}.jpg")
    return try {
        val stream: OutputStream = FileOutputStream(file)
        compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
        Uri.parse(file.absolutePath)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}


fun Context.getUriFromUrl(): Uri? {

    // converts a link to a qualified URL
    val imageUrl =
        URL(
            "https://files.virgool.io/upload/users/472315/posts/ambv6wnhmzd0/7gew8yhmnbrk.png"
        )
    val bitmap = imageUrl.toBitmap()

    /**Last, you save the image bitmap to internal storage. Moreover,
     *  you get the URI of the path to the image’s storage location.
    **/
    var savedUri: Uri? = null
    bitmap?.apply {
        savedUri = saveToInternalStorage(this@getUriFromUrl)
    }
    return savedUri


}


