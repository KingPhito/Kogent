package dataconnector.data

import dataconnector.domain.entities.KogentQueryResult
import dataconnector.domain.entities.KogentRESTDataSource
import dataconnector.domain.entities.KogentSQLDataConnector
import dataconnector.domain.entities.KogentSQLDataSource
import indexing.domain.entities.KogentDocument
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import utils.RandomsFactory
import java.sql.Connection
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class KogentSQLDataConnectorTest {
    @OptIn(DelicateCoroutinesApi::class)
    private val mainCoroutineDispatcher = newSingleThreadContext("main")
    private lateinit var subject: KogentSQLDataConnector
    private val dbName: String = "DB_${RandomsFactory.genRandomString()}"
    private val dbUser: String = RandomsFactory.genRandomString()
    private val dbPassword: String = RandomsFactory.genRandomString()
    private val dbTable: String = "table_${RandomsFactory.genRandomString()}"
    private lateinit var dbConnection: Connection

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(mainCoroutineDispatcher)
        subject = SQLDataConnectorImpl()
        dbConnection = FakeDatabaseFactory.createFakeDatabase(dbName, dbUser, dbPassword)
        FakeDatabaseFactory.createTestTable(
            connection = dbConnection,
            tableName = dbTable,
            columns =
                listOf(
                    Pair("id", "INT"),
                    Pair("name", "VARCHAR(255)"),
                    Pair("age", "INT"),
                ),
        )
    }

    @AfterTest
    fun tearDown() {
        dbConnection.close()
        mainCoroutineDispatcher.close()
    }

    /**
     * fetchData tests
     */

    @Test
    fun `fetchData should return a KogentQueryResult with a FAILURE resultType list if the dataSource is not a KogentSQLDataSource`() =
        runTest {
            val dataSource =
                KogentRESTDataSource(
                    identifier = RandomsFactory.genRandomString(),
                    url = RandomsFactory.genRandomString(),
                    endpoint = RandomsFactory.genRandomString(),
                )
            val result = subject.fetchData(dataSource)
            assertEquals(KogentQueryResult.ResultType.FAILURE, result.resultType)
        }

    @Test
    fun `fetchData should return a KogentQueryResult with a FAILURE resultType if the query is null`() =
        runTest {
            val dataSource =
                KogentSQLDataSource(
                    identifier = RandomsFactory.genRandomString(),
                    host = "mem",
                    databaseName = dbName,
                    username = dbUser,
                    password = dbPassword,
                    databaseType = KogentSQLDataSource.DatabaseType.H2,
                )
            val result = subject.fetchData(dataSource)
            assertEquals(KogentQueryResult.ResultType.FAILURE, result.resultType)
        }

    @Test
    fun `fetchData should return a KogentQueryResult with the correct data for a valid query`() =
        runTest {
            val data =
                listOf(
                    listOf(1, "Alice", 25),
                    listOf(2, "Bob", 30),
                    listOf(3, "Charlie", 35),
                )
            FakeDatabaseFactory.insertTestData(dbConnection, dbTable, data)
            val dataSource =
                KogentSQLDataSource(
                    identifier = RandomsFactory.genRandomString(),
                    host = "mem",
                    databaseName = dbName,
                    username = dbUser,
                    password = dbPassword,
                    databaseType = KogentSQLDataSource.DatabaseType.H2,
                    query = "SELECT * FROM $dbTable",
                )
            val result = subject.fetchData(dataSource)
            val expectedColumnNames = listOf("id", "name", "age").map { it.uppercase() }
            val expectedRows = data.map { row -> expectedColumnNames.zip(row).toMap() }
            assertEquals(expectedColumnNames, result.columnNames)
            assertEquals(expectedRows, result.rows)
            assertEquals(KogentQueryResult.ResultType.SUCCESS, result.resultType)
        }

    /**
     * createDocument tests
     */

    @Test
    fun `createDocument should throw an IllegalArgumentException if the source is not a KogentSQLDataSource`() {
        val dataSource =
            KogentRESTDataSource(
                identifier = RandomsFactory.genRandomString(),
                url = RandomsFactory.genRandomString(),
                endpoint = RandomsFactory.genRandomString(),
            )
        val data =
            KogentQueryResult(
                columnNames = listOf("id", "name", "age"),
                rows =
                    listOf(
                        mapOf("id" to 1, "name" to "Alice", "age" to 25),
                        mapOf("id" to 2, "name" to "Bob", "age" to 30),
                        mapOf("id" to 3, "name" to "Charlie", "age" to 35),
                    ),
            )
        try {
            subject.createDocument(data, dataSource)
        } catch (e: IllegalArgumentException) {
            assertEquals("Data source must be an SQL data source", e.message)
        }
    }

    @Test
    fun `createDocument should throw an IllegalArgumentException if the data resultType is FAILURE`() {
        val dataSource =
            KogentSQLDataSource(
                identifier = RandomsFactory.genRandomString(),
                host = "mem",
                databaseName = dbName,
                username = dbUser,
                password = dbPassword,
                databaseType = KogentSQLDataSource.DatabaseType.H2,
            )
        val data = KogentQueryResult(emptyList(), emptyList(), KogentQueryResult.ResultType.FAILURE)
        try {
            subject.createDocument(data, dataSource)
        } catch (e: IllegalArgumentException) {
            assertEquals("Cannot create document from failed query result", e.message)
        }
    }

    @Test
    fun `createDocument should return a KogentDocument with the correct data`() {
        val expected =
            KogentDocument.KogentSQLDocument(
                id = RandomsFactory.genRandomString(),
                sourceType = "SQL",
                sourceName = dbName,
                content =
                    KogentQueryResult(
                        columnNames = listOf("id", "name", "age"),
                        rows =
                            listOf(
                                mapOf("id" to 1, "name" to "Alice", "age" to 25),
                                mapOf("id" to 2, "name" to "Bob", "age" to 30),
                                mapOf("id" to 3, "name" to "Charlie", "age" to 35),
                            ),
                    ),
            )
        val dataSource =
            KogentSQLDataSource(
                identifier = expected.id,
                host = "mem",
                databaseName = dbName,
                username = dbUser,
                password = dbPassword,
                databaseType = KogentSQLDataSource.DatabaseType.H2,
            )
        val result = subject.createDocument(expected.content, dataSource)
        assertEquals(expected, result)
    }

    @Test
    fun `executeQuery should return a KogentQueryResult with a FAILURE resultType if an exception is thrown`() =
        runTest {
            val dataSource =
                KogentSQLDataSource(
                    identifier = RandomsFactory.genRandomString(),
                    host = "mem",
                    databaseName = dbName,
                    username = dbUser,
                    password = dbPassword,
                    databaseType = KogentSQLDataSource.DatabaseType.H2,
                )
            val result = subject.executeQuery(dataSource, "SELECT * FROM non_existent_table")
            assertEquals(KogentQueryResult.ResultType.FAILURE, result.resultType)
        }
}
