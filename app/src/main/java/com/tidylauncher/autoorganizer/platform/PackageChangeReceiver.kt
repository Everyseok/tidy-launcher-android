package com.tidylauncher.autoorganizer.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        RefreshLayoutWorker.enqueue(
            context = context,
            reason = intent.action ?: "package_change",
        )
    }
}

