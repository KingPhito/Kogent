package com.ralphdugue.kogent.query.domain.entities

/**
 * A large language model.
 *
 * A language model is a model that can generate text based on a given input. It is used to generate
 * responses to user queries in the Kogent system.
 * @author Ralph Dugue
 */
interface LLModel {
    /**
     * Queries the language model with the given text.
     * @param text The text to query the language model with.
     * @return The response from the language model.
     */
    suspend fun query(text: String): String
}

sealed interface LLModelConfig {
    data class HuggingFaceLLModelConfig(
        val connectionString: String,
        val credentials: List<Map<String, String>>,
    ) : LLModelConfig
}

class HuggingFaceLLModelConfigBuilder {
    var connectionString: String? = null
    var credentials: List<Map<String, String>> = emptyList()

    fun build(): LLModelConfig.HuggingFaceLLModelConfig =
        LLModelConfig.HuggingFaceLLModelConfig(
            connectionString = connectionString ?: throw IllegalStateException("connectionString must be set"),
            credentials = credentials,
        )
}
