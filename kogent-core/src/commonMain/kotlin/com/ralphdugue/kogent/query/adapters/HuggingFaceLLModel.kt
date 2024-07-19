package com.ralphdugue.kogent.query.adapters

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.api.getResponse
import com.ralphdugue.kogent.query.domain.entities.HuggingFaceLLModelConfig
import com.ralphdugue.kogent.query.domain.entities.LLModel
import io.ktor.client.*

class HuggingFaceLLModel(
    private val config: HuggingFaceLLModelConfig,
    private val client: HttpClient,
) : LLModel {
    override suspend fun query(text: String): String {
        val apiDataSource = createDataSource(text)
        return getResponse(client, apiDataSource).fold(
            onSuccess = { it },
            onFailure = { "There was an error generating the response: ${it.localizedMessage}" }
        )
    }

    private fun createDataSource(text: String): APIDataSource =
        APIDataSource(
            identifier = config.endpoint,
            baseUrl = "https://api-inference.huggingface.co",
            headers = mapOf("Authorization" to "Bearer ${config.apiToken}"),
            method = APIDataSource.HttpMethod.POST,
            endpoint = config.endpoint,
            body = text,
        )
}
