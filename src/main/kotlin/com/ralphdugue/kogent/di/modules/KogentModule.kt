package com.ralphdugue.kogent.di.modules

import com.ralphdugue.kogent.config.KogentConfig
import com.ralphdugue.kogent.dataconnector.adapters.embedding.HuggingFaceEmbeddingModel
import com.ralphdugue.kogent.dataconnector.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingModel
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
class KogentModule(
    private val config: KogentConfig,
) {
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
    fun provideLLModel(apiDataConnector: APIDataConnector): LLModel =
        when (val llModelConfig = config.llModelConfig) {
            is LLModelConfig.HuggingFaceLLModelConfig -> HuggingFaceLLModel(llModelConfig, apiDataConnector)
            null -> TODO()
        }

    @Single
    fun provideEmbeddingModel(apiDataConnector: APIDataConnector): EmbeddingModel =
        if (config.embeddingConfig != null) {
            when (val embeddingConfig = config.embeddingConfig) {
                is EmbeddingConfig.HuggingFaceEmbeddingConfig -> HuggingFaceEmbeddingModel(embeddingConfig, apiDataConnector)
                null -> TODO()
            }
        } else {
            TODO()
        }

    @Single
    fun provideIndex(): Index = IndexFactory.createIndex(config.indexConfig)
}
