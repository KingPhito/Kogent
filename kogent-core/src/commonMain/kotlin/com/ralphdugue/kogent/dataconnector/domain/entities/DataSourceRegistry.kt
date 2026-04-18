package com.ralphdugue.kogent.dataconnector.domain.entities

/**
 * This interface is used to register and retrieve data sources that were added to the Kogent system.
 * This is useful for choosing which data source to query from.
 */
interface DataSourceRegistry {
    /**
     * Returns a list of all the data sources that were added to the Kogent system.
     * @return a list of [DataSource] objects
     */
    suspend fun getDataSources(): List<DataSource>

    suspend fun registerDataSource(dataSource: DataSource)
}
