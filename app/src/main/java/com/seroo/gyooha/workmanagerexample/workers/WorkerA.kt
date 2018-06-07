package com.seroo.gyooha.workmanagerexample.workers

import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import com.seroo.gyooha.workmanagerexample.Constants

class WorkerA : Worker() {
    companion object {
        const val TAG = "WorkerA"
    }

    override fun doWork(): WorkerResult {
        Log.d(TAG, "$TAG run")
        return try {

            Data.Builder()
                .putString(Constants.TAG_WORKER_A, "Worker_A")
                .build()
                .also { outputData = it }

            WorkerResult.SUCCESS
        } catch (exception: Exception) {
            WorkerResult.FAILURE
        }
    }
}