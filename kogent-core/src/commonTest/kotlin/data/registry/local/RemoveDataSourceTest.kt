package data.registry.local

import com.ralphdugue.kogent.cache.DataSourceRegistryDB
import com.ralphdugue.kogent.data.adapters.registry.LocalDataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import common.BaseTest
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.FakeDatasourceFactory
import utils.FakeRegistryFactory
import kotlin.test.assertTrue

class RemoveDataSourceTest : BaseTest() {
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
    fun `removeDataSource should correctly remove a data source from the registry`() =
        runTest {
            val dataSource = FakeDatasourceFactory.createAPIDatasource()
            dataSourceRegistryDB.dataSourceRegistryQueries.insert(
                identifier = dataSource.identifier,
                sourceType = dataSource.dataSourceType.toString(),
                content = json.encodeToString(dataSource)
            )
            val actual = subject.removeDataSource(dataSource.identifier)
            val expected = dataSourceRegistryDB.dataSourceRegistryQueries
                .selectById(dataSource.identifier)
                .executeAsOneOrNull()
            assertTrue(actual.isSuccess, "Expected success but got ${actual.exceptionOrNull()}")
            assert(expected == null)
        }

    @Test
    fun `removeDataSource should return a failure when the data source is not in the registry`() =
        runTest {
            val actual = subject.removeDataSource("non-existent-identifier")
            assert(actual.isFailure)
        }
}