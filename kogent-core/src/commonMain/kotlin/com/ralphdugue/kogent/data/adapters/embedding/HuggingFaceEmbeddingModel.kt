package com.ralphdugue.kogent.data.adapters.embedding

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.api.getResponse
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import io.ktor.client.*
import kotlinx.serialization.json.Json

internal class HuggingFaceEmbeddingModel(
    private val config: EmbeddingConfig.HuggingFaceEmbeddingConfig,
    private val client: HttpClient,
) : EmbeddingModel {
    override suspend fun getEmbedding(text: String): List<Float> {
        val apiDataSource = createDataSource(text)
        getResponse(client, apiDataSource).fold(
            onSuccess = { return Json.decodeFromString(it) },
            onFailure = { return emptyList() }
        )
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
