package com.ralphdugue.kogent.dataconnector.domain.entities.sql

/**
 * This class represents the result of an SQL query.
 * @param tableName The name of the table in the result.
 * @param columnNames The names of the columns in the result.
 * @param rows The rows in the result.
 * @param resultType The type of the result.
 */
sealed interface KogentQueryResult {
    val resultType: ResultType
    val queryType: String

    /**
     * This enum represents the type of the result.
     */
    enum class ResultType {
        SUCCESS,
        FAILURE,
    }

    override fun toString(): String

    data class TableQuery(
        val tableName: String,
        val columnNames: Set<String>,
        val rows: List<Map<String, Any?>>,
        override val resultType: ResultType = ResultType.SUCCESS,
        override val queryType: String = "Table Query",
    ) : KogentQueryResult {
        override fun toString(): String {
            val stringBuilder = StringBuilder()
            if (resultType == ResultType.SUCCESS) {
                stringBuilder.append("$queryType:\n")
                stringBuilder.append("Table: $tableName\n")
                stringBuilder.append("Columns: ${columnNames.joinToString(", ")}\n")
                rows.forEach { row ->
                    stringBuilder.append(row.entries.joinToString(", ") { "${it.key}: ${it.value}" })
                    stringBuilder.append("\n")
                }
            } else {
                stringBuilder.append("Query failed.")
            }
            return stringBuilder.toString()
        }
    }

    data class SchemaQuery(
        val schema: Map<String, Map<String, String>>,
        override val resultType: ResultType = ResultType.SUCCESS,
        override val queryType: String = "Database Schema",
    ) : KogentQueryResult {
        override fun toString(): String {
            val stringBuilder = StringBuilder()
            if (resultType == ResultType.SUCCESS) {
                stringBuilder.append("$queryType:\n")
                schema.forEach { (tableName, columns) ->
                    stringBuilder.append("Table: $tableName\n")
                    columns.forEach { (columnName, dataType) ->
                        stringBuilder.append("- $columnName: $dataType\n")
                    }
                }
            } else {
                stringBuilder.append("Query failed.")
            }
            return stringBuilder.toString()
        }
    }
}
