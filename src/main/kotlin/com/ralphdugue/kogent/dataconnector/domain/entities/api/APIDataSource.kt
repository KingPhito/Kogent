package com.ralphdugue.kogent.dataconnector.domain.entities.api

import com.ralphdugue.kogent.dataconnector.domain.entities.DataSource

/**
 * This class represents a REST data source.
 * @param identifier The identifier of the data source.
 * @param url The URL of the REST API.
 * @param endpoint The endpoint to query.
 * @param method The HTTP method to use.
 * @param headers The headers to use (optional).
 * @param queryParams The query parameters to use (optional).
 * @param body The body of the request (optional).
 */
data class APIDataSource(
    override val identifier: String,
    val url: String,
    val endpoint: String, // Specific API endpoint to query
    val method: HttpMethod, // HTTP method (GET, POST, etc.)
    val headers: Map<String, String>?, // Headers (optional)
    val queryParams: Map<String, String>?, // Query parameters (optional)
    val body: String? = null,
) : DataSource {
    /**
     * This enum represents the HTTP method.
     */
    enum class HttpMethod {
        GET,
        POST,
        PUT,
        DELETE,
    }

    override fun toString(): String {
        TODO()
    }
}
