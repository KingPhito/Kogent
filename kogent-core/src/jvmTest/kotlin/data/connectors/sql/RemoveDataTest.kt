package data.connectors.sql

import com.ralphdugue.kogent.data.adapters.connectors.KogentSQLDataConnector
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.SQLDataSource
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.indexing.domain.entities.Index
import common.BaseTest
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import utils.FakeDatabaseFactory
import utils.RandomPrimitivesFactory
import java.sql.Connection
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class RemoveDataTest : BaseTest() {
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
    override fun tearDown() {
        super.tearDown()
        dbConnection.close()
    }

    @Test
    fun `removeData should return success when data is removed from the index successfully`() =
        runTest {
            coEvery { index.deleteDocument(any()) } returns true
            coEvery { dataSourceRegistry.removeDataSource(any()) } returns Result.success(Unit)
            val dataSource = SQLDataSource(
                identifier = RandomPrimitivesFactory.genRandomString(),
                databaseType = SQLDataSource.DatabaseType.H2,
                databaseName = dbName,
                username = dbUser,
                password = dbPassword,
                host = "mem",
                query = "SELECT * FROM $dbTable",
            )
            val actual = subject.removeData(dataSource)
            assert(actual.isSuccess)
        }

    @Test
    fun `removeData should return a failed result when the data is not removed from the index successfully`() =
        runTest {
            coEvery { index.deleteDocument(any()) } returns false
            coEvery { dataSourceRegistry.removeDataSource(any()) } returns Result.success(Unit)
            val dataSource = SQLDataSource(
                identifier = RandomPrimitivesFactory.genRandomString(),
                databaseType = SQLDataSource.DatabaseType.H2,
                databaseName = dbName,
                username = dbUser,
                password = dbPassword,
                host = "mem",
                query = "SELECT * FROM $dbTable",
            )
            val actual = subject.removeData(dataSource)
            assert(actual.isFailure)
        }
}