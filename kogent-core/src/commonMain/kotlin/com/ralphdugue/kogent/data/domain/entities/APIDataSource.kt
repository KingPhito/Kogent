package com.ralphdugue.kogent.data.domain.entities

import kotlinx.serialization.Serializable

/**
 * A REST API data source.
 *
 * This data source is used to query a REST API.
 * @param identifier The identifier of the data source.
 * @param baseUrl The base URL of the API.
 * @param endpoint The endpoint to query.
 * @param method The HTTP method to use.
 * @param headers The headers to use (optional).
 * @param queryParams The query parameters to use (optional).
 * @param body The body of the request (optional).
 */
@Serializable
data class APIDataSource(
    override val identifier: String,
    override val dataSourceType: DataSourceType = DataSourceType.API,
    val baseUrl: String,
    val endpoint: String, // Specific API endpoint to query
    val method: HttpMethod, // HTTP method (GET, POST, etc.)
    val headers: Map<String, String>?, // Headers (optional)
    val queryParams: Map<String, String>? = null, // Query parameters (optional)
    val body: String? = null,
) : DataSource {
    /**
     * This enum represents the HTTP method.
     */
    @Serializable
    enum class HttpMethod {
        GET,
        POST,
        PUT,
        DELETE,
    }
}

/**
 * A builder for the [APIDataSource].
 */
class ApiDataSourceBuilder {
    var identifier: String? = null
    var baseUrl: String? = null
    var endpoint: String? = null
    var method: APIDataSource.HttpMethod? = null
    var headers: Map<String, String>? = null
    var queryParams: Map<String, String>? = null
    var body: String? = null

    fun build(): APIDataSource {
        // Check for required parameters
        val id = identifier ?: throw IllegalArgumentException("Identifier must be provided")
        val url = baseUrl ?: throw IllegalArgumentException("Base URL must be provided")
        val end = endpoint ?: throw IllegalArgumentException("Endpoint must be provided")
        val meth = method ?: throw IllegalArgumentException("HTTP method must be provided")

        return APIDataSource(id, DataSourceType.API, url, end, meth, headers, queryParams, body)
    }
}
