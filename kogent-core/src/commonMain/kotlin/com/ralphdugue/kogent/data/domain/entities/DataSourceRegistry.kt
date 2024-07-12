package com.ralphdugue.kogent.data.domain.entities

import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataSource
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataSourceBuilder
import kotlinx.serialization.Serializable

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
    suspend fun getDataSources(): List<DataSource>

    /**
     * Adds a data source to the registry.
     * @param dataSource The data source to add.
     */
    suspend fun registerDataSource(dataSource: DataSource)
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
