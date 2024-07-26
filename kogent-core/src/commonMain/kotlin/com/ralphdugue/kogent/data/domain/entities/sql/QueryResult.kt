package com.ralphdugue.kogent.data.domain.entities.sql

/**
 * This class represents the result of an SQL query.
 * @param tableName The name of the table in the result.
 * @param columnNames The names of the columns in the result.
 * @param rows The rows in the result.
 */
sealed interface QueryResult {
    val queryType: String

    override fun toString(): String

    data class TableQuery(
        val tableName: String,
        val columnNames: Set<String>,
        val rows: List<Map<String, Any?>>,
        override val queryType: String = "Table Query",
    ) : QueryResult {
        override fun toString(): String {
            val stringBuilder = StringBuilder()
            stringBuilder.appendLine("$queryType:")
            stringBuilder.appendLine("Table: $tableName")
            stringBuilder.appendLine("Columns: ${columnNames.joinToString(" | ")}")
            rows.forEach { row ->
                stringBuilder.appendLine(row.entries.joinToString(" | ") { "${it.key}: ${it.value}" })
            }
            return stringBuilder.toString()
        }
    }

    data class SchemaQuery(
        val schema: Map<String, Map<String, String>>,
        override val queryType: String = "Database Schema",
    ) : QueryResult {
        override fun toString(): String {
            val stringBuilder = StringBuilder()
            stringBuilder.appendLine("$queryType:")
            schema.forEach { (tableName, columns) ->
                stringBuilder.appendLine("Table: $tableName")
                columns.forEach { (columnName, dataType) ->
                    stringBuilder.appendLine("- $columnName: $dataType")
                }
            }
            stringBuilder.appendLine("-----------------------------")
            return stringBuilder.toString()
        }
    }
}
