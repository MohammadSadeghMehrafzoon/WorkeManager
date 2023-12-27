package com.example.workmanager.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.workmanager.R
import com.example.workmanager.utils.getUriFromUrl
import kotlinx.coroutines.delay

class ImageDownloadWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {


    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    override suspend fun doWork(): Result {

        setForeground(createForegroundInfo())

        /** The image downloads quickly. To simulate a near real-world situation,
         *  you add a delay of 700 milliseconds so the work can take time.
         */
        delay(700)

        val saveUri = context.getUriFromUrl()

        //Last, once you have the URI, you return a successful response to notify that your work has finished without failure. workDataOf() converts a list of pairs to a Data object. A Data object is a set of key/value pairs used as inputs/outputs for ListenableWorker‘s. IMAGE_URI is a key for identifying the result. You’re going to use it to get the value from this worker.
        return Result.success(workDataOf("IMAGE_URI" to saveUri.toString()))

    }


    private fun createForegroundInfo(): ForegroundInfo {
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        val notification = NotificationCompat.Builder(applicationContext, "workDownload")
            .setContentTitle("Downloading Your Image")
            .setTicker("Downloading Your Image")
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, "Cancel Download", intent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notification, "workDownload")
        }

        return ForegroundInfo(1, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(notificationBuilder: NotificationCompat.Builder, id: String) {
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
        val channel = NotificationChannel(
            id,
            "WorkManagerApp",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "WorkManagerApp Notifications"
        notificationManager.createNotificationChannel(channel)
    }

}

