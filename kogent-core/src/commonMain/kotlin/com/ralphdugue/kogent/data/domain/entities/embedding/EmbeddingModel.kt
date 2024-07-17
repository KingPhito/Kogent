package com.ralphdugue.kogent.data.domain.entities.embedding

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.ApiDataSourceBuilder
import com.ralphdugue.kogent.indexing.domain.entities.Index

/**
 * An embedding model.
 *
 * Embedding models are used to convert text into a vector representation.
 * This is done to store the text in an [Index].
 * @author Ralph Dugue
 */
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

class APIEmbeddingModelConfigBuilder {
    private var dataSource: APIDataSource? = null

    fun dataSource(block: ApiDataSourceBuilder.() -> Unit) {
        dataSource = ApiDataSourceBuilder().apply(block).build()
    }

    fun build(): EmbeddingConfig.APIEmbeddingConfig =
        EmbeddingConfig.APIEmbeddingConfig(
            dataSource = dataSource ?: throw IllegalStateException("dataSource must be set"),
        )
}

class HuggingFaceEmbeddingModelConfigBuilder {
    var model: String = "all-MiniLM-L6-v2"
    var apiToken: String? = null

    fun build(): EmbeddingConfig.HuggingFaceEmbeddingConfig =
        EmbeddingConfig.HuggingFaceEmbeddingConfig(
            model = model,
            apiToken = apiToken ?: throw IllegalStateException("apiToken must be set"),
        )
}
