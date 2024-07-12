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
        val model: String = "all-MiniLM-L6-v2",
        val apiToken: String,
    ) : LLModelConfig
}

class HuggingFaceLLModelConfigBuilder {
    var model: String = "all-MiniLM-L6-v2"
    var apiToken: String? = null

    fun build(): LLModelConfig.HuggingFaceLLModelConfig =
        LLModelConfig.HuggingFaceLLModelConfig(
            model = model,
            apiToken = apiToken ?: throw IllegalStateException("apiToken must be set"),
        )
}
