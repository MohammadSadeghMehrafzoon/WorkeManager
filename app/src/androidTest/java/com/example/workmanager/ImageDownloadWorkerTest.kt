package com.example.workmanager

import org.junit.Rule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.workmanager.workers.ImageDownloadWorker
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.Test


class ImageDownloadWorkerTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var workerManagerTestRule = WorkManagerTestRule()

    @Test
    fun testDownloadWork() {
        // Create Work Request
        val work = TestListenableWorkerBuilder<ImageDownloadWorker>(workerManagerTestRule.targetContext).build()
        runBlocking {
            val result = work.doWork()
            // Assert
            assertNotNull(result)
        }
    }

}