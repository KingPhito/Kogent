package indexing.milvus

import com.ralphdugue.kogent.indexing.adapters.MilvusIndex
import com.ralphdugue.kogent.indexing.domain.entities.Document
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreOptions
import common.BaseTest
import io.milvus.v2.client.MilvusClientV2
import io.milvus.v2.service.vector.response.DeleteResp
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import utils.FakeDocumentFactory
import utils.RandomPrimitivesFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DeleteDocumentTest : BaseTest() {
    @MockK
    private lateinit var clientV2: MilvusClientV2
    private lateinit var subject: MilvusIndex

    @BeforeTest
    override fun setUp() {
        super.setUp()
        subject =
            MilvusIndex(
                config =
                    VectorStoreConfig(
                        connectionString = "localhost:19530",
                        vectorDatabaseType = VectorStoreOptions.MILVUS,
                    ),
                client = clientV2,
            )
    }

    @AfterTest
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun `deleteDocument should return true when document is deleted successfully`() =
        runTest {
            val document = FakeDocumentFactory.createRandomSQLDocument()
            val mockResponse = DeleteResp.builder().deleteCnt(1).build()
            every { clientV2.delete(any()) } returns mockResponse
            val result = subject.deleteDocument(document)
            assertEquals(true, result)
        }

    @Test
    fun `deleteDocument should return false when document is not deleted successfully`() =
        runTest {
            val document = FakeDocumentFactory.createRandomSQLDocument()
            val mockResponse = DeleteResp.builder().deleteCnt(0).build()
            every { clientV2.delete(any()) } returns mockResponse
            val result = subject.deleteDocument(document)
            assertEquals(false, result)
        }
}
