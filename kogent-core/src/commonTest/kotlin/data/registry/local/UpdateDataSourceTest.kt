package data.registry.local

import com.ralphdugue.kogent.cache.DataSourceRegistryDB
import com.ralphdugue.kogent.data.adapters.registry.LocalDataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import common.BaseTest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.FakeDatasourceFactory
import utils.FakeRegistryFactory
import utils.RandomPrimitivesFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateDataSourceTest : BaseTest() {
    private lateinit var dataSourceRegistryDB: DataSourceRegistryDB
    private lateinit var subject: DataSourceRegistry
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeEach
    override fun setUp() {
        super.setUp()
        dataSourceRegistryDB = FakeRegistryFactory.createFakeLocalRegistry()
        subject = LocalDataSourceRegistry(dataSourceRegistryDB)
    }

    @Test
    fun `updateDataSource should correctly update a data source in the registry`() =
        runTest {
            val dataSource = FakeDatasourceFactory.createAPIDatasource()
            dataSourceRegistryDB.dataSourceRegistryQueries.insert(
                identifier = dataSource.identifier,
                sourceType = dataSource.dataSourceType.toString(),
                content = json.encodeToString(dataSource)
            )
            val expected = dataSource.copy(endpoint = RandomPrimitivesFactory.genRandomString())
            val result = subject.updateDataSource(expected)
            val actual = dataSourceRegistryDB.dataSourceRegistryQueries
                .selectById(dataSource.identifier)
                .executeAsOneOrNull()
                .let { it?.let { it1 -> json.decodeFromString<APIDataSource>(it1.content) } }
            assertTrue(result.isSuccess, "Expected success but got ${result.exceptionOrNull()}")
            assert(actual != null)
            assertEquals(actual, expected)
        }

    @Test
    fun `updateDataSource should return a failure when the data source is not in the registry`() =
        runTest {
            val dataSource = FakeDatasourceFactory.createAPIDatasource()
            val result = subject.updateDataSource(dataSource)
            assert(result.isFailure)
        }
}