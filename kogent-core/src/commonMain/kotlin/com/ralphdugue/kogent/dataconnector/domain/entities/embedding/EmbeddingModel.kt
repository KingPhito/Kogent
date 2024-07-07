package com.ralphdugue.kogent.dataconnector.domain.entities.embedding

import com.ralphdugue.kogent.dataconnector.domain.entities.api.APIDataSource

interface EmbeddingModel {
    suspend fun getEmbedding(text: String): List<Float>
}

sealed interface EmbeddingConfig {
    data class APIEmbeddingConfig(
        val dataSource: APIDataSource,
    ) : EmbeddingConfig

    data class HuggingFaceEmbeddingConfig(
        val model: String = "all-MiniLM-L6-v2",
        val apiToken: String,
    ) : EmbeddingConfig
}
