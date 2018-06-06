package com.seroo.gyooha.workmanagerexample.workers

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.Data
import androidx.work.Worker
import com.seroo.gyooha.workmanagerexample.Constants.KEY_IMAGE_URI

class BlurWorker : Worker() {

    override fun doWork(): WorkerResult {
        val resourceUri: String = inputData.getString(KEY_IMAGE_URI, "")
        return try {
            if (resourceUri.isBlank()) throw IllegalArgumentException("UriString is Empty space")
            outputData = makeFunction(applicationContext, resourceUri, makeLogicByContextAndRes)
            WorkerResult.SUCCESS
        } catch (exception: Throwable) {
            WorkerResult.FAILURE
        }
    }

    private fun makeFunction(context: Context, uriString: String,
        function: (context: Context, uriString: String) -> Data): Data =
        function.invoke(context, uriString)

    private val makeLogicByContextAndRes: (context: Context, uriString: String) -> Data = { context, uriString ->
        createBitmapByUri(context.contentResolver,
            uriString).let {
            WorkerUtils.blurBitmap(it, context)
        }.let {
            WorkerUtils.writeBitmapToFile(context, it)
        }.let {
            WorkerUtils.makeStatusNotification("Output is $it", context)
            Data.Builder().putString(KEY_IMAGE_URI, it.toString()).build()
        }
    }
}

fun createBitmapByUri(resolver: ContentResolver, uriString: String): Bitmap =
    BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(uriString)))
