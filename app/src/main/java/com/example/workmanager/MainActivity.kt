package com.example.workmanager

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmanager.databinding.ActivityMainBinding
import com.example.workmanager.workers.ImageDownloadWorker
import java.util.UUID
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {


    private val workManager by lazy {

        WorkManager.getInstance(applicationContext)
    }

    /**
     *Defined limits for work:
     *
     * 1. An active network connection
     * 2. Sufficient storage
     * 3. Enough battery
     */
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresStorageNotLow(true)
        .setRequiresBatteryNotLow(true)
        .build()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnImageDownload.setOnClickListener {
            showLottieAnimation()
            binding.downloadLayout.visibility = View.GONE

            createOneTimeWorkRequest()
            //createPeriodicWorkRequest()
            //createDelayedWorkRequest()


        }


    }

    private fun createOneTimeWorkRequest() {

        /**
         * Create your WorkRequest.
         * You also set constraints to the work. Additionally, you add a tag to
         * make your work unique. You can use this tag to cancel the work and observe its progress, too.
         * */
        val imageWorker = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setConstraints(constraints)
            .addTag("imageWork")
            .build()


        //you submit your work to WorkManager by calling enqueueUniqueWork.
        workManager.enqueueUniqueWork(
            "onTimeDownload",
            ExistingWorkPolicy.KEEP,
            imageWorker
        )

        observeWork(imageWorker.id)


    }

    /**
     * When you tap START IMAGE DOWNLOAD. You will notice that the animation does not disappear.
     * This is because the periodic job never finishes and only has a CANCELED state. After each
     * execution, the task is re-executed regardless of the previous state.
     */

    private fun createPeriodicWorkRequest() {

        /**
         * Use PeriodicWorkRequestBuilder to define your work. Notice that it takes
         * time as a parameter. A restriction requires the interval between successive executions
         * of your work to be at least 10 minutes.
         * */
        val imageWork = PeriodicWorkRequestBuilder<ImageDownloadWorker>(10, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag("imageWork")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "periodicImageWork",
            ExistingPeriodicWorkPolicy.KEEP,
            imageWork
        )

        observeWork(imageWork.id)


    }

    /**
     * when Tap START IMAGE DOWNLOAD to start the download, which delays for 1
     * minute . After this delay is complete, your work begins to run.
     */
    private fun createDelayedWorkRequest() {
        val imageWorker = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.MINUTES)
            .addTag("imageWork")
            .build()
        workManager.enqueueUniqueWork(
            "delayedImageDownload",
            ExistingWorkPolicy.KEEP,
            imageWorker
        )
        observeWork(imageWorker.id)
    }

    private fun showDownloadedImage(resultUri: Uri?) {
        binding.completeLayout.visibility = View.VISIBLE
        binding.downloadLayout.visibility = View.GONE
        hideLottieAnimation()
        binding.imgDownloaded.setImageURI(resultUri)
    }

    private fun observeWork(id: UUID) {
        workManager.getWorkInfoByIdLiveData(id)
            .observe(this) { info ->
                if (info != null && info.state.isFinished) {
                    hideLottieAnimation()
                    binding.downloadLayout.visibility = View.VISIBLE
                    val uriResult = info.outputData.getString("IMAGE_URI")
                    if (uriResult != null) {
                        showDownloadedImage(uriResult.toUri())
                    }
                }
            }
    }

    private fun hideLottieAnimation() {
        binding.animationView.visibility = View.GONE
        binding.animationView.cancelAnimation()

    }

    private fun showLottieAnimation() {
        binding.animationView.visibility = View.VISIBLE
        binding.animationView.playAnimation()

    }

}

