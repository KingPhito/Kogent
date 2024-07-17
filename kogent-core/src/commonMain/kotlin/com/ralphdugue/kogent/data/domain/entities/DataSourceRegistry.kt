package com.ralphdugue.kogent.data.domain.entities

/**
 * A registry for all the data sources that were added to the Kogent system by the user.
 *
 * This is useful for choosing which data source to query from. The registry will be
 * updated by the [DataConnector] classes when they index data from a data source.
 * @author Ralph Dugue
 */
interface DataSourceRegistry {
    /**
     * Returns a list of all the data sources that were added to the Kogent system.
     * @return a list of [DataSource] objects
     */
    suspend fun getDataSources(): Result<List<DataSource>>

    /**
     * Adds a data source to the registry.
     * @param dataSource The data source to add.
     * @return success if the data source was added successfully, failure otherwise.
     */
    suspend fun registerDataSource(dataSource: DataSource): Result<Unit>

    /**
     * Gets a [DataSource] by its identifier.
     * @param identifier The identifier of the data source.
     */
    suspend fun getDataSourceById(identifier: String): Result<DataSource>

    /**
     * Removes a data source from the registry.
     * @param identifier The identifier of the data source to remove.
     * @return success if the data source was removed successfully, failure otherwise.
     */
    suspend fun removeDataSource(identifier: String): Result<Unit>

    /**
     * Updates a data source in the registry.
     * @param dataSource The data source to update.
     * @return success if the data source was updated successfully, failure otherwise.
     */
    suspend fun updateDataSource(dataSource: DataSource): Result<Unit>
}

/**
 * The type of registry that is used by the Kogent system.
 *
 * The registry can be an embedded registry, which is a local SQLite database, or an external
 * registry, which is a remote SQL database provided by the user in the configuration,
 * using the [SQLDataSource] class.
 * @author Ralph Dugue
 */
sealed interface RegistryType

data object LocalRegistry : RegistryType

data class ExternalRegistry(
    val dataSource: SQLDataSource,
) : RegistryType

class ExternalRegistryBuilder {
    private var dataSource: SQLDataSource? = null

    fun dataSource(block: SQLDataSourceBuilder.() -> Unit) {
        dataSource = SQLDataSourceBuilder().apply(block).build()
    }

    fun build(): ExternalRegistry =
        ExternalRegistry(
            dataSource = dataSource ?: throw IllegalStateException("dataSource must be set"),
        )
}
