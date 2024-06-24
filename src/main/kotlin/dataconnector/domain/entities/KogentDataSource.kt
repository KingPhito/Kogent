package dataconnector.domain.entities

/**
 * This is the interface for all DataSources to inherit from.
 * DataSources are classes that represent anything from an SQL database to a REST API.
 */
sealed interface KogentDataSource {
    /**
     * The identifier of the data source.
     */
    val identifier: String
}
