package com.ralphdugue.kogent.dataconnector.adapters.embedding

import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingModel
import io.ktor.client.HttpClient

internal class APIEmbeddingModel(
    private val config: EmbeddingConfig.APIEmbeddingConfig,
    private val client: HttpClient,
) : EmbeddingModel {
    override suspend fun getEmbedding(text: String): List<Float> {
        TODO("Not yet implemented")
    }
}
