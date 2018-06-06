package com.seroo.gyooha.workmanagerexample

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkStatus
import com.seroo.gyooha.workmanagerexample.workers.BlurWorker
import com.seroo.gyooha.workmanagerexample.workers.CleanupWorker
import com.seroo.gyooha.workmanagerexample.workers.SaveImageToFileWorker
import kotlin.properties.Delegates

class BlurViewModel : ViewModel() {
    private lateinit var blurView: BlurView

    private var uriString: String by Delegates.observable("") { _, _, newString ->
        blurView.uriResult(Uri.parse(newString))
    }
    private var outputUri: Uri = Uri.EMPTY

    private val workManager: WorkManager by lazy {
        WorkManager.getInstance()
    }

    private val savedWorkState: LiveData<List<WorkStatus>> by lazy {
        workManager.getStatusesByTag(Constants.TAG_OUTPUT)
    }

    fun applyBlur(blurLevel: Int) {
        var workerContinuation =
            workManager.beginUniqueWork(Constants.IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker::class.java))

        repeat(blurLevel) {
            OneTimeWorkRequest.Builder(BlurWorker::class.java).apply {
                if (it == 0) setInputData(createData())
                workerContinuation = workerContinuation.then(build())
            }
        }

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build()

        OneTimeWorkRequest.Builder(
            SaveImageToFileWorker::class.java)
            .addTag(Constants.TAG_OUTPUT)
            .setConstraints(constraints)
            .build()
            .also {
                workerContinuation = workerContinuation.then(it)
            }

        workerContinuation.enqueue()
    }

    fun setUri(uriS: String) {
        uriString = uriS
    }

    private fun createData(): Data {
        return if (uriString.isNotBlank()) {
            Data.Builder().putString(Constants.KEY_IMAGE_URI, uriString).build()
        } else {
            Data.EMPTY
        }
    }

    fun setView(view: BlurView) {
        blurView = view
    }

    fun cancelWork() {
        workManager.cancelAllWorkByTag(Constants.TAG_OUTPUT)
    }

    fun getWorksState(): LiveData<List<WorkStatus>> = savedWorkState

    override fun onCleared() {
        cancelWork()

        super.onCleared()
    }

    fun setOutPutUri(uri: Uri) {
        outputUri = uri
    }

    fun getOutputUri(): Uri = outputUri
}
