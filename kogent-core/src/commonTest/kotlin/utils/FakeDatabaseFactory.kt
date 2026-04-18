package utils

import org.h2.jdbcx.JdbcDataSource
import java.sql.Connection

object FakeDatabaseFactory {
    fun createFakeDatabase(
        dbName: String,
        user: String,
        password: String,
    ): Connection {
        val dataSource = JdbcDataSource()
        dataSource.setURL("jdbc:h2:mem:$dbName")
        dataSource.user = user
        dataSource.password = password
        return dataSource.connection
    }

    fun createTestTable(
        connection: Connection,
        tableName: String,
        columns: List<Pair<String, String>>,
    ) {
        val createTableStatement =
            """
            CREATE TABLE $tableName (
                ${columns.joinToString(", ") { (name, type) -> "$name $type" }}
            )
            """.trimIndent()
        connection.createStatement().execute(createTableStatement)
    }

    fun insertTestData(
        connection: Connection,
        tableName: String,
        data: List<List<Any?>>,
    ) {
        val columns = (1..data[0].size).joinToString(", ") { "?" }
        val insertStatement = "INSERT INTO $tableName VALUES ($columns)"
        val preparedStatement = connection.prepareStatement(insertStatement)

        for (row in data) {
            for (i in row.indices) {
                preparedStatement.setObject(i + 1, row[i])
            }
            preparedStatement.executeUpdate()
        }
    }
}
