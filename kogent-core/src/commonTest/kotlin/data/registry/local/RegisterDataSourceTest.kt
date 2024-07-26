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
import utils.FakeDataSourceFactory
import utils.FakeRegistryFactory

class RegisterDataSourceTest : BaseTest() {
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
    fun `registerDataSource should correctly store a data source in the registry`() =
        runTest {
            val dataSource = FakeDataSourceFactory.createAPIDatasource()
            val actual = subject.registerDataSource(dataSource)
            val expected = dataSourceRegistryDB.dataSourceRegistryQueries
                .selectById(dataSource.identifier)
                .executeAsOneOrNull()
                .let { it?.let { it1 -> json.decodeFromString<APIDataSource>(it1.content) } }
            assert(actual.isSuccess)
            assert(expected != null)
            assert(expected == dataSource)
        }

    @Test
    fun `registerDataSource should return a failure when the data source is already in the registry`() =
        runTest {
            val dataSource = FakeDataSourceFactory.createAPIDatasource()
            dataSourceRegistryDB.dataSourceRegistryQueries.insert(
                identifier = dataSource.identifier,
                sourceType = dataSource.dataSourceType.toString(),
                content = json.encodeToString(dataSource)
            )
            val actual = subject.registerDataSource(dataSource)
            assert(actual.isFailure)
        }
}