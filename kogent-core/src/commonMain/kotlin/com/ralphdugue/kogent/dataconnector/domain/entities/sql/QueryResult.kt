package com.ralphdugue.kogent.dataconnector.domain.entities.sql

/**
 * This class represents the result of an SQL query.
 * @param tableName The name of the table in the result.
 * @param columnNames The names of the columns in the result.
 * @param rows The rows in the result.
 * @param resultType The type of the result.
 */
sealed interface QueryResult {
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
    ) : QueryResult {
        override fun toString(): String {
            val stringBuilder = StringBuilder()
            if (resultType == ResultType.SUCCESS) {
                stringBuilder.appendLine("$queryType:")
                stringBuilder.appendLine("Table: $tableName")
                stringBuilder.appendLine("Columns: ${columnNames.joinToString(" | ")}")
                rows.forEach { row ->
                    stringBuilder.appendLine(row.entries.joinToString(" | ") { "${it.key}: ${it.value}" })
                }
            } else {
                stringBuilder.appendLine("Could not fetch data.")
            }
            return stringBuilder.toString()
        }
    }

    data class SchemaQuery(
        val schema: Map<String, Map<String, String>>,
        override val resultType: ResultType = ResultType.SUCCESS,
        override val queryType: String = "Database Schema",
    ) : QueryResult {
        override fun toString(): String {
            val stringBuilder = StringBuilder()
            if (resultType == ResultType.SUCCESS) {
                stringBuilder.appendLine("$queryType:")
                schema.forEach { (tableName, columns) ->
                    stringBuilder.appendLine("Table: $tableName")
                    columns.forEach { (columnName, dataType) ->
                        stringBuilder.appendLine("- $columnName: $dataType")
                    }
                }
                stringBuilder.appendLine("-----------------------------")
            } else {
                stringBuilder.appendLine("Could not fetch schema.")
            }
            return stringBuilder.toString()
        }
    }
}
