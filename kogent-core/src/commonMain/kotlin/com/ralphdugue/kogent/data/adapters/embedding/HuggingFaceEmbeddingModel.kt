package com.ralphdugue.kogent.data.adapters.embedding

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.api.getResponse
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.embedding.HuggingFaceEmbeddingConfig
import io.ktor.client.*
import kotlinx.serialization.json.Json

internal class HuggingFaceEmbeddingModel(
    private val config: HuggingFaceEmbeddingConfig,
    private val client: HttpClient,
) : EmbeddingModel {
    override suspend fun getEmbedding(text: String): Result<List<Float>> {
        val apiDataSource = createDataSource(text)
        return runCatching {
            getResponse(client, apiDataSource).fold(
                onSuccess = { Json.decodeFromString(it) },
                onFailure = { throw it }
            )
        }
    }

    private fun createDataSource(text: String): APIDataSource =
        APIDataSource(
            identifier = config.model,
            baseUrl = "https://api-inference.huggingface.co",
            headers = mapOf("Authorization" to "Bearer ${config.apiToken}"),
            method = APIDataSource.HttpMethod.POST,
            endpoint = "/pipeline/feature-extraction/sentence-transformers/${config.model}",
            body = text
        )
}
