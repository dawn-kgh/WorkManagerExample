package com.seroo.gyooha.workmanagerexample.workers

import android.util.Log
import androidx.work.Worker
import com.seroo.gyooha.workmanagerexample.Constants

class MergeWorker : Worker() {
    companion object {
        const val TAG = "MergeWorker"
    }

    override fun doWork(): WorkerResult {
        Log.d(TAG, "$TAG run")

        Log.d("inputData1", inputData.getString(Constants.TAG_OUTPUT, ""))
        Log.d("inputData2", inputData.getString(Constants.TAG_WORKER_A, ""))
        Log.d("inputData3", inputData.getString(Constants.TAG_WORKER_B, ""))

        return try {
            WorkerResult.SUCCESS
        } catch (exception: Exception) {
            WorkerResult.FAILURE
        }
    }
}