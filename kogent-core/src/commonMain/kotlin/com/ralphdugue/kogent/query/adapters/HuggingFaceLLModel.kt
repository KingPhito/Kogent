package com.ralphdugue.kogent.query.adapters

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.api.getResponse
import com.ralphdugue.kogent.query.domain.entities.LLModel
import com.ralphdugue.kogent.query.domain.entities.LLModelConfig
import io.ktor.client.*

class HuggingFaceLLModel(
    private val config: LLModelConfig.HuggingFaceLLModelConfig,
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
            identifier = config.model,
            baseUrl = "https://api-inference.huggingface.co",
            headers = mapOf("Authorization" to "Bearer ${config.apiToken}"),
            method = APIDataSource.HttpMethod.POST,
            endpoint = "/models/${config.model}",
            body = text,
        )
}
