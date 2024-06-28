package dataconnector.data

import com.ralphdugue.kogent.dataconnector.adapters.connectors.KogentSQLDataConnectorImpl
import com.ralphdugue.kogent.dataconnector.domain.entities.api.KogentAPIDataSource
import com.ralphdugue.kogent.dataconnector.domain.entities.sql.KogentQueryResult
import com.ralphdugue.kogent.dataconnector.domain.entities.sql.KogentSQLDataConnector
import com.ralphdugue.kogent.dataconnector.domain.entities.sql.KogentSQLDataSource
import com.ralphdugue.kogent.indexing.domain.entities.KogentDocument
import io.mockk.mockkClass
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import utils.FakeDatabaseFactory
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
        subject = KogentSQLDataConnectorImpl()
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
    fun `fetchData should return a failed result when the data source is not an SQL data source`() =
        runTest {
            val actual =
                subject.fetchData(
                    dataSource = mockkClass(KogentAPIDataSource::class),
                )

            val expected =
                KogentQueryResult.TableQuery(
                    tableName = "",
                    columnNames = emptySet(),
                    rows = emptyList(),
                    resultType = KogentQueryResult.ResultType.FAILURE,
                )
            assertEquals(expected.toString().uppercase(), actual.toString().uppercase())
            assertEquals(expected.resultType, actual.resultType)
        }

    @Test
    fun `fetchData should return a successful result, with the correct data, when the query is successful`() =
        runTest {
            FakeDatabaseFactory.insertTestData(
                connection = dbConnection,
                tableName = dbTable,
                data =
                    listOf(
                        listOf(1, "Alice", 25),
                        listOf(2, "Bob", 30),
                    ),
            )

            val actual =
                subject.fetchData(
                    dataSource =
                        KogentSQLDataSource(
                            identifier = RandomsFactory.genRandomString(),
                            databaseType = KogentSQLDataSource.DatabaseType.H2,
                            host = "mem",
                            databaseName = dbName,
                            username = dbUser,
                            password = dbPassword,
                            query = "SELECT * FROM $dbTable",
                        ),
                )

            val expected =
                KogentQueryResult.TableQuery(
                    tableName = dbTable,
                    columnNames = setOf("ID", "NAME", "AGE"),
                    rows =
                        listOf(
                            mapOf("id" to 1, "NAME" to "Alice", "AGE" to 25),
                            mapOf("id" to 2, "NAME" to "Bob", "AGE" to 30),
                        ),
                )
            assertEquals(expected.toString().uppercase(), actual.toString().uppercase())
            assertEquals(expected.resultType, actual.resultType)
        }

    /**
     * updateData tests
     */

    @Test
    fun `updateData should return a failed result when the query did not update any rows`() =
        runTest {
            val actual =
                subject.updateData(
                    dataSource =
                        KogentSQLDataSource(
                            identifier = RandomsFactory.genRandomString(),
                            databaseType = KogentSQLDataSource.DatabaseType.H2,
                            host = "mem",
                            databaseName = dbName,
                            username = dbUser,
                            password = dbPassword,
                        ),
                    query = "UPDATE $dbTable SET name = 'Charlie' WHERE id = 3",
                )

            val expected =
                KogentQueryResult.TableQuery(
                    tableName = "",
                    columnNames = emptySet(),
                    rows = emptyList(),
                    resultType = KogentQueryResult.ResultType.FAILURE,
                )
            assertEquals(expected.toString().uppercase(), actual.toString().uppercase())
            assertEquals(expected.resultType, actual.resultType)
        }

    @Test
    fun `updateData should return a successful result when the query updated rows`() =
        runTest {
            FakeDatabaseFactory.insertTestData(
                connection = dbConnection,
                tableName = dbTable,
                data =
                    listOf(
                        listOf(1, "Alice", 25),
                        listOf(2, "Bob", 30),
                    ),
            )

            val actual =
                subject.updateData(
                    dataSource =
                        KogentSQLDataSource(
                            identifier = RandomsFactory.genRandomString(),
                            databaseType = KogentSQLDataSource.DatabaseType.H2,
                            host = "mem",
                            databaseName = dbName,
                            username = dbUser,
                            password = dbPassword,
                        ),
                    query = "UPDATE $dbTable SET name = 'Charlie' WHERE id = 2",
                )

            val expected =
                KogentQueryResult.TableQuery(
                    tableName = "",
                    columnNames = emptySet(),
                    rows = emptyList(),
                    resultType = KogentQueryResult.ResultType.SUCCESS,
                )
            assertEquals(expected.toString().uppercase(), actual.toString().uppercase())
            assertEquals(expected.resultType, actual.resultType)
        }

    /**
     * fetchSchema tests
     */

    @Test
    fun `fetchSchema should return a successful result, with the correct schema, when the query is successful`() =
        runTest {
            val actual =
                subject.fetchSchema(
                    dataSource =
                        KogentSQLDataSource(
                            identifier = RandomsFactory.genRandomString(),
                            databaseType = KogentSQLDataSource.DatabaseType.H2,
                            host = "mem",
                            databaseName = dbName,
                            username = dbUser,
                            password = dbPassword,
                        ),
                )

            val expected =
                KogentQueryResult.SchemaQuery(
                    schema =
                        mapOf(
                            dbTable to
                                mutableMapOf(
                                    "ID" to "INTEGER",
                                    "NAME" to "CHARACTER VARYING",
                                    "AGE" to "INTEGER",
                                ),
                        ),
                )
            assertEquals(expected.toString().uppercase(), actual.toString().uppercase())
            assertEquals(expected.resultType, actual.resultType)
        }

    /**
     * createDocument tests
     */

    @Test
    fun `createDocument should throw an exception if the data source is not an SQL data source`() =
        runTest {
            try {
                subject.createDocument(
                    data = mockkClass(KogentQueryResult::class),
                    source = mockkClass(KogentAPIDataSource::class),
                )
            } catch (e: Exception) {
                assertEquals("Data source is not an SQL data source.", e.message)
            }
        }

    @Test
    fun `createDocument should throw an exception if the query result is a failure`() =
        runTest {
            try {
                subject.createDocument(
                    data =
                        KogentQueryResult.TableQuery(
                            tableName = "",
                            columnNames = emptySet(),
                            rows = emptyList(),
                            resultType = KogentQueryResult.ResultType.FAILURE,
                        ),
                    source =
                        KogentSQLDataSource(
                            identifier = RandomsFactory.genRandomString(),
                            databaseType = KogentSQLDataSource.DatabaseType.H2,
                            host = "mem",
                            databaseName = dbName,
                            username = dbUser,
                            password = dbPassword,
                        ),
                )
            } catch (e: Exception) {
                assertEquals("Cannot create document from failed query result", e.message)
            }
        }

    @Test
    fun `createDocument should return a KogentSQLDocument with the correct content`() =
        runTest {
            val actual =
                subject.createDocument(
                    data =
                        KogentQueryResult.TableQuery(
                            tableName = dbTable,
                            columnNames = setOf("ID", "NAME", "AGE"),
                            rows =
                                listOf(
                                    mapOf("id" to 1, "NAME" to "Alice", "AGE" to 25),
                                    mapOf("id" to 2, "NAME" to "Bob", "AGE" to 30),
                                ),
                        ),
                    source =
                        KogentSQLDataSource(
                            identifier = RandomsFactory.genRandomString(),
                            databaseType = KogentSQLDataSource.DatabaseType.H2,
                            host = "mem",
                            databaseName = dbName,
                            username = dbUser,
                            password = dbPassword,
                        ),
                )

            val expected =
                KogentDocument.KogentSQLDocument(
                    id = actual.id,
                    sourceType = "SQL",
                    sourceName = dbName,
                    content =
                        "Table Query:\n" +
                            "Table: $dbTable\n" +
                            "Columns: ID, NAME, AGE\n" +
                            "id: 1, NAME: Alice, AGE: 25\n" +
                            "id: 2, NAME: Bob, AGE: 30\n",
                )
            assertEquals(expected.toString().uppercase(), actual.toString().uppercase())
        }
}
