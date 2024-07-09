package com.ralphdugue.kogent.dataconnector.domain.entities

/**
 * A data source.
 *
 * Users provide data sources to the Kogent system. These data sources can be of different types,
 * such as SQL databases, CSV files, or other data sources. They are used to build context
 * for the Kogent system to make queries with.
 * @author Ralph Dugue
 */
interface DataSource {
    /**
     * The identifier of the data source.
     */
    val identifier: String
}
