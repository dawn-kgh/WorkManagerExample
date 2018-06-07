package com.seroo.gyooha.workmanagerexample.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.seroo.gyooha.workmanagerexample.Constants.CHANNEL_ID
import com.seroo.gyooha.workmanagerexample.Constants.NOTIFICATION_ID
import com.seroo.gyooha.workmanagerexample.Constants.NOTIFICATION_TITLE
import com.seroo.gyooha.workmanagerexample.Constants.OUTPUT_PATH
import com.seroo.gyooha.workmanagerexample.Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
import com.seroo.gyooha.workmanagerexample.Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME
import com.seroo.gyooha.workmanagerexample.R
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object WorkerUtils {
    private final const val TAG: String = "WorkerUtils"

    fun makeStatusNotification(message: String, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: String = VERBOSE_NOTIFICATION_CHANNEL_NAME
            val importance: Int = IMPORTANCE_HIGH

            NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
            }.also {
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)
                    ?.createNotificationChannel(it)
            }
        }

        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .build()
            .also {
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, it)
            }
    }

    fun blurBitmap(bitmap: Bitmap, context: Context): Bitmap {
        val rsContext = RenderScript.create(context, RenderScript.ContextType.DEBUG)

        return try {
            val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

            val inAlloc = Allocation.createFromBitmap(rsContext, bitmap)
            val outAlloc = Allocation.createTyped(rsContext, inAlloc.type)
            val theIntrinsic = ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext))

            with(theIntrinsic) {
                setRadius(10.0F)
                setInput(inAlloc)
                forEach(outAlloc)
            }

            outAlloc.copyTo(output)
            output
        } finally {
            rsContext?.finish()
        }
    }

    fun writeBitmapToFile(context: Context, bitmap: Bitmap) {
        val name = String.format("blur-filter-output-%s.png", UUID.randomUUID())
        val outputDir = File(context.filesDir, OUTPUT_PATH).apply {
            if(!exists()) mkdirs()
        }

        val outputFile = File(outputDir, name)

        val out: FileOutputStream = FileOutputStream(outputFile)
        out.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, it)
        }
    }
}