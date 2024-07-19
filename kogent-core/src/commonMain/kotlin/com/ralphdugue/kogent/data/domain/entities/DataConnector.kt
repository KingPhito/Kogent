package com.ralphdugue.kogent.data.domain.entities

/**
 * A data connector.
 *
 * Data connectors are classes that are used to connect to data sources and fetch data from them.
 * They are also used to update data sources and delete data from them, as well as from the [Index].
 * @author Ralph Dugue
 */
interface DataConnector {
    /**
     * This function fetches data from a [DataSource], and adds it to the index.
     * @param dataSource The data source to fetch data from.
     * @return success if the data was fetched and added to the index, failure otherwise.
     */
    suspend fun indexData(dataSource: DataSource): Result<Unit>

    /**
     * This function updates a [DataSource].
     * @param dataSource The data source to update the data of.
     * @return success if the data was updated, failure otherwise.
     */
    suspend fun updateData(dataSource: DataSource): Result<Unit>

    /**
     * This function deletes a data source from the index.
     * @param dataSource The data source to delete data.
     * @return success if the data was deleted, failure otherwise.
     */
    suspend fun removeData(dataSource: DataSource): Result<Unit>

}
