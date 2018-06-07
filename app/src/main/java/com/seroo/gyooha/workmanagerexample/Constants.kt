package com.seroo.gyooha.workmanagerexample

object Constants {

    const val VERBOSE_NOTIFICATION_CHANNEL_NAME = "Verbose WorkManager Notifications"
    const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
    const val NOTIFICATION_TITLE= "WorkRequest Starting"
    const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
    const val NOTIFICATION_ID = 1

    internal const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"

    const val OUTPUT_PATH = "blur_filter_outputs"
    const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
    internal const val TAG_OUTPUT = "OUTPUT"
    internal const val TAG_WORKER_A = "WORKER_A"
    internal const val TAG_WORKER_B = "WORKER_B"

    const val DELAY_TIME_MILLIS: Long = 3000
}