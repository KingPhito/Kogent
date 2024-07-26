package data.connectors.sql

import com.ralphdugue.kogent.data.adapters.connectors.KogentSQLDataConnector
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.DataSourceType
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.sql.QueryResult
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.data.domain.entities.SQLDataSource
import com.ralphdugue.kogent.indexing.domain.entities.Index
import common.BaseTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import utils.FakeDatabaseFactory
import utils.RandomPrimitivesFactory
import java.sql.Connection
import kotlin.test.*

class ReadQueryTest : BaseTest() {
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
    override fun setUp() {
        super.setUp()
        coEvery { embeddingModel.getEmbedding(any()) } returns Result.success(RandomPrimitivesFactory.genRandomFloatList())
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
    override fun tearDown() {
        super.tearDown()
        dbConnection.close()
    }

    @Test
    fun `readQuery should return a failure when the query causes an exception`() =
        runTest {
            val actual =
                subject.readQuery(
                    dataSource =
                    SQLDataSource(
                        identifier = RandomPrimitivesFactory.genRandomString(),
                        dataSourceType = DataSourceType.SQL,
                        databaseType = SQLDataSource.DatabaseType.H2,
                        host = "mem",
                        databaseName = dbName,
                        username = dbUser,
                        password = dbPassword,
                        query = "SELECT * FROM bad_table",
                    ),
                )
            val expected = Result.failure<QueryResult.TableQuery>(actual.exceptionOrNull()!!)
            assertTrue(actual.isFailure, "Expected Result.failure but was ${actual.getOrNull()}")
            assertEquals(expected.exceptionOrNull().toString(), actual.exceptionOrNull().toString())
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
                        dataSourceType = DataSourceType.SQL,
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
        }

}