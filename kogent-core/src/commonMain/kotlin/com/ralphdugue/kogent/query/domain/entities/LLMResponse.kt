package com.ralphdugue.kogent.query.domain.entities

import kotlinx.serialization.Serializable

/**
 * A response from the Large Language Model (LLM).
 *
 * This represents the response from the Large Language Model (LLM) to a user query. It contains
 * the answer to the query, whether the underlying data needs an update, and the operation to perform.
 * @param answer Human-readable answer to the query
 * @param needsUpdate Whether the underlying data needs an update
 * @param operation The operation to perform if update is needed (null if not)
 * @author Ralph Dugue
 */
@Serializable
data class LLMResponse(
    val answer: String,            // Human-readable answer to the query
    val needsUpdate: Boolean,      // Whether the underlying data needs an update
    val operation: Operation? = null // The operation to perform if update is needed (null if not)
)

@Serializable
sealed interface Operation {       // Sealed class to define different operation types
    val dataSource: String   // Identifier of the data source to update

    @Serializable
    data class SqlQuery(
        override val dataSource: String,
        val query: String               // The SQL query to execute
    ) : Operation

    @Serializable
    data class ApiCall(
        override val dataSource: String,
        val endpoint: String,            // API endpoint to call
        val method: String = "GET",   // HTTP method (default to GET)
        val body: String? = null        // Optional request body (for POST, PUT, etc.)
    ) : Operation
}
