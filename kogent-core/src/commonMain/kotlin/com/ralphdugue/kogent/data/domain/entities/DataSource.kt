package com.ralphdugue.kogent.data.domain.entities

import kotlinx.serialization.Serializable

/**
 * A data source.
 *
 * Users provide data sources to the Kogent system. These data sources can be of different types,
 * such as SQL databases, PDF files, or APIs. They are used to build context
 * for the Kogent system to make queries with.
 * @author Ralph Dugue
 */
@Serializable
sealed interface DataSource {
    /**
     * The identifier of the data source.
     */
    val identifier: String

    /**
     * The type of the data source.
     */
    val dataSourceType: DataSourceType
}

@Serializable
enum class DataSourceType {
    API, SQL
}