package com.example.workmanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.*
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.workmanager.workers.SampleWorker
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class SampleWorkerTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var workerManagerTestRule = WorkManagerTestRule()


    @Test
    fun testWorkerInitialDelay() {
        val inputData = workDataOf("Worker" to "sampleWorker")

        // Create Work request.
        val request = OneTimeWorkRequestBuilder<SampleWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setInputData(inputData)
            .build()


        val testDriver = WorkManagerTestInitHelper.getTestDriver(workerManagerTestRule.targetContext)
        val workManager = workerManagerTestRule.workManager

        // Enqueue the request
        workManager.enqueue(request).result.get()

        // Set Initial Delay
        testDriver?.setInitialDelayMet(request.id)

        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(request.id).get()

        // Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    }

    @Test
    fun testPeriodicSampleWorker() {
        val inputData = workDataOf("Worker" to "sampleWorker")

        // Create Work request.
        val request = PeriodicWorkRequestBuilder<SampleWorker>(15, TimeUnit.MINUTES)
            .setInputData(inputData)
            .build()

        val testDriver = WorkManagerTestInitHelper.getTestDriver(workerManagerTestRule.targetContext)
        val workManager = workerManagerTestRule.workManager

        // Enqueue the request
        workManager.enqueue(request).result.get()

        // Complete period delay
        testDriver?.setPeriodDelayMet(request.id)

        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(request.id).get()
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))
    }

    @Test
    fun testAllConstraintsAreMet() {
        val inputData = workDataOf("Worker" to "sampleWorker")

        // Create Constraints.
        val constraints = Constraints.Builder()
            // Add network constraint.
            .setRequiredNetworkType(NetworkType.CONNECTED)
            // Add battery constraint.
            .setRequiresBatteryNotLow(true)
            .build()

        // Create Work request.
        val request = OneTimeWorkRequestBuilder<SampleWorker>()
            // Add constraints
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        val workManager = WorkManager.getInstance(workerManagerTestRule.targetContext)

        // Enqueue the request
        workManager.enqueue(request).result.get()

        // Simulate constraints
        WorkManagerTestInitHelper.getTestDriver(workerManagerTestRule.targetContext)
            ?.setAllConstraintsMet(request.id)

        val workInfo = workManager.getWorkInfoById(request.id).get()

        // Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    }
}