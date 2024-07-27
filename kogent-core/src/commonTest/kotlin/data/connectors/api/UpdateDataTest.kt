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
import utils.RandomPrimitivesFactory
import kotlin.test.assertTrue

class UpdateDataTest : BaseTest() {
    private lateinit var subject: APIDataConnector
    private lateinit var client: HttpClient

    @MockK
    private lateinit var embeddingModel: EmbeddingModel

    @MockK
    private lateinit var index: Index

    @MockK
    private lateinit var dataSourceRegistry: DataSourceRegistry

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
    fun `updateData should return a failure when a document can't be created`() =
        runTest {
            coEvery { embeddingModel.getEmbedding(any()) } returns Result.failure(Exception("Failed to create document"))
            val apiDataSource = FakeDataSourceFactory.createAPIDatasource()
            val actual = subject.updateData(apiDataSource)
            assertTrue(actual.isFailure, "Expected Result.failure but was ${actual.getOrNull()}")
        }

    @Test
    fun `updateData should return a failure when a document can't be indexed`() =
        runTest {
            coEvery { embeddingModel.getEmbedding(any()) } returns Result.success(RandomPrimitivesFactory.genRandomFloatList())
            coEvery { index.updateDocument(any()) } returns false
            val apiDataSource = FakeDataSourceFactory.createAPIDatasource()
            val actual = subject.updateData(apiDataSource)
            assertTrue(actual.isFailure, "Expected Result.failure but was ${actual.getOrNull()}")
        }

    @Test
    fun `updateData should return success when a document is indexed`() =
        runTest {
            coEvery { embeddingModel.getEmbedding(any()) } returns Result.success(RandomPrimitivesFactory.genRandomFloatList())
            coEvery { index.updateDocument(any()) } returns true
            coEvery { dataSourceRegistry.updateDataSource(any()) } returns Result.success(Unit)
            val apiDataSource = FakeDataSourceFactory.createAPIDatasource()
            val actual = subject.updateData(apiDataSource)
            assertTrue(actual.isSuccess, "Expected Result.success but was ${actual.exceptionOrNull()}")
        }
}