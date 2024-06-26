package dataconnector.domain.entities

/**
 * This class represents a REST data source.
 * @param identifier The identifier of the data source.
 * @param url The URL of the REST API.
 * @param endpoint The endpoint to query.
 */
data class KogentRESTDataSource(
    override val identifier: String,
    val url: String,
    val endpoint: String,
) : KogentDataSource
