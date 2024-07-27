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
     * @param context The context to use for the query.
     * @return The response from the language model.
     */
    suspend fun query(text: String, context: String): String
}

sealed interface LLModelConfig
data class HuggingFaceLLModelConfig(
    val endpoint: String,
    val apiToken: String,
) : LLModelConfig


/**
 * A builder for the [HuggingFaceLLModelConfig].
 */
class HuggingFaceLLModelConfigBuilder {
    var endpoint: String = "/models/deepset/roberta-base-squad2"
    var apiToken: String? = null

    fun build(): HuggingFaceLLModelConfig =
        HuggingFaceLLModelConfig(
            endpoint = endpoint,
            apiToken = apiToken ?: throw IllegalStateException("apiToken must be set"),
        )
}
