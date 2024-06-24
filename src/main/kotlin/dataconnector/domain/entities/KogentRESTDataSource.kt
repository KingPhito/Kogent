package dataconnector.domain.entities

/**
 * This class represents a REST data source.
 * @param identifier The identifier of the data source.
 * @param url The URL of the REST API.
 * @param query The specific endpoint to retrieve data from the REST API.
 */
data class KogentRESTDataSource(
    override val identifier: String,
    val url: String,
    val query: String,
) : KogentDataSource
