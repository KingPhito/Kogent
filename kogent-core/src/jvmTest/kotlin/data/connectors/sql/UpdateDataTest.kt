package data.connectors.sql

import com.ralphdugue.kogent.data.adapters.connectors.KogentSQLDataConnector
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.SQLDataSource
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.indexing.domain.entities.Index
import common.BaseTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import utils.FakeDatabaseFactory
import utils.RandomPrimitivesFactory
import java.sql.Connection
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class UpdateDataTest : BaseTest() {
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
    fun `updateData should return success when index is updated`() =
        runTest {
            coEvery { index.updateDocument(any()) } returns true
            coEvery { dataSourceRegistry.updateDataSource(any()) } returns Result.success(Unit)
            val dataSource = SQLDataSource(
                identifier = RandomPrimitivesFactory.genRandomString(),
                databaseType = SQLDataSource.DatabaseType.H2,
                databaseName = dbName,
                username = dbUser,
                password = dbPassword,
                host = "mem",
                query = "SELECT * FROM $dbTable",
            )
            val actual = subject.updateData(dataSource)
            assert(actual.isSuccess)
        }

    @Test
    fun `updateData should return a failed result when the index is not updated`() =
        runTest {
            coEvery { index.updateDocument(any()) } returns false
            coEvery { dataSourceRegistry.updateDataSource(any()) } returns Result.failure(Throwable())
            val dataSource = SQLDataSource(
                identifier = RandomPrimitivesFactory.genRandomString(),
                databaseType = SQLDataSource.DatabaseType.H2,
                databaseName = dbName,
                username = dbUser,
                password = dbPassword,
                host = "mem",
                query = "SELECT * FROM $dbTable",
            )
            val actual = subject.updateData(dataSource)
            assert(actual.isFailure)
        }
}