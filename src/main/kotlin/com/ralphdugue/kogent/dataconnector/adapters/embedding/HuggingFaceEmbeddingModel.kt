package com.ralphdugue.kogent.dataconnector.adapters.embedding

import com.ralphdugue.kogent.dataconnector.domain.entities.api.APIDataSource
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

    private fun createDataStructure(body: String): APIDataSource =
        APIDataSource(
            identifier = "HuggingFace",
            url = "https://api-inference.huggingface.co",
            headers = mapOf("Authorization" to "Bearer ${config.apiToken}"),
            method = APIDataSource.HttpMethod.POST,
            endpoint = "/pipeline/feature-extraction/sentence-transformers/${config.model}",
            body = body,
        )
}
