package com.tidylauncher.autoorganizer.di

import android.content.Context
import com.tidylauncher.autoorganizer.billing.BillingManager
import com.tidylauncher.autoorganizer.data.AppInventoryProvider
import com.tidylauncher.autoorganizer.data.DefaultAppInventoryProvider
import com.tidylauncher.autoorganizer.data.local.AppDatabase
import com.tidylauncher.autoorganizer.data.local.SettingsRepository
import com.tidylauncher.autoorganizer.domain.DefaultRecommendationEngine
import com.tidylauncher.autoorganizer.domain.FolderNamingService
import com.tidylauncher.autoorganizer.domain.RecommendationEngine
import com.tidylauncher.autoorganizer.domain.classification.AppCategoryClassifier
import com.tidylauncher.autoorganizer.domain.classification.AppColorClassifier
import com.tidylauncher.autoorganizer.domain.classification.CategoryClassifier
import com.tidylauncher.autoorganizer.domain.classification.ColorClassifier
import com.tidylauncher.autoorganizer.domain.layout.AutoArrangeCoordinator
import com.tidylauncher.autoorganizer.domain.layout.CoreAppResolver
import com.tidylauncher.autoorganizer.domain.layout.DefaultAutoArrangeCoordinator
import com.tidylauncher.autoorganizer.domain.layout.DefaultLayoutPlanner
import com.tidylauncher.autoorganizer.domain.layout.LayoutPlanner

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = AppDatabase.create(appContext)

    val settingsRepository = SettingsRepository(appContext)
    val billingManager = BillingManager(appContext, settingsRepository)

    private val categoryClassifier: CategoryClassifier = AppCategoryClassifier()
    private val colorClassifier: ColorClassifier = AppColorClassifier()

    val appInventoryProvider: AppInventoryProvider = DefaultAppInventoryProvider(
        context = appContext,
        appDao = database.installedAppDao(),
        categoryClassifier = categoryClassifier,
        colorClassifier = colorClassifier,
    )

    private val folderNamingService = FolderNamingService(appContext)
    private val coreAppResolver = CoreAppResolver(appContext)

    val recommendationEngine: RecommendationEngine = DefaultRecommendationEngine(appContext)
    val layoutPlanner: LayoutPlanner = DefaultLayoutPlanner(
        folderNamingService = folderNamingService,
        coreAppResolver = coreAppResolver,
    )

    val autoArrangeCoordinator: AutoArrangeCoordinator = DefaultAutoArrangeCoordinator(
        appInventoryProvider = appInventoryProvider,
        settingsRepository = settingsRepository,
        layoutPlanner = layoutPlanner,
    )
}
