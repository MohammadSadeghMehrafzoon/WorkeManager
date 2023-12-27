package com.example.workmanager.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class SampleWorker (
    private val context: Context,
    private val workerParameters: WorkerParameters
) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        return when (inputData.getString("Worker")) {
            "sampleWorker" -> Result.success()
            else -> Result.retry()
        }
    }
}