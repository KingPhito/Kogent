package com.ralphdugue.kogent.dataconnector.domain.entities.embedding

interface EmbeddingModel {
    suspend fun getEmbedding(text: String): List<FloatArray>
}

sealed interface EmbeddingConfig {
    data class HuggingFaceEmbeddingConfig(
        val connectionString: String,
        val credentials: List<Map<String, String>>,
    ) : EmbeddingConfig
}
