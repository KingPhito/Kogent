package data.connectors

import com.ralphdugue.kogent.data.adapters.connectors.KogentSQLDataConnector
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.api.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.sql.QueryResult
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataSource
import com.ralphdugue.kogent.indexing.domain.entities.Index
import common.BaseTest
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockkClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import utils.FakeDatabaseFactory
import utils.RandomPrimitivesFactory
import java.sql.Connection
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SQLDataConnectorTest : BaseTest() {
    @MockK
    private lateinit var embeddingModel: EmbeddingModel

    @MockK
    private lateinit var index: Index

    @MockK
    private lateinit var dataSourceRegistry: DataSourceRegistry

    private lateinit var subject: SQLDataConnector
    private val dbName: String = "DB_${RandomPrimitivesFactory.genRandomString()}"
    private val dbUser: String = RandomPrimitivesFactory.genRandomString()
    private val dbPassword: String = RandomPrimitivesFactory.genRandomString()
    private val dbTable: String = "table_${RandomPrimitivesFactory.genRandomString()}"
    private lateinit var dbConnection: Connection

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(mainCoroutineDispatcher)
        coEvery { embeddingModel.getEmbedding(any()) } returns RandomPrimitivesFactory.genRandomFloatList()
        coEvery { index.indexDocument(any()) } returns true
        subject = KogentSQLDataConnector(embeddingModel, index, dataSourceRegistry)
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
     * readQuery tests
     */

    @Test
    fun `readQuery should return a failed result when the data source is not an SQL data source`() =
        runTest {
            val actual =
                subject.readQuery(
                    dataSource = mockkClass(APIDataSource::class),
                )

            val expected =
                QueryResult.TableQuery(
                    tableName = "",
                    columnNames = emptySet(),
                    rows = emptyList(),
                    resultType = QueryResult.ResultType.FAILURE,
                )
            assertEquals(expected.toString().uppercase(), actual.toString().uppercase())
            assertEquals(expected.resultType, actual.resultType)
        }

    @Test
    fun `readQuery should return a failed result when the query is null`() =
        runTest {
            val actual =
                subject.readQuery(
                    dataSource =
                        SQLDataSource(
                            identifier = RandomPrimitivesFactory.genRandomString(),
                            databaseType = SQLDataSource.DatabaseType.H2,
                            host = "mem",
                            databaseName = dbName,
                            username = dbUser,
                            password = dbPassword,
                        ),
                )

            val expected =
                QueryResult.TableQuery(
                    tableName = "",
                    columnNames = emptySet(),
                    rows = emptyList(),
                    resultType = QueryResult.ResultType.FAILURE,
                )
            assertEquals(expected.toString().uppercase(), actual.toString().uppercase())
            assertEquals(expected.resultType, actual.resultType)
        }

    @Test
    fun `readQuery should return a successful result, with the correct data, when the query is successful`() =
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
                subject.readQuery(
                    dataSource =
                        SQLDataSource(
                            identifier = RandomPrimitivesFactory.genRandomString(),
                            databaseType = SQLDataSource.DatabaseType.H2,
                            host = "mem",
                            databaseName = dbName,
                            username = dbUser,
                            password = dbPassword,
                            query = "SELECT * FROM $dbTable",
                        ),
                )

            val expected =
                QueryResult.TableQuery(
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
     * writeQuery tests
     */

    @Test
    fun `writeQuery should return a failed result when the query did not update any rows`() =
        runTest {
            val actual =
                subject.writeQuery(
                    dataSource =
                        SQLDataSource(
                            identifier = RandomPrimitivesFactory.genRandomString(),
                            databaseType = SQLDataSource.DatabaseType.H2,
                            host = "mem",
                            databaseName = dbName,
                            username = dbUser,
                            password = dbPassword,
                        ),
                    query = "UPDATE $dbTable SET name = 'Charlie' WHERE id = 3",
                )

            val expected =
                QueryResult.TableQuery(
                    tableName = "",
                    columnNames = emptySet(),
                    rows = emptyList(),
                    resultType = QueryResult.ResultType.FAILURE,
                )
            assertEquals(expected.toString().uppercase(), actual.toString().uppercase())
            assertEquals(expected.resultType, actual.resultType)
        }

    @Test
    fun `writeQuery should return a successful result when the query updated rows`() =
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
                subject.writeQuery(
                    dataSource =
                        SQLDataSource(
                            identifier = RandomPrimitivesFactory.genRandomString(),
                            databaseType = SQLDataSource.DatabaseType.H2,
                            host = "mem",
                            databaseName = dbName,
                            username = dbUser,
                            password = dbPassword,
                        ),
                    query = "UPDATE $dbTable SET name = 'Charlie' WHERE id = 2",
                )

            val expected =
                QueryResult.TableQuery(
                    tableName = "",
                    columnNames = emptySet(),
                    rows = emptyList(),
                    resultType = QueryResult.ResultType.SUCCESS,
                )
            assertEquals(expected.toString().uppercase(), actual.toString().uppercase())
            assertEquals(expected.resultType, actual.resultType)
        }
}
