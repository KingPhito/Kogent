package dataconnector.domain.entities

/**
 * This class represents the result of an SQL query.
 * @param columnNames The names of the columns in the result.
 * @param rows The rows in the result.
 */
data class SQLQueryResult(
    val columnNames: List<String>,
    val rows: List<List<Any?>>,
)
