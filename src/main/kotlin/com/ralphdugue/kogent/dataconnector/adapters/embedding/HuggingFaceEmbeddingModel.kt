package com.ralphdugue.kogent.dataconnector.adapters.embedding

import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingModel
import io.ktor.client.HttpClient

class HuggingFaceEmbeddingModel(
    private val config: EmbeddingConfig.HuggingFaceEmbeddingConfig,
    private val client: HttpClient,
) : EmbeddingModel {
    override suspend fun getEmbedding(text: String): FloatArray {
        TODO("Not yet implemented")
    }
}
