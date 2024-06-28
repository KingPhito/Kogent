package com.ralphdugue.kogent.di.modules

import com.ralphdugue.kogent.config.KogentConfig
import com.ralphdugue.kogent.dataconnector.domain.entities.KogentDataSourceCollection
import com.ralphdugue.kogent.dataconnector.domain.entities.KogentEmbeddingModel
import com.ralphdugue.kogent.query.domain.entities.KogentLLModel
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
    fun provideLLModel(): KogentLLModel {
        TODO()
    }

    @Single
    fun provideDataSources(): KogentDataSourceCollection {
        TODO()
    }

    @Single
    fun provideEmbeddingModel(): KogentEmbeddingModel {
        TODO()
    }

    @Single
    fun 
}
