package com.ralphdugue.kogent

import com.ralphdugue.kogent.config.KogentConfigBuilder
import com.ralphdugue.kogent.di.modules.KogentModule
import com.ralphdugue.kogent.query.domain.entities.QueryEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
            coroutineScope {
                config.dataSources.forEach { dataSource ->
                    launch(Dispatchers.IO) {
                        getQueryEngine().addDataSource(dataSource)
                    }
                }
            }
        }
    }

    fun getQueryEngine() = GlobalContext.get().get<QueryEngine>()
}

interface AppContextProvider {
    fun getContext(): Any?
}