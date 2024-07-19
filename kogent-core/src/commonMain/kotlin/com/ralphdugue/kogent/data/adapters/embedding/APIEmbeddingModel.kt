package com.ralphdugue.kogent.data.adapters.embedding

import com.ralphdugue.kogent.data.domain.entities.embedding.APIEmbeddingConfig
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import io.ktor.client.HttpClient

internal class APIEmbeddingModel(
    private val config: APIEmbeddingConfig,
    private val client: HttpClient,
) : EmbeddingModel {
    override suspend fun getEmbedding(text: String): Result<List<Float>> {
        TODO("Not yet implemented")
    }
}
