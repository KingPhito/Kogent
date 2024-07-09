package com.ralphdugue.kogent.dataconnector.adapters.embedding

import com.ralphdugue.kogent.dataconnector.domain.entities.api.APIDataSource
import com.ralphdugue.kogent.dataconnector.domain.entities.api.KogentAPIResponse
import com.ralphdugue.kogent.dataconnector.domain.entities.api.getResponse
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingModel
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

internal class HuggingFaceEmbeddingModel(
    private val config: EmbeddingConfig.HuggingFaceEmbeddingConfig,
    private val client: HttpClient,
) : EmbeddingModel {
    override suspend fun getEmbedding(text: String): List<Float> {
        val apiDataSource = createDataSource(text)
        when (val response = getResponse(client, apiDataSource)) {
            is KogentAPIResponse.Success -> {
                val body = response.data ?: throw Exception("No embedding found")
                val embedding: List<Float> = Json.decodeFromString(body)
                return embedding
            }
            is KogentAPIResponse.Error -> throw response.exception
        }
    }

    private fun createDataSource(body: String): APIDataSource =
        APIDataSource(
            identifier = config.model,
            baseUrl = "https://api-inference.huggingface.co",
            headers = mapOf("Authorization" to "Bearer ${config.apiToken}"),
            method = APIDataSource.HttpMethod.POST,
            endpoint = "/pipeline/feature-extraction/sentence-transformers/${config.model}",
            body = mapOf("inputs" to body),
        )
}
