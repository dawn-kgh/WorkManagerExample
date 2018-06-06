package com.seroo.gyooha.workmanagerexample.workers;

import com.seroo.gyooha.workmanagerexample.Constants;
import com.seroo.gyooha.workmanagerexample.R;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static com.seroo.gyooha.workmanagerexample.Constants.CHANNEL_ID;
import static com.seroo.gyooha.workmanagerexample.Constants.DELAY_TIME_MILLIS;


public final class WorkerUtils {
    private static final String TAG = WorkerUtils.class.getSimpleName();

   public static void makeStatusNotification(String message, Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME;
            String description = Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(Constants.NOTIFICATION_TITLE)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        NotificationManagerCompat.from(context).notify(Constants.NOTIFICATION_ID, builder.build());
    }

    public static void sleep() {
        try {
            Thread.sleep(DELAY_TIME_MILLIS, 0);
        } catch (InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @WorkerThread
    public static Bitmap blurBitmap(@NonNull Bitmap bitmap,
                             @NonNull Context applicationContext) {

        RenderScript rsContext = null;
        try {

            Bitmap output = Bitmap.createBitmap(
                bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

            rsContext = RenderScript.create(applicationContext, RenderScript.ContextType.DEBUG);
            Allocation inAlloc = Allocation.createFromBitmap(rsContext, bitmap);
            Allocation outAlloc = Allocation.createTyped(rsContext, inAlloc.getType());
            ScriptIntrinsicBlur theIntrinsic =
                ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext));
            theIntrinsic.setRadius(10.f);
            theIntrinsic.setInput(inAlloc);
            theIntrinsic.forEach(outAlloc);
            outAlloc.copyTo(output);

            return output;
        } finally {
            if (rsContext != null) {
                rsContext.finish();
            }
        }
    }

    public static Uri writeBitmapToFile(
            @NonNull Context applicationContext,
            @NonNull Bitmap bitmap) throws FileNotFoundException {

        String name = String.format("blur-filter-output-%s.png", UUID.randomUUID().toString());
        File outputDir = new File(applicationContext.getFilesDir(), Constants.OUTPUT_PATH);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File outputFile = new File(outputDir, name);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, out);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
        }
        return Uri.fromFile(outputFile);
    }

    private WorkerUtils() {
    }
}