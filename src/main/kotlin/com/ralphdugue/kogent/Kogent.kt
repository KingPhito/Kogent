package com.ralphdugue.kogent

import com.ralphdugue.kogent.config.KogentConfig
import com.ralphdugue.kogent.di.modules.KogentModule
import com.ralphdugue.kogent.query.domain.entities.QueryEngine
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.ksp.generated.module

object Kogent {
    fun init(
        config: KogentConfig,
        modules: List<Module> = listOf(),
    ) {
        // Check if Koin is already started
        if (GlobalContext.getOrNull() == null) {
            modules.plus(KogentModule(config).module).let { modules ->
                startKoin {
                    // Add your Koin configuration here (e.g., logger)
                    modules(modules)
                }
            }
        }
    }

    fun getQueryEngine() = GlobalContext.get().get<QueryEngine>()
}
