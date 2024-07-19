package com.ralphdugue.kogent.di.modules

import com.ralphdugue.kogent.AppContextProvider
import com.ralphdugue.kogent.cache.DataSourceRegistryDB
import com.ralphdugue.kogent.config.KogentConfig
import com.ralphdugue.kogent.data.adapters.DriverFactory
import com.ralphdugue.kogent.data.adapters.connectors.buildSQLDataConnector
import com.ralphdugue.kogent.data.adapters.embedding.APIEmbeddingModel
import com.ralphdugue.kogent.data.adapters.embedding.HuggingFaceEmbeddingModel
import com.ralphdugue.kogent.data.adapters.registry.ExternalDataSourceRegistry
import com.ralphdugue.kogent.data.adapters.registry.LocalDataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.ExternalRegistry
import com.ralphdugue.kogent.data.domain.entities.LocalRegistry
import com.ralphdugue.kogent.data.domain.entities.embedding.APIEmbeddingConfig
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.embedding.HuggingFaceEmbeddingConfig
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.indexing.utils.IndexFactory
import com.ralphdugue.kogent.query.adapters.HuggingFaceLLModel
import com.ralphdugue.kogent.query.domain.entities.HuggingFaceLLModelConfig
import com.ralphdugue.kogent.query.domain.entities.LLModel
import com.ralphdugue.kogent.query.domain.entities.LLModelConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.ralphdugue.kogent")
class KogentModule {
    lateinit var config: KogentConfig
    var appContextProvider: AppContextProvider? = null

    @Single
    fun provideHttpClient(): HttpClient =
        HttpClient(CIO) {
            expectSuccess = true
            install(HttpTimeout)
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = true
                    },
                )
            }
        }


    @Single
    fun providesDataSourceRegistryDB(): DataSourceRegistryDB {
        val driverFactory = DriverFactory()
        driverFactory.appContextProvider = appContextProvider
        return DataSourceRegistryDB(driverFactory.createDriver())
    }

    @Single
    fun provideDataSourceRegistry(dataSourceRegistryDB: DataSourceRegistryDB): DataSourceRegistry {
        return when (val registryType = config.registryType) {
            LocalRegistry -> LocalDataSourceRegistry(dataSourceRegistryDB)
            is ExternalRegistry -> ExternalDataSourceRegistry(registryType.dataSource)
        }
    }

    @Single
    fun provideLLModel(client: HttpClient): LLModel =
        when (val llModelConfig = config.llModelConfig) {
            is HuggingFaceLLModelConfig -> HuggingFaceLLModel(llModelConfig, client)
        }

    @Single
    fun provideEmbeddingModel(client: HttpClient): EmbeddingModel =
        when (val embeddingConfig = config.embeddingConfig) {
            is APIEmbeddingConfig -> APIEmbeddingModel(embeddingConfig, client)
            is HuggingFaceEmbeddingConfig -> HuggingFaceEmbeddingModel(embeddingConfig, client)
        }

    @Single
    fun provideIndex(): Index = IndexFactory.createIndex(config.indexConfig)

    @Single
    fun provideSQLDataConnector(
        embeddingModel: EmbeddingModel,
        index: Index,
        dataSourceRegistry: DataSourceRegistry,
    ): SQLDataConnector = buildSQLDataConnector(embeddingModel, index, dataSourceRegistry)
}

