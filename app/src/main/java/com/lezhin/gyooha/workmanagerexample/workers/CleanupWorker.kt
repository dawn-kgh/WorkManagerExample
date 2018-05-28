package com.lezhin.gyooha.workmanagerexample.workers

import android.util.Log
import androidx.work.Worker
import com.lezhin.gyooha.workmanagerexample.Constants.OUTPUT_PATH
import java.io.File

class CleanupWorker : Worker() {
    companion object {
        private val TAG: String = CleanupWorker::class.java.simpleName
    }

    override fun doWork(): WorkerResult {
        val outputDirection = File(applicationContext.filesDir, OUTPUT_PATH)
        return try {
            if (outputDirection.exists()) {
                val entries: List<File> = outputDirection.listFiles()?.toList() ?: emptyList()
                entries.filter {
                    it.name.isNotBlank() && it.name.endsWith(".png")
                }.forEach {
                    it.delete()
                    Log.d(TAG, String.format("Deleted %s - %s", it.name, it.delete()))
                }
            }
            WorkerResult.SUCCESS
        } catch (exception: Exception) {
            WorkerResult.FAILURE
        }
    }
}