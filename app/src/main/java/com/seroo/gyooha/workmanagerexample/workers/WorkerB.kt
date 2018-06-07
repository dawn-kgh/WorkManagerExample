package com.seroo.gyooha.workmanagerexample.workers

import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import com.seroo.gyooha.workmanagerexample.Constants

class WorkerB(): Worker() {
    companion object {
        const val TAG = "WorkerB"
    }
    override fun doWork(): WorkerResult {
        Log.d(WorkerA.TAG,"${WorkerA.TAG} run")

        return try {
            Data.Builder()
                .putString(Constants.TAG_WORKER_B, "Worker_B")
                .build()
                .also { outputData = it }

            WorkerResult.SUCCESS
        } catch (exception: Exception) {
            WorkerResult.FAILURE
        }
    }
}