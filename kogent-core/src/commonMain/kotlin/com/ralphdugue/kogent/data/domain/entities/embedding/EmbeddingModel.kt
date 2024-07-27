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
    /**
     * Gets the embedding for the given text.
     * @param text The text to get the embedding for.
     * @return The embedding for the text.
     */
    suspend fun getEmbedding(text: String): Result<List<Float>>
}

sealed interface EmbeddingConfig

data class APIEmbeddingConfig(
    val dataSource: APIDataSource,
) : EmbeddingConfig

data class HuggingFaceEmbeddingConfig(
    val endpoint: String = "/feature-extraction/sentence-transformers/all-mpnet-base-v2",
    val apiToken: String,
) : EmbeddingConfig


class APIEmbeddingModelConfigBuilder {
    private var dataSource: APIDataSource? = null

    fun dataSource(block: ApiDataSourceBuilder.() -> Unit) {
        dataSource = ApiDataSourceBuilder().apply(block).build()
    }

    fun build(): APIEmbeddingConfig =
        APIEmbeddingConfig(
            dataSource = dataSource ?: throw IllegalStateException("dataSource must be set"),
        )
}

/**
 * A builder for the [HuggingFaceEmbeddingConfig].
 */
class HuggingFaceEmbeddingModelConfigBuilder {
    var endpoint: String = "all-MiniLM-L6-v2"
    var apiToken: String? = null

    fun build(): HuggingFaceEmbeddingConfig =
        HuggingFaceEmbeddingConfig(
            endpoint = endpoint,
            apiToken = apiToken ?: throw IllegalStateException("apiToken must be set"),
        )
}
