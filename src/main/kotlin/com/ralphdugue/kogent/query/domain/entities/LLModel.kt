package com.ralphdugue.kogent.query.domain.entities

interface LLModel {
    suspend fun query(text: String): String
}

sealed interface LLModelConfig {
    data class HuggingFaceLLModelConfig(
        val connectionString: String,
        val credentials: List<Map<String, String>>,
    ) : LLModelConfig
}
