package com.ralphdugue.kogent

import com.ralphdugue.kogent.config.KogentConfigBuilder
import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.di.modules.KogentModule
import com.ralphdugue.kogent.query.domain.entities.QueryEngine
import kotlinx.coroutines.*
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.ksp.generated.module

object Kogent {
    fun init(
        modules: List<Module> = listOf(),
        appContextProvider: AppContextProvider? = null,
        block: KogentConfigBuilder.() -> Unit,
    ) {
        val config = KogentConfigBuilder().apply(block).build()
        // Check if Koin is already started
        if (GlobalContext.getOrNull() == null) {
            val kogentModule = KogentModule().apply {
                this.config = config
                this.appContextProvider = appContextProvider
            }.module
            modules.plus(kogentModule).let { modules ->
                startKoin {
                    modules(modules)
                }
            }
        }
        runBlocking {
            val deferredJobs = config.dataSources.map { dataSource ->
                async(Dispatchers.IO + NonCancellable) {
                    try {
                        getQueryEngine().addDataSource(dataSource)
                        Result.success(dataSource)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }
            }

            val results: List<Result<DataSource>> = deferredJobs.awaitAll()

            // Process results
            results.forEach { result ->
                result.fold(
                    onSuccess = { dataSource ->
                        println("Successfully added data source: ${dataSource.identifier}")
                    },
                    onFailure = { exception ->
                        println("Failed to add data source: $exception")
                    }
                )
            }
        }
    }

    fun getQueryEngine() = GlobalContext.get().get<QueryEngine>()
}

interface AppContextProvider {
    fun getContext(): Any?
}