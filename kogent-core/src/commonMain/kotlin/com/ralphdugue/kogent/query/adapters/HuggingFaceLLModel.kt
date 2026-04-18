package com.ralphdugue.kogent.query.adapters

import com.ralphdugue.kogent.query.domain.entities.LLModel
import com.ralphdugue.kogent.query.domain.entities.LLModelConfig
import io.ktor.client.HttpClient

class HuggingFaceLLModel(
    private val config: LLModelConfig.HuggingFaceLLModelConfig,
    private val client: HttpClient,
) : LLModel {
    override suspend fun query(text: String): String {
        TODO("Not yet implemented")
    }
}
