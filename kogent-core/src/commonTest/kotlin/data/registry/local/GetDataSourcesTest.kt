package data.registry.local

import com.ralphdugue.kogent.cache.DataSourceRegistryDB
import com.ralphdugue.kogent.data.adapters.registry.LocalDataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import common.BaseTest
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.FakeDataSourceFactory
import utils.FakeRegistryFactory

class GetDataSourcesTest : BaseTest() {
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
    fun `getDataSources should return all the data sources in the registry`() =
        runTest {
            val expected = List(3) { FakeDataSourceFactory.createAPIDatasource() }
                .plus(List(3) { FakeDataSourceFactory.createSQLDatasource() })
            val jobs = mutableListOf<Job>()
            expected.forEach { dataSource ->
                jobs.add(launch {
                    dataSourceRegistryDB.dataSourceRegistryQueries.insert(
                        identifier = dataSource.identifier,
                        sourceType = dataSource.dataSourceType.toString(),
                        content = json.encodeToString(dataSource)
                    )
                })
            }
            jobs.joinAll()
            val actual = subject.getDataSources()
            assert(actual.isSuccess)
            assert(actual.getOrNull() == expected)
        }

    @Test
    fun `getDataSources should return an empty list when no data sources are in the registry`() =
        runTest {
            val actual = subject.getDataSources()
            assert(actual.isSuccess)
            assert(actual.getOrNull()?.isEmpty() == true)
        }
}