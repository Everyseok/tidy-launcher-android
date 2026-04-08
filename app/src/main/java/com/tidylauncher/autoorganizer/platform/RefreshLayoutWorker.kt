package com.tidylauncher.autoorganizer.platform

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.tidylauncher.autoorganizer.TidyLauncherApplication

class RefreshLayoutWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = try {
        val app = applicationContext as TidyLauncherApplication
        val reason = inputData.getString(KEY_REASON) ?: "worker"
        app.appContainer.autoArrangeCoordinator.refreshLayout(reason)
        Result.success()
    } catch (throwable: Throwable) {
        Result.retry()
    }

    companion object {
        private const val UNIQUE_NAME = "refresh-layout"
        private const val KEY_REASON = "reason"

        fun enqueue(context: Context, reason: String) {
            val request = OneTimeWorkRequestBuilder<RefreshLayoutWorker>()
                .setInputData(workDataOf(KEY_REASON to reason))
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_NAME,
                ExistingWorkPolicy.REPLACE,
                request,
            )
        }
    }
}

