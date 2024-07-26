package data.registry.local

import com.ralphdugue.kogent.cache.DataSourceRegistryDB
import com.ralphdugue.kogent.data.adapters.registry.LocalDataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import common.BaseTest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.FakeDataSourceFactory
import utils.FakeRegistryFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetDataSourceByIdTest : BaseTest() {
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
    fun `getDataSourceById should return the data source with the given identifier`() =
        runTest {
            val expected = FakeDataSourceFactory.createAPIDatasource()
            dataSourceRegistryDB.dataSourceRegistryQueries.insert(
                identifier = expected.identifier,
                sourceType = expected.dataSourceType.toString(),
                content = json.encodeToString(expected)
            )
            val actual = subject.getDataSourceById(expected.identifier)
            assertTrue(actual.isSuccess, "Expected success but got ${actual.exceptionOrNull()}")
            assertEquals(expected, actual.getOrNull())
        }

    @Test
    fun `getDataSourceById should return a failure when the data source with the given identifier does not exist`() =
        runTest {
            val actual = subject.getDataSourceById("non-existent-identifier")
            assert(actual.isFailure)
        }
}