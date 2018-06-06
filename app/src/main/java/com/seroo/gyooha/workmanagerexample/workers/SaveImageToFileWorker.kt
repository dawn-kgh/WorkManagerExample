package com.seroo.gyooha.workmanagerexample.workers

import android.annotation.SuppressLint
import android.provider.MediaStore
import androidx.work.Data
import androidx.work.Worker
import com.seroo.gyooha.workmanagerexample.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveImageToFileWorker : Worker() {

    companion object {
        @SuppressLint("ConstantLocale")
        private val DATE_FORMATION =
            SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault())
    }

    override fun doWork(): WorkerResult {
        return try {
            val resourceString = inputData.getString(
                Constants.KEY_IMAGE_URI, "")
            val resolver = applicationContext.contentResolver

            createBitmapByUri(resolver,
                resourceString).let {
                MediaStore.Images.Media.insertImage(resolver,
                    it,
                    "Blurred Image",
                    DATE_FORMATION.format(Date()))
            }.also {
                if (it.isBlank()) {
                    return WorkerResult.FAILURE
                }
            }

            Data.Builder()
                .putString(Constants.KEY_IMAGE_URI, resourceString)
                .build()
                .also { outputData = it }

            WorkerResult.SUCCESS
        } catch (exception: Exception) {
            WorkerResult.FAILURE
        }
    }
}