package com.ralphdugue.kogent.di.modules

import com.ralphdugue.kogent.config.KogentConfig
import com.ralphdugue.kogent.dataconnector.adapters.connectors.buildSQLDataConnector
import com.ralphdugue.kogent.dataconnector.adapters.embedding.APIEmbeddingModel
import com.ralphdugue.kogent.dataconnector.adapters.embedding.HuggingFaceEmbeddingModel
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.dataconnector.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.indexing.utils.IndexFactory
import com.ralphdugue.kogent.query.adapters.HuggingFaceLLModel
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
    fun provideLLModel(client: HttpClient): LLModel =
        when (val llModelConfig = config.llModelConfig) {
            is LLModelConfig.HuggingFaceLLModelConfig -> HuggingFaceLLModel(llModelConfig, client)
        }

    @Single
    fun provideEmbeddingModel(client: HttpClient): EmbeddingModel =
        when (val embeddingConfig = config.embeddingConfig) {
            is EmbeddingConfig.APIEmbeddingConfig -> APIEmbeddingModel(embeddingConfig, client)
            is EmbeddingConfig.HuggingFaceEmbeddingConfig -> HuggingFaceEmbeddingModel(embeddingConfig, client)
        }

    @Single
    fun provideIndex(): Index = IndexFactory.createIndex(config.indexConfig)

    @Single
    fun provideSQLDataConnector(
        embeddingModel: EmbeddingModel,
        index: Index,
    ): SQLDataConnector = buildSQLDataConnector(embeddingModel, index)
}
