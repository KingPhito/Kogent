package data.connectors.api

import com.ralphdugue.kogent.data.adapters.connectors.KogentAPIDataConnector
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.indexing.domain.entities.Index
import common.BaseTest
import io.ktor.client.*
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.FakeDataSourceFactory
import utils.MockHttpClientFactory
import kotlin.test.assertTrue

class RemoveDataTest : BaseTest() {
    @MockK
    private lateinit var embeddingModel: EmbeddingModel

    @MockK
    private lateinit var index: Index

    @MockK
    private lateinit var dataSourceRegistry: DataSourceRegistry

    private lateinit var subject: APIDataConnector
    private lateinit var client: HttpClient

    @BeforeEach
    override fun setUp() {
        super.setUp()
        client = MockHttpClientFactory.createClient(
            json = "",
            status = HttpStatusCode.OK
        )
        subject = KogentAPIDataConnector(client, embeddingModel, index, dataSourceRegistry)
    }

    @Test
    fun `removeData should return a failure when a document can't be removed`() =
        runTest {
            coEvery { index.deleteDocument(any(), any()) } returns false
            val apiDataSource = FakeDataSourceFactory.createAPIDatasource()
            val actual = subject.removeData(apiDataSource)
            assertTrue(actual.isFailure, "Expected Result.failure but was ${actual.getOrNull()}")
        }

    @Test
    fun `removeData should return a success when a document is removed`() =
        runTest {
            coEvery { index.deleteDocument(any(), any()) } returns true
            coEvery { dataSourceRegistry.removeDataSource(any()) } returns Result.success(Unit)
            val apiDataSource = FakeDataSourceFactory.createAPIDatasource()
            val actual = subject.removeData(apiDataSource)
            assertTrue(actual.isSuccess, "Expected Result.success but was ${actual.exceptionOrNull()}")
        }
}