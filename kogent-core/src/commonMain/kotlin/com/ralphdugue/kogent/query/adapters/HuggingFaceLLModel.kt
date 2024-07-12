package com.ralphdugue.kogent.query.adapters

import com.ralphdugue.kogent.data.domain.entities.api.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.api.KogentAPIResponse
import com.ralphdugue.kogent.data.domain.entities.api.getResponse
import com.ralphdugue.kogent.query.domain.entities.LLModel
import com.ralphdugue.kogent.query.domain.entities.LLModelConfig
import io.ktor.client.HttpClient

class HuggingFaceLLModel(
    private val config: LLModelConfig.HuggingFaceLLModelConfig,
    private val client: HttpClient,
) : LLModel {
    override suspend fun query(text: String): String {
        val apiDataSource = createDataSource(text)
        return when (val response = getResponse(client, apiDataSource)) {
            is KogentAPIResponse.Success -> response.data ?: throw Exception("No response found")
            is KogentAPIResponse.Error -> throw response.exception
        }
    }

    private fun createDataSource(text: String): APIDataSource =
        APIDataSource(
            identifier = config.model,
            baseUrl = "https://api-inference.huggingface.co",
            headers = mapOf("Authorization" to "Bearer ${config.apiToken}"),
            method = APIDataSource.HttpMethod.POST,
            endpoint = "/models/${config.model}",
            body = mapOf("inputs" to text),
        )
}
